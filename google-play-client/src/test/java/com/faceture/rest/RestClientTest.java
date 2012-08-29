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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests the RestClient
 */
public class RestClientTest extends TestCase {

    // class under test
    RestClient restClient;

    // dependencies
    HttpClientFactory httpClientFactory;
    HttpUtil httpUtil;
    RestClientUtil restClientUtil;

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

    public RestClientTest() {
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
        restClientUtil = mock(RestClientUtil.class);

        // create test object
        restClient = new RestClient(httpClientFactory, httpUtil, restClientUtil);
    }

    public void testConsFailsDueToNullHttpClientFactory() {
        try {
            restClient = new RestClient(null, httpUtil, restClientUtil);

            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {}
    }

     public void testConsFailsDueToNullHttpUtil() {
        try {
            restClient = new RestClient(httpClientFactory, null, restClientUtil);

            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testConsFailsDueToNullRestClientUtil() {
        try {
            restClient = new RestClient(httpClientFactory, httpUtil, null);

            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testDoPostFailsDueToNullHostName() throws IOException, URISyntaxException {
        try {
            restClient.doPost(https, null, path, queryParams, httpHeaders, cookies, formFields);

            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {}
    }

    public void testDoPostFailsDueToEmptyHostName() throws IOException, URISyntaxException {
        try {
            restClient.doPost(https, "", path, queryParams, httpHeaders, cookies, formFields);

            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {}
    }

    public void testDoPostFailsDueToNullPath() throws IOException, URISyntaxException {
        try {
            restClient.doPost(https, hostName, null, queryParams, httpHeaders, cookies, formFields);

            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {}
    }

    public void testDoPostFailsDueToEmptyPath() throws IOException, URISyntaxException {
        try {
            restClient.doPost(https, hostName, "", queryParams, httpHeaders, cookies, formFields);

            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {}
    }

    public void testDoPostHappyPath() throws IOException, URISyntaxException {
        // mock interactions
        HttpPost httpPost = mock(HttpPost.class);
        when(httpClientFactory.createHttpPost()).thenReturn(httpPost);
        RestResponse restResponse = mock(RestResponse.class);
        when(restClientUtil.doRequest(httpPost, https, hostName, path, queryParams, httpHeaders, cookies))
                .thenReturn(restResponse);

        // do the call
        assertEquals(restResponse, restClient.doPost(https, hostName, path, queryParams, httpHeaders, cookies, formFields));

        // verify interactions
        verify(httpUtil).setFormData(httpPost, formFields);

    }

    public void testDoGetFailsDueToNullHostName() throws IOException, URISyntaxException {
        try {
            restClient.doGet(https, null, path, queryParams, httpHeaders, cookies);

            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {}
    }

    public void testDoGetFailsDueToEmptyHostName() throws IOException, URISyntaxException {
        try {
            restClient.doGet(https, "", path, queryParams, httpHeaders, cookies);

            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {}
    }

    public void testDoGetFailsDueToNullPath() throws IOException, URISyntaxException {
        try {
            restClient.doGet(https, hostName, null, queryParams, httpHeaders, cookies);

            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {}
    }

    public void testDoGetFailsDueToEmptyPath() throws IOException, URISyntaxException {
        try {
            restClient.doGet(https, hostName, "", queryParams, httpHeaders, cookies);

            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {}
    }

    public void testDoGetHappyPath() throws IOException, URISyntaxException {
        HttpGet httpGet = mock(HttpGet.class);
        when(httpClientFactory.createHttpGet()).thenReturn(httpGet);

        RestResponse restResponse = mock(RestResponse.class);

        when(restClientUtil.doRequest(httpGet, https, hostName, path, queryParams, httpHeaders, cookies))
                .thenReturn(restResponse);

        // do the call
        assertEquals(restResponse, restClient.doGet(https, hostName, path, queryParams, httpHeaders, cookies));

    }
}
