/*
 * Copyright (c) 2012. Faceture Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.faceture.google.play;

import com.faceture.google.GoogleUtil;
import com.faceture.google.gson.GsonWrapper;
import com.faceture.google.play.domain.*;
import com.faceture.rest.RestClient;
import com.faceture.rest.RestResponse;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Client for using the Google Play REST API
 */
public class PlayClient {

    // dependencies
    private RestClient restClient;
    private PlaySessionFactory playSessionFactory;
    private GoogleUtil googleUtil;
    private GsonWrapper gsonWrapper;
    private PlayDomainFactory playDomainFactory;
    private LoginResponseFactory loginResponseFactory;

    public PlayClient(RestClient restClient, PlaySessionFactory playSessionFactory, GoogleUtil googleUtil,
                      GsonWrapper gsonWrapper, PlayDomainFactory playDomainFactory,
                      LoginResponseFactory loginResponseFactory)
    {
        if (null == restClient) {
            throw new IllegalArgumentException("restClient is null");
        }
        if (null == playSessionFactory) {
            throw new IllegalArgumentException("playSessionFactory is null");
        }
        if (null == googleUtil) {
            throw new IllegalArgumentException("googleUtil is null");
        }
        if (null == gsonWrapper) {
            throw new IllegalArgumentException("gsonWrapper is null");
        }
        if (null == playDomainFactory) {
            throw new IllegalArgumentException("playDomainFactory is null");
        }
        if (null == loginResponseFactory) {
            throw new IllegalArgumentException("loginResponseFactory is null");
        }
        this.restClient = restClient;
        this.playSessionFactory = playSessionFactory;
        this.googleUtil = googleUtil;
        this.gsonWrapper = gsonWrapper;
        this.playDomainFactory = playDomainFactory;
        this.loginResponseFactory = loginResponseFactory;
    }

    public LoginResponse login(String emailAddress, String password) throws IOException, URISyntaxException {
        if (null == emailAddress || emailAddress.isEmpty()) {
            throw new IllegalArgumentException("emailAddress is null or empty");
        }
        if (null == password || password.isEmpty()) {
            throw new IllegalArgumentException("password is null or empty");
        }

        // create the login form
        Map<String, String> loginForm = new HashMap<String, String>();
        loginForm.put(FormFieldConst.SERVICE_NAME, FormFieldConst.SERVICE_VALUE);
        loginForm.put(FormFieldConst.EMAIL_NAME, emailAddress);
        loginForm.put(FormFieldConst.PASSWORD_NAME, password);

        // do the login to Google
        RestResponse googleLoginResponse = restClient.doPost(Const.USE_HTTPS, HostName.GOOGLE, Path.GOOGLE_LOGIN,
                null, null, null, loginForm);

        // make sure the login succeeded
        int googleLoginSC = googleLoginResponse.getStatusCode();
        if (googleLoginSC != HttpStatus.SC_OK) {
            if (HttpStatus.SC_FORBIDDEN == googleLoginSC) {
                return loginResponseFactory.create(LoginResult.BAD_CREDENTIALS, null);
            }
            else {
                return loginResponseFactory.create(LoginResult.FAILURE, null);
            }
        }

        // the login will pass out auth info we need in the response body
        String googleLoginResponseBody = googleLoginResponse.getBody();
        String googleAuthToken = googleUtil.getAuthTokenFromLoginResponse(googleLoginResponseBody);

        // setup the play login query params
        Map<String, String> playLoginQueryParams = new HashMap<String, String>();
        playLoginQueryParams.put(QueryParamConst.HL_NAME, QueryParamConst.HL_VALUE);
        playLoginQueryParams.put(QueryParamConst.U_NAME, QueryParamConst.U_VALUE);

        // setup the play login headers
        String playAuthHeaderValue = googleUtil.createAuthHeaderValue(googleAuthToken);
        Map<String, String> playLoginRequestHeaders = new HashMap<String, String>();
        playLoginRequestHeaders.put(HeaderName.AUTHORIZATION, playAuthHeaderValue);

        // do the login to Play
        RestResponse playLoginResponse = restClient.doPost(Const.USE_HTTPS, HostName.PLAY, Path.MUSIC_LOGIN,
                playLoginQueryParams, playLoginRequestHeaders, null, null);

        LoginResponse loginResponse;

        switch (playLoginResponse.getStatusCode()) {
            case HttpStatus.SC_OK:
                // logging in succeeded

                // get the xt and sjsaid cookies
                Map<String, String> playLoginResponseCookies = playLoginResponse.getCookies();
                String xtCookie = playLoginResponseCookies.get(CookieName.XT);
                String sjsaidCookie = playLoginResponseCookies.get(CookieName.SJSAID);

                PlaySession playSession = playSessionFactory.create(xtCookie, sjsaidCookie, googleAuthToken);

                loginResponse = loginResponseFactory.create(LoginResult.SUCCESS, playSession);

                break;

            case HttpStatus.SC_FORBIDDEN:
                // bad credentials
                loginResponse = loginResponseFactory.create(LoginResult.BAD_CREDENTIALS, null);

            break;

            default:
                // some other status code
                loginResponse = loginResponseFactory.create(LoginResult.FAILURE, null);
        }


        return loginResponse;
    }

    public SearchResults search(String query, PlaySession session) throws IOException, URISyntaxException {
        if (null == query || query.isEmpty()) {
            throw new IllegalArgumentException("query is null or empty");
        }
        if (null == session) {
            throw new IllegalArgumentException("session is null");
        }

        // create the URL query params
        Map<String, String> searchQueryParams = new HashMap<String, String>();
        searchQueryParams.put(QueryParamConst.U_NAME, QueryParamConst.U_VALUE);
        searchQueryParams.put(QueryParamConst.XT_NAME, session.getXtCookie());

        // create the HTTP headers
        Map<String, String> searchRequestHeaders = new HashMap<String, String>();
        String authHeader = googleUtil.createAuthHeaderValue(session.getAuthToken());
        searchRequestHeaders.put(HeaderName.AUTHORIZATION, authHeader);

        // create the JSON search request
        SearchRequest searchRequest = playDomainFactory.createSearchRequest(query);
        String searchJson = gsonWrapper.toJson(searchRequest);

        // create the form
        Map<String, String> searchForm = new HashMap<String, String>();
        searchForm.put(FormFieldConst.JSON_NAME, searchJson);

        // do the POST
        RestResponse restResponse = restClient.doPost(Const.USE_HTTPS, HostName.PLAY, Path.MUSIC_SEARCH,
                searchQueryParams, searchRequestHeaders, null, searchForm);

        if (restResponse.getStatusCode() != HttpStatus.SC_OK) {
            throw new IllegalStateException("Bad status: " + restResponse.getStatusCode() + " response body: " +
                    restResponse.getBody());
        }

        // the results will come back in the body
        String jsonResponse = restResponse.getBody();
        SearchResponse searchResponse = gsonWrapper.fromJson(jsonResponse, SearchResponse.class);

        return searchResponse.getResults();
    }

    public URI getPlayURI(String songId, PlaySession playSession) throws IOException, URISyntaxException {
        if (null == songId || songId.isEmpty()) {
            throw new IllegalArgumentException("query is null or empty");
        }
        if (null == playSession) {
            throw new IllegalArgumentException("playSession is null");
        }

        // setup the HTTP query params
        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put(QueryParamConst.U_NAME, QueryParamConst.U_VALUE);
        queryParams.put(QueryParamConst.PT_NAME, QueryParamConst.PT_VALUE);
        queryParams.put(QueryParamConst.SONG_ID_NAME, songId);

        // setup the request headers
        String authHeader = googleUtil.createAuthHeaderValue(playSession.getAuthToken());

        Map<String, String> requestHeaders = new HashMap<String, String>();
        requestHeaders.put(HeaderName.AUTHORIZATION, authHeader);

        // setup the cookies
        Map<String, String> cookies = new HashMap<String, String>();
        cookies.put(CookieName.SJSAID, playSession.getSjsaidCookie());
        cookies.put(CookieName.XT, playSession.getXtCookie());

        // do the call
        RestResponse restResponse = restClient.doGet(Const.USE_HTTPS, HostName.PLAY, Path.MUSIC_PLAY,
                queryParams, requestHeaders, cookies);

        if (restResponse.getStatusCode() != HttpStatus.SC_OK) {
            throw new IllegalStateException("Bad status: " + restResponse.getStatusCode() + " response body: " +
                    restResponse.getBody());
        }

        // the response will be JSON in the body
        String jsonResponse = restResponse.getBody();
        StreamingUrl streamingUrl = gsonWrapper.fromJson(jsonResponse, StreamingUrl.class);

        return new URI(streamingUrl.getUrl());
    }
}
