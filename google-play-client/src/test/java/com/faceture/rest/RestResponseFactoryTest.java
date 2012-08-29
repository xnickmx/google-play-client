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

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Tests the RestResponseFactory
 */
public class RestResponseFactoryTest extends TestCase {

    // class under test
    RestResponseFactory restResponseFactory;

    // params
    int statusCode = 100;
    Map<String, String> cookies = new HashMap<String, String>();
    String cookieName = "cookieName";
    String cookieValue = "cookieValue";

    Map<String, String> headers = new HashMap<String, String>();
    String headerName = "headerName";
    String headerValue = "headerValue";

    String body = "body";

    public RestResponseFactoryTest() {
        cookies.put(cookieName, cookieValue);
        headers.put(headerName, headerValue);
    }

    public void setUp() throws Exception {
        super.setUp();

        // create the test object
        restResponseFactory = new RestResponseFactory();
    }

    public void testCreateHappyPath() {
        RestResponse restResponse = restResponseFactory.create(statusCode, cookies, headers, body);

        assertNotNull(restResponse);
        assertEquals(statusCode, restResponse.getStatusCode());
        assertEquals(cookies, restResponse.getCookies());
        assertEquals(headers, restResponse.getHeaders());
        assertEquals(body, restResponse.getBody());
    }


}
