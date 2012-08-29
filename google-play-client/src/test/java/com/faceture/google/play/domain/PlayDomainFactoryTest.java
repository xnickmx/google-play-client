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

package com.faceture.google.play.domain;

import junit.framework.TestCase;

/**
 * Tests the PlayDomainFactory
 */
public class PlayDomainFactoryTest extends TestCase {

    // class under test
    PlayDomainFactory playDomainFactory;

    public void setUp() throws Exception {
        super.setUp();

        playDomainFactory = new PlayDomainFactory();
    }

    public void testCreateSearchRequestFailsDueToNullQ() {
        try {
            playDomainFactory.createSearchRequest(null);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testCreateSearchRequestFailsDueToEmptyQ() {
        try {
            playDomainFactory.createSearchRequest("");

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testCreateSearchRequestHappyPath() {
        assertNotNull(playDomainFactory.createSearchRequest("query"));
    }

}
