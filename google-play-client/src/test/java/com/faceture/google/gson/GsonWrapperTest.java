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

package com.faceture.google.gson;

import com.faceture.google.play.domain.SearchRequest;
import com.google.gson.Gson;
import junit.framework.TestCase;

/**
 * Tests the GsonWrapper
 */
public class GsonWrapperTest extends TestCase {

    // class under test
    GsonWrapper gsonWrapper;

    // dependencies
    Gson gson;

    // params
    final String query = "query";
    final SearchRequest searchRequest = new SearchRequest(query);
    final String jsonSearchRequest = "{\"q\":\"" + query + "\"}";


    public void setUp() throws Exception {
        super.setUp();

        // use real Gson -- it can't be mocked with Mockito
        gson = new Gson();

        gsonWrapper = new GsonWrapper(gson);
    }

    public void testConsFailsDueToNullGson() {
        try {
            gsonWrapper = new GsonWrapper(null);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testToJsonFailsDueToNullObj() {
        try {
            gsonWrapper.toJson(null);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testToJsonHappyPath() {
        String result = gsonWrapper.toJson(searchRequest);
        assertNotNull(result);
        assertEquals(jsonSearchRequest, result);
    }

    public void testFromJsonFailsDueToNullJson() {
        try {
            gsonWrapper.fromJson(null, SearchRequest.class);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testFromJsonFailsDueToEmptyJson() {
        try {
            gsonWrapper.fromJson("", SearchRequest.class);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testFromJsonFailsDueToNullClass() {
        try {
            gsonWrapper.fromJson(jsonSearchRequest, null);

            fail("should have thrown exception");
        }
        catch (IllegalArgumentException e) {}
    }

    public void testFromJsonHappyPath() {

        assertEquals(searchRequest, gsonWrapper.fromJson(jsonSearchRequest, SearchRequest.class));
    }
}
