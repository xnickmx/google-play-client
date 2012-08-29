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

import junit.framework.TestCase;

import java.io.UnsupportedEncodingException;

/**
 * tests the HttpClientFactory
 */
public class HttpClientFactoryTest extends TestCase {

    // class under test
    HttpClientFactory httpClientFactory;

    public void setUp() throws Exception {
        super.setUp();

        httpClientFactory = new HttpClientFactory();
    }

    public void testCreateHttpClientHappyPath() {
        assertNotNull(httpClientFactory.createHttpClient());
    }

    public void testCreateHttpPostHappyPath() {
        assertNotNull(httpClientFactory.createHttpPost());
    }

    public void testCreateHttpGetHappyPath() {
        assertNotNull(httpClientFactory.createHttpGet());
    }

    public void testCreateMultipartEntity() {
        assertNotNull(httpClientFactory.createMultipartEntity());
    }

    public void testCreateStringBodyFailsDueToNullString() throws UnsupportedEncodingException {
        try {
            httpClientFactory.createStringBody(null);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testCreateStringBodyFailsDueToEmptyString() throws UnsupportedEncodingException {
        try {
            httpClientFactory.createStringBody("");

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testCreateStringBodyHappyPath() throws UnsupportedEncodingException {
        assertNotNull(httpClientFactory.createStringBody("string"));
    }

}
