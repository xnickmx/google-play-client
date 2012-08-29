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

package com.faceture.rest;

import com.faceture.http.HttpClientFactory;
import com.faceture.http.HttpUtil;
import junit.framework.TestCase;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests the RestClientUtil
 */
public class RestClientUtilTest extends TestCase {

    // class under test
    RestClientUtil restClientUtil;

    // dependencies
    HttpClientFactory httpClientFactory;
    HttpUtil httpUtil;
    RestResponseFactory restResponseFactory;

    // params
    boolean https = true;
    String hostName = "hostName";
    String path = "/path";
    Map<String, String> queryParams = new HashMap<String, String>();
    String qpName = "qpName";
    String qpValue = "qpValue";
    Map<String, String> httpHeaders = new HashMap<String, String>();
    String headerName = "headerName";
    String headerValue = "headerValue";
    Map<String, String> formFields = new HashMap<String, String>();
    String fieldName = "fieldName";
    String fieldValue = "fieldValue";
    Map<String, String> cookies = new HashMap<String, String>();
    String cookieName = "cookieName";
    String cookieValue = "cookieValue";
    HttpRequestBase httpRequest;

    public RestClientUtilTest() {
        // setup our maps
        queryParams.put(qpName, qpValue);
        httpHeaders.put(headerName, headerValue);
        formFields.put(fieldName, fieldValue);
        cookies.put(cookieName, cookieValue);
    }

    public void setUp() throws Exception {
        super.setUp();

        // mock dependencies
        httpClientFactory = mock(HttpClientFactory.class);
        httpUtil = mock(HttpUtil.class);
        restResponseFactory = mock(RestResponseFactory.class);
        
        // mock params
        httpRequest = mock(HttpRequestBase.class);

        // create test object
        restClientUtil = new RestClientUtil(httpClientFactory, httpUtil, restResponseFactory);
    }
    
    public void testConsFailsDueToNullHttpClientFactory() {
        try {
            restClientUtil = new RestClientUtil(null, httpUtil, restResponseFactory);

            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {}
    }

     public void testConsFailsDueToNullHttpUtil() {
        try {
            restClientUtil = new RestClientUtil(httpClientFactory, null, restResponseFactory);

            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testConsFailsDueToNullRestResponseFactory() {
        try {
            restClientUtil = new RestClientUtil(httpClientFactory, httpUtil, null);

            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testDoRequestFailsDueToNullRequest() throws IOException, URISyntaxException {
        try {
            restClientUtil.doRequest(null, https, hostName, path, queryParams, httpHeaders, cookies);

            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {}
    }

    public void testDoRequestFailsDueToNullHostName() throws IOException, URISyntaxException {
        try {
            restClientUtil.doRequest(httpRequest, https, null, path, queryParams, httpHeaders, cookies);

            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {}
    }

    public void testDoRequestFailsDueToEmptyHostName() throws IOException, URISyntaxException {
        try {
            restClientUtil.doRequest(httpRequest, https, "", path, queryParams, httpHeaders, cookies);

            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {}
    }

    public void testDoRequestFailsDueToNullPath() throws IOException, URISyntaxException {
        try {
            restClientUtil.doRequest(httpRequest, https, hostName, null, queryParams, httpHeaders, cookies);

            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {}
    }

    public void testDoRequestFailsDueToEmptyPath() throws IOException, URISyntaxException {
        try {
            restClientUtil.doRequest(httpRequest, https, hostName, "", queryParams, httpHeaders, cookies);

            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {}
    }

    public void testDoRequestWithFormHappyPath() throws IOException, URISyntaxException {
        // mock interactions
        DefaultHttpClient httpClient = mock(DefaultHttpClient.class);
        when(httpClientFactory.createHttpClient()).thenReturn(httpClient);
        String query = "query";
        when(httpUtil.getQueryString(queryParams)).thenReturn(query);

        HttpResponse httpResponse = mock(HttpResponse.class);

        when(httpUtil.execute(httpClient, httpRequest)).thenReturn(httpResponse);

        String responseBody = "responseBody";
        when(httpUtil.getResponseString(httpResponse)).thenReturn(responseBody);

        int statusCode = HttpStatus.SC_OK;
        StatusLine statusLine = mock(StatusLine.class);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(statusCode);

        when(httpUtil.getCookies(httpClient)).thenReturn(cookies);
        when(httpUtil.getHeaders(httpResponse)).thenReturn(httpHeaders);

        RestResponse restResponse = mock(RestResponse.class);
        when(restResponseFactory.create(statusCode, cookies, httpHeaders, responseBody)).thenReturn(restResponse);

        // do the call
        assertEquals(restResponse, restClientUtil.doRequest(httpRequest, https, hostName, path, queryParams, httpHeaders, cookies));

        // verify interactions
        verify(httpUtil).setUri(httpRequest, https, hostName, path, query);
        verify(httpUtil).setHeaders(httpRequest, httpHeaders);
        verify(httpUtil).setCookies(httpRequest, cookies);
        verify(httpUtil).execute(httpClient, httpRequest);
    }
}
