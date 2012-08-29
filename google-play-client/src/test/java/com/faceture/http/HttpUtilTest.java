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

package com.faceture.http;

import com.faceture.google.play.HeaderName;
import junit.framework.TestCase;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * Tests the HttpUtil
 */
public class HttpUtilTest extends TestCase {

    // class under test HttpUtil
    HttpUtil httpUtil;

    // dependencies
    HttpClientFactory httpClientFactory;

    // params
    HttpResponse httpResponse;
    Map<String, String> queryParams = new HashMap<String, String>();
    String queryParamName1 = "queryParamName1";
    String queryParamValue1 = "queryParamValue1";
    String queryParamName2 = "queryParamName2";
    String queryParamValue2 = "queryParamValue2";
    HttpRequestBase httpRequest;
    boolean https = true;
    String hostName = "hostName";
    String path = "/path";
    String queryString = "queryString";
    Map<String, String> headers = new HashMap<String, String>();
    String headerName = "headerName";
    String headerValue = "headerValue";
    HttpPost httpPost;
    Map<String, String> formData = new HashMap<String, String>();
    String fieldName = "fieldName";
    String fieldValue = "fieldValue";
    Map<String, String> cookies = new HashMap<String, String>();
    String cookieName = "cookieName";
    String cookieValue = "cookieValue";
    DefaultHttpClient defaultHttpClient;
    HttpClient httpClient;

    public HttpUtilTest() {
        queryParams.put(queryParamName1, queryParamValue1);
        queryParams.put(queryParamName2, queryParamValue2);
        headers.put(headerName, headerValue);
        formData.put(fieldName, fieldValue);
        cookies.put(cookieName, cookieValue);
    }

    public void setUp() throws Exception {
        super.setUp();

        // mock dependencies
        httpClientFactory = mock(HttpClientFactory.class);

        // mock params
        httpResponse = mock(HttpResponse.class);
        httpRequest = mock(HttpRequestBase.class);
        httpPost = mock(HttpPost.class);
        defaultHttpClient = mock(DefaultHttpClient.class);
        httpClient = mock(HttpClient.class);

        // create test object
        httpUtil = new HttpUtil(httpClientFactory);
    }

    public void testConsFailsDueToNullHttpClientFactory() {
        try {
            httpUtil = new HttpUtil(null);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testGetResponseStringFailsDueToNullHttpResponse() throws IOException {
        try {
            httpUtil.getResponseString(null);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testGetResponseStringHappyPath() throws IOException {
        HttpEntity entity = mock(HttpEntity.class);
        when(httpResponse.getEntity()).thenReturn(entity);

        httpUtil.getResponseString(httpResponse);
    }

    public void testGetQueryStringFailsDueToNullQueryParams() {
        try {
            httpUtil.getQueryString(null);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testGetQueryStringFailsDueToEmptyQueryParams() {
        try {
            Map<String, String> emptyParams = new HashMap<String, String>();
            httpUtil.getQueryString(emptyParams);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testGetQueryStringHappyPath() {
        String queryString = httpUtil.getQueryString(queryParams);
        assertNotNull(queryString);
        assertTrue(!queryString.isEmpty());
        assertTrue(queryString.contains(queryParamName1));
        assertTrue(queryString.contains(queryParamValue1));
        assertTrue(queryString.contains(queryParamName2));
        assertTrue(queryString.contains(queryParamValue2));
    }

    public void testSetUriFailsDueToNullRequest() throws URISyntaxException, MalformedURLException {
        try {
            httpUtil.setUri(null, https, hostName, path, queryString);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testSetUriFailsDueToNullHostName() throws URISyntaxException, MalformedURLException {
        try {
            httpUtil.setUri(httpRequest, https, null, path, queryString);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testSetUriFailsDueToEmptyHostName() throws URISyntaxException, MalformedURLException {
        try {
            httpUtil.setUri(httpRequest, https, "", path, queryString);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testSetUriFailsDueToNullPath() throws URISyntaxException, MalformedURLException {
        try {
            httpUtil.setUri(httpRequest, https, hostName, null, queryString);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testSetUriFailsDueToEmptyPath() throws URISyntaxException, MalformedURLException {
        try {
            httpUtil.setUri(httpRequest, https, hostName, "", queryString);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testSetUriHappyPathWithoutQueryParams() throws URISyntaxException, MalformedURLException {

        // do the call
        httpUtil.setUri(httpRequest, https, hostName, path, null);

        // verify results
        URI uri = new URL(Scheme.HTTPS, hostName, path).toURI();
        verify(httpRequest).setURI(uri);
    }

    public void testSetUriHappyPathWithQueryParams() throws URISyntaxException, MalformedURLException {
        // do the call
        httpUtil.setUri(httpRequest, https, hostName, path, queryString);

        // verify results
        URI uri = new URL(Scheme.HTTPS, hostName, path + "?" + queryString).toURI();
        verify(httpRequest).setURI(uri);
    }

    public void testSetHeadersFailsDueToNullRequest() {
        try {
            httpUtil.setHeaders(null, headers);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testSetHeadersFailsDueToNullHeaders() {
        try {
            httpUtil.setHeaders(httpRequest, null);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testSetHeadersFailsDueToEmptyHeaders() {
        try {
            Map<String, String> emptyHeaders = new HashMap<String, String>();
            httpUtil.setHeaders(httpRequest, emptyHeaders);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testSetHeadersHappyPath() {
        // do the call
        httpUtil.setHeaders(httpRequest, headers);

        // verify results
        verify(httpRequest).setHeader(headerName, headerValue);
    }

    public void testSetFormDataFailsDueToNullPost() throws UnsupportedEncodingException {
        try {
            httpUtil.setFormData(null, formData);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testSetFormDataFailsDueToNullFormData() throws UnsupportedEncodingException {
        try {
            Map<String, String> emptyHeaders = new HashMap<String, String>();
            httpUtil.setFormData(httpPost, emptyHeaders);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testSetFormDataFailsDueToEmptyFormData() throws UnsupportedEncodingException {
        try {
            Map<String, String> emptyForm = new HashMap<String, String>();
            httpUtil.setFormData(httpPost, emptyForm);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testSetFormDataHappyPath() throws UnsupportedEncodingException {

        MultipartEntity multipartEntity = mock(MultipartEntity.class);
        when(httpClientFactory.createMultipartEntity()).thenReturn(multipartEntity);
        StringBody stringBody = mock(StringBody.class);
        when(httpClientFactory.createStringBody(fieldValue)).thenReturn(stringBody);

        // do the call
        httpUtil.setFormData(httpPost, formData);

        verify(multipartEntity).addPart(fieldName, stringBody);
        verify(httpPost).setEntity(multipartEntity);
    }

    public void testSetCookiesFailsDueToNullHttpClient() {
        try {
            httpUtil.setCookies(null, cookies);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testSetCookiesFailsDueToNullCookies() {
        try {
            httpUtil.setCookies(httpRequest, null);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testSetCookiesFailsDueToEmptyCookies() {
        try {
            Map<String, String> emptyCookies = new HashMap<String, String>();
            httpUtil.setCookies(httpRequest, emptyCookies);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testSetCookiesHappyPath() {
        // mock interactions
        HttpParams httpParams = mock(HttpParams.class);
        when(httpRequest.getParams()).thenReturn(httpParams);

        // do the call
        httpUtil.setCookies(httpRequest, cookies);

        // verify interactions
        verify(httpRequest).addHeader(eq(HeaderName.COOKIE), anyString());
    }

    public void testGetCookiesFailsDueToNullHttpClient() {
        try {
            httpUtil.getCookies(null);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testGetCookiesHappyPath() {
        // mock interactions
        CookieStore cookieStore = mock(CookieStore.class);
        when(defaultHttpClient.getCookieStore()).thenReturn(cookieStore);
        List<Cookie> httpCookies = new ArrayList<Cookie>();
        Cookie cookie = mock(Cookie.class);
        httpCookies.add(cookie);
        when(cookie.getName()).thenReturn(cookieName);
        when(cookie.getValue()).thenReturn(cookieValue);
        when(cookieStore.getCookies()).thenReturn(httpCookies);

        // do the call
        assertEquals(cookies, httpUtil.getCookies(defaultHttpClient));
    }

    public void testGetHeadersFailsDueToNullResponse() {
        try {
            httpUtil.getHeaders(null);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testGetHeadersHappyPath() {
        // mock interactions
        Header httpHeader = mock(Header.class);
        Header[] httpHeaders =  {httpHeader};
        when(httpResponse.getAllHeaders()).thenReturn(httpHeaders);
        when(httpHeader.getName()).thenReturn(headerName);
        when(httpHeader.getValue()).thenReturn(headerValue);

        // do the call and verify results
        assertEquals(headers, httpUtil.getHeaders(httpResponse));
    }

    public void testExecuteFailsDueToNullHttpClient() throws IOException {
        try {
            httpUtil.execute(null, httpRequest);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testExecuteFailsDueToNullHttpRequest() throws IOException {
        try {
            httpUtil.execute(httpClient, null);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testExecuteHappyPath() throws IOException {
        // mock interaction
        when(httpClient.execute(httpRequest)).thenReturn(httpResponse);

        // do the call
        assertEquals(httpResponse, httpUtil.execute(httpClient, httpRequest));

        verify(httpClient).execute(httpRequest);
    }
}
