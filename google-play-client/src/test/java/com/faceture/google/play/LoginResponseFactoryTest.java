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
 * Tests the LoginResponseFactory
 */
public class LoginResponseFactoryTest extends TestCase {

    // class under test
    LoginResponseFactory loginResponseFactory;

    // params
    PlaySession playSession;

    public void setUp() throws Exception {
        super.setUp();

        // mock params
        playSession = mock(PlaySession.class);

        // create test object
        loginResponseFactory = new LoginResponseFactory();
    }

    public void testCreateFailsDueToNullLoginResult() {
        try {
            loginResponseFactory.create(null, playSession);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testCreateSucceedsWithBadCredentialsAndNullSession() {
        assertNotNull(loginResponseFactory.create(LoginResult.BAD_CREDENTIALS, null));
    }

    public void testCreateSucceedsWithFailureAndNullSession() {
        assertNotNull(loginResponseFactory.create(LoginResult.FAILURE, null));
    }

    public void testCreateFailsDueToBadCredentialsAndSession() {
        try {
            loginResponseFactory.create(LoginResult.BAD_CREDENTIALS, playSession);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testCreateFailsDueToFailureAndSession() {
        try {
            loginResponseFactory.create(LoginResult.FAILURE, playSession);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testCreateHappyPath() {
        LoginResult loginResult = LoginResult.SUCCESS;
        LoginResponse loginResponse = loginResponseFactory.create(loginResult, playSession);
        assertNotNull(loginResponse);
        assertEquals(loginResult, loginResponse.getLoginResult());
        assertEquals(playSession, loginResponse.getPlaySession());
    }
}
