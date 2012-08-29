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
 * Tests the SearchRequest class
 */
public class SearchRequestTest extends TestCase {

    // the class under test
    SearchRequest searchRequest;

    // params
    final String q = "query string";

    public void setUp() throws Exception {
        super.setUp();

        searchRequest = new SearchRequest(q);
    }

    public void testConsFailsDueToNullQ() {
        try {
            searchRequest = new SearchRequest(null);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testConsFailsDueToEmptyQ() {
        try {
            searchRequest = new SearchRequest("");

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testConsHappyPath() {
        assertEquals(q, searchRequest.getQ());
    }

}
