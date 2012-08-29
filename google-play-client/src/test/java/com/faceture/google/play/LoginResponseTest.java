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

import junit.framework.TestCase;
import static org.mockito.Mockito.*;

/**
 * Tests the LoginResponse
 */
public class LoginResponseTest extends TestCase {

    // class under test
    LoginResponse loginResponse;

    // dependencies
    LoginResult loginResult = LoginResult.SUCCESS;
    PlaySession playSession;

    public void setUp() throws Exception {
        super.setUp();

        // mock dependencies
        playSession = mock(PlaySession.class);

        loginResponse = new LoginResponse(loginResult, playSession);
    }

    public void testConsFailsDueToNullLoginResult() {
        try {
            loginResponse = new LoginResponse(null, playSession);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testConsSucceedsWithBadCredentialsAndNullSession() {
        loginResponse = new LoginResponse(LoginResult.BAD_CREDENTIALS, null);
    }

    public void testConsSucceedsWithFailureAndNullSession() {
        loginResponse = new LoginResponse(LoginResult.FAILURE, null);
    }

    public void testConsFailsDueToBadCredentialsAndSession() {
        try {
            loginResponse = new LoginResponse(LoginResult.BAD_CREDENTIALS, playSession);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testConsFailsDueToFailureAndSession() {
        try {
            loginResponse = new LoginResponse(LoginResult.FAILURE, playSession);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testHappyPath() {
        assertEquals(loginResult, loginResponse.getLoginResult());
        assertEquals(playSession, loginResponse.getPlaySession());
    }
}
