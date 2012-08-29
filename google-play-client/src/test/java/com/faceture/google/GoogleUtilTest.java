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

package com.faceture.google;

import com.faceture.google.play.Const;
import junit.framework.TestCase;

/**
 * Tests the GoogleUtil
 */
public class GoogleUtilTest extends TestCase {

    // class under test
    GoogleUtil googleUtil;

    public void setUp() throws Exception {
        super.setUp();

        // create the test object
        googleUtil = new GoogleUtil();
    }

    public void testCreateAuthHeaderValueFailsDueToNullAuthToken() {
        try {
            googleUtil.createAuthHeaderValue(null);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testCreateAuthHeaderValueFailsDueToEmptyAuthToken() {
        try {
            googleUtil.createAuthHeaderValue("");

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testCreateAuthHeaderValueHappyPath() {
        String authToken = "authToken";

        String headerValue = googleUtil.createAuthHeaderValue(authToken);
        assertNotNull(headerValue);
        assertTrue(headerValue.contains(GoogleConst.AUTH_HEADER_START));
        assertTrue(headerValue.contains(authToken));
    }

    public void testGetAuthTokenFromLoginResponseFailsDueToNullLoginResponseBody() {
        try {
            googleUtil.getAuthTokenFromLoginResponse(null);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testGetAuthTokenFromLoginResponseFailsDueToEmptyLoginResponseBody() {
        try {
            googleUtil.getAuthTokenFromLoginResponse("");

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testGetAuthTokenFromLoginResponseHappyPath() {
        String authToken = "autoToken";
        String loginResponseBody = "Foo=bar\n" + Const.GOOLE_LOGIN_AUTH + authToken + "\n";

        assertEquals(authToken, googleUtil.getAuthTokenFromLoginResponse(loginResponseBody));
    }
}
