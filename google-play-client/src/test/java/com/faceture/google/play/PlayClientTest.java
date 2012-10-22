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
import junit.framework.TestCase;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * Tests the PlayClient
 */
public class PlayClientTest extends TestCase {

    // the class under test
    PlayClient playClient;

    // dependencies
    RestClient restClient;
    PlaySessionFactory playSessionFactory;
    GoogleUtil googleUtil;
    GsonWrapper gsonWrapper;
    PlayDomainFactory playDomainFactory;
    LoginResponseFactory loginResponseFactory;

    // params
    final String emailAddress = "emailAddress";
    final String password = "password";
    final Map<String, String> loginForm = new HashMap<String, String>();
    final String query = "query";
    PlaySession playSession;
    final String xtValue = "xtValue";
    final String authHeaderValue = "authHeaderValue";
    final String songId = "songId";

    public PlayClientTest() {
        // setup the login form
        loginForm.put(FormFieldConst.SERVICE_NAME, FormFieldConst.SERVICE_VALUE);
        loginForm.put(FormFieldConst.EMAIL_NAME, emailAddress);
        loginForm.put(FormFieldConst.PASSWORD_NAME, password);
    }

    public void setUp() throws Exception {
        super.setUp();

        // mock dependencies
        restClient = mock(RestClient.class);
        playSessionFactory = mock(PlaySessionFactory.class);
        googleUtil = mock(GoogleUtil.class);
        playSession = mock(PlaySession.class);
        gsonWrapper = mock(GsonWrapper.class);
        playDomainFactory = mock(PlayDomainFactory.class);
        loginResponseFactory = mock(LoginResponseFactory.class);

        playClient = new PlayClient(restClient, playSessionFactory, googleUtil, gsonWrapper, playDomainFactory,
                loginResponseFactory);
    }

    public void testConsFailsDueToNullRestClient() {
        try {
            playClient = new PlayClient(null, playSessionFactory, googleUtil, gsonWrapper, playDomainFactory,
                    loginResponseFactory);

            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testConsFailsDueToNullPlaySessionFactory() {
        try {
            playClient = new PlayClient(restClient, null, googleUtil, gsonWrapper, playDomainFactory,
                    loginResponseFactory);

            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testConsFailsDueToNullGoogleUtil() {
        try {
            playClient = new PlayClient(restClient, playSessionFactory, null, gsonWrapper, playDomainFactory,
                    loginResponseFactory);

            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testConsFailsDueToNullGsonWrapper() {
        try {
            playClient = new PlayClient(restClient, playSessionFactory, googleUtil, null, playDomainFactory,
                    loginResponseFactory);

            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testConsFailsDueToNullPlayDomainFactory() {
        try {
            playClient = new PlayClient(restClient, playSessionFactory, googleUtil, gsonWrapper, null,
                    loginResponseFactory);

            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testConsFailsDueToNullLoginResponseFactory() {
        try {
            playClient = new PlayClient(restClient, playSessionFactory, googleUtil, gsonWrapper, playDomainFactory,
                    null);

            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testLoginFailsDueToNullEmailAddress() throws IOException, URISyntaxException {
        try {
            playClient.login(null, password);

            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testLoginFailsDueToEmptyEmailAddress() throws IOException, URISyntaxException {
        try {
            playClient.login("", password);

            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testLoginFailsDueToNullPassword() throws IOException, URISyntaxException {
        try {
            playClient.login(emailAddress, null);

            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testLoginFailsDueToEmptyPassword() throws IOException, URISyntaxException {
        try {
            playClient.login(emailAddress, "");

            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testLoginFailsDueToBadCredentials() throws IOException, URISyntaxException {
        // mock interactions -- assume bad credentials
        RestResponse googleLoginResponse = mock(RestResponse.class);
        when(googleLoginResponse.getStatusCode()).thenReturn(HttpStatus.SC_FORBIDDEN);

        when(restClient.doPost(Const.USE_HTTPS, HostName.GOOGLE, Path.GOOGLE_LOGIN, null, null, null, loginForm))
                .thenReturn(googleLoginResponse);

        LoginResponse loginResponse = mock(LoginResponse.class);
        when(loginResponseFactory.create(LoginResult.BAD_CREDENTIALS, null)).thenReturn(loginResponse);

        // do the call
        assertEquals(loginResponse, playClient.login(emailAddress, password));
    }

    public void testLoginFailsDueToOtherError() throws IOException, URISyntaxException {
        // mock interactions -- assume bad credentials
        RestResponse restResponse = mock(RestResponse.class);
        when(restResponse.getStatusCode()).thenReturn(HttpStatus.SC_INTERNAL_SERVER_ERROR);

        when(restClient.doPost(Const.USE_HTTPS, HostName.GOOGLE, Path.GOOGLE_LOGIN, null, null, null, loginForm))
                .thenReturn(restResponse);

        LoginResponse loginResponse = mock(LoginResponse.class);
        when(loginResponseFactory.create(LoginResult.FAILURE, null)).thenReturn(loginResponse);

        // do the call
        assertEquals(loginResponse, playClient.login(emailAddress, password));
    }

    public void testLoginHappyPath() throws IOException, URISyntaxException {
        RestResponse googleLoginResponse = mock(RestResponse.class);


        // mock the call to login to Google
        when(restClient.doPost(Const.USE_HTTPS, HostName.GOOGLE, Path.GOOGLE_LOGIN, null, null, null, loginForm))
                .thenReturn(googleLoginResponse);

        // the response to the initial login to Google will contain the authorization string in the body
        String authToken = "authToken";
        String googleLoginResponseBody = Const.GOOLE_LOGIN_AUTH + authToken + "\n";
        when(googleLoginResponse.getBody()).thenReturn(googleLoginResponseBody);
        when(googleLoginResponse.getStatusCode()).thenReturn(HttpStatus.SC_OK);

        when(googleUtil.getAuthTokenFromLoginResponse(googleLoginResponseBody)).thenReturn(authToken);

        // setup the query params for the login to Play
        Map<String, String> playLoginQueryParams = new HashMap<String, String>();
        playLoginQueryParams.put(QueryParamConst.HL_NAME, QueryParamConst.HL_VALUE);
        playLoginQueryParams.put(QueryParamConst.U_NAME, QueryParamConst.U_VALUE);

        // setup the request headers for the login to play
        Map<String, String> playLoginRequestHeaders = new HashMap<String, String>();
        when(googleUtil.createAuthHeaderValue(authToken)).thenReturn(authHeaderValue);
        playLoginRequestHeaders.put(HeaderName.AUTHORIZATION, authHeaderValue);

        // the response to logging into play will contain the st and sjsaid cookies
        String sjsaidValue = "sjsaidValue";
        Map<String, String> playLoginResponseCookies = new HashMap<String, String>();
        playLoginResponseCookies.put(CookieName.SJSAID, sjsaidValue);
        playLoginResponseCookies.put(CookieName.XT, xtValue);

        RestResponse playLoginResponse = mock(RestResponse.class);
        when(playLoginResponse.getCookies()).thenReturn(playLoginResponseCookies);
        when(playLoginResponse.getStatusCode()).thenReturn(HttpStatus.SC_OK);

        // mock the login to Play
        when(restClient.doPost(Const.USE_HTTPS, HostName.PLAY, Path.MUSIC_LOGIN, playLoginQueryParams,
                playLoginRequestHeaders, null, null))
                .thenReturn(playLoginResponse);

        when(playSessionFactory.create(xtValue, sjsaidValue, authToken)).thenReturn(playSession);


        LoginResponse loginResponse = mock(LoginResponse.class);
        when(loginResponseFactory.create(LoginResult.SUCCESS, playSession)).thenReturn(loginResponse);

        // do the call
        assertEquals(loginResponse, playClient.login(emailAddress, password));
    }

    public void testSearchFailsDueToNullQuery() throws IOException, URISyntaxException {
        try {
            playClient.search(null, playSession);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testSearchFailsDueToEmptyQuery() throws IOException, URISyntaxException {
        try {
            playClient.search("", playSession);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testSearchFailsDueToNullPlaySession() throws IOException, URISyntaxException {
        try {
            playClient.search(query, null);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    @SuppressWarnings("unchecked")
    public void testSearchHappyPath() throws IOException, URISyntaxException {
        // mock the POST and response
        RestResponse searchRestResponse = mock(RestResponse.class);

        when(restClient.doPost(eq(Const.USE_HTTPS), eq(HostName.PLAY), eq(Path.MUSIC_SEARCH), isA((Map.class)),
                isA(Map.class), (Map<String, String>) isNull(), isA(Map.class))).thenReturn(searchRestResponse);

        String searchResultBody = "searchResultBody";
        when(searchRestResponse.getBody()).thenReturn(searchResultBody);
        when(searchRestResponse.getStatusCode()).thenReturn(HttpStatus.SC_OK);

        SearchResponse searchResponse = mock(SearchResponse.class);
        when(gsonWrapper.fromJson(searchResultBody, SearchResponse.class)).thenReturn(searchResponse);

        SearchResults searchResults = mock(SearchResults.class);
        when(searchResponse.getResults()).thenReturn(searchResults);

        // do the call and compare results
        assertEquals(searchResults, playClient.search(query, playSession));
    }

    public void testGetPlayURIFailsDueToNullSongId() throws IOException, URISyntaxException {
        try {
            playClient.getPlayURI(null, playSession);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testGetPlayURIFailsDueToEmptySongId() throws IOException, URISyntaxException {
        try {
            playClient.getPlayURI("", playSession);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testGetPlayURIFailsDueNullPlaySession() throws IOException, URISyntaxException {
        try {
            playClient.getPlayURI(songId, null);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    @SuppressWarnings("unchecked")
    public void testGetPlayURIHappyPath() throws URISyntaxException, IOException {

        // mock request and response
        RestResponse restResponse = mock(RestResponse.class);
        when(restClient.doGet(eq(Const.USE_HTTPS), eq(HostName.PLAY), eq(Path.MUSIC_PLAY), isA(Map.class),
                isA(Map.class), isA(Map.class))).thenReturn(restResponse);

        when(restResponse.getStatusCode()).thenReturn(HttpStatus.SC_OK);

        String playResponseJson = "playResponseJson";
        when(restResponse.getBody()).thenReturn(playResponseJson);
        StreamingUrl streamingUrl = mock(StreamingUrl.class);
        when(gsonWrapper.fromJson(playResponseJson, StreamingUrl.class)).thenReturn(streamingUrl);

        String url = "http://google.com";
        when(streamingUrl.getUrl()).thenReturn(url);

        URI uri = new URI(url);

        // do the call
        assertEquals(uri, playClient.getPlayURI(songId, playSession));

    }

    public void testLoadAllTracksFailsDueToNullPlaySession() throws IOException, URISyntaxException {
        try {
            playClient.loadAllTracks(null);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testLoadAllPlaylistsFailsDueToNullSession() throws IOException, URISyntaxException {
        try {
            playClient.loadAllPlaylists(null);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    @SuppressWarnings("unchecked")
    public void testLoadAllPlaylistsHappyPath() throws IOException, URISyntaxException {
        // mock interactions
        RestResponse restResponse = mock(RestResponse.class);
        when(restClient.doPost(eq(Const.USE_HTTPS), eq(HostName.PLAY), eq(Path.MUSIC_LOAD_PLAYLIST),
                    isA((Map.class)), isA(Map.class), (Map<String, String>) isNull(), isA(Map.class))).thenReturn(restResponse);

        when(restResponse.getStatusCode()).thenReturn(HttpStatus.SC_OK);

        String responseBody = "responseBody";
        when(restResponse.getBody()).thenReturn(responseBody);

        LoadAllPlaylistsResponse loadAllPlaylistsResponse = mock(LoadAllPlaylistsResponse.class);
        when(gsonWrapper.fromJson(responseBody, LoadAllPlaylistsResponse.class)).thenReturn(loadAllPlaylistsResponse);

        Playlist playlist = mock(Playlist.class);
        Collection<Playlist> playlists = new ArrayList<Playlist>();
        playlists.add(playlist);

        when(loadAllPlaylistsResponse.getPlaylists()).thenReturn(playlists);

        // do the call
        Collection<Playlist> resultPlaylists = playClient.loadAllPlaylists(playSession);

        // verify results
        assertNotNull(resultPlaylists);
        assertFalse(resultPlaylists.isEmpty());
    }

}
