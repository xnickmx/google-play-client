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

import com.faceture.google.play.PlaySession;
import junit.framework.TestCase;

/**
 * Tests the PlaySession
 */
public class PlaySessionTest extends TestCase {

    // class under test
    PlaySession playSession;

    // params
    String xtCookie = "xtCookie";
    String sjsaidCookie = "sjsaidCookie";
    String authToken = "authToken";

    public void setUp() throws Exception {
        super.setUp();

        playSession = new PlaySession(xtCookie, sjsaidCookie, authToken);
    }

    public void testConsFailsDueToNullXtCookie() {
        try {
            playSession = new PlaySession(null, sjsaidCookie, authToken);
            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testConsFailsDueToEmptyXtCookie() {
        try {
            playSession = new PlaySession("", sjsaidCookie, authToken);
            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testConsFailsDueToNullSjsaidCookie() {
        try {
            playSession = new PlaySession(xtCookie, null, authToken);
            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testConsFailsDueToEmptySjsaidCookie() {
        try {
            playSession = new PlaySession(xtCookie, "", authToken);
            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testConsFailsDueToNullAuthToken() {
        try {
            playSession = new PlaySession(xtCookie, sjsaidCookie, null);
            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testConsFailsDueToEmptyAuthToken() {
        try {
            playSession = new PlaySession(xtCookie, sjsaidCookie, "");
            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testGetGtCookieHappyPath() {
        assertEquals(xtCookie, playSession.getXtCookie());
    }

    public void testGetSjsaidCookieHappyPath() {
        assertEquals(sjsaidCookie, playSession.getSjsaidCookie());
    }

    public void testGetAuthTokenHappyPath() {
        assertEquals(authToken, playSession.getAuthToken());
    }
}
