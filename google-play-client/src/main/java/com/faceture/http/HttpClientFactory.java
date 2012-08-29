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

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.io.UnsupportedEncodingException;

/**
 * Creates HttpClient-related objects
 */
public class HttpClientFactory {

    public DefaultHttpClient createHttpClient() {
        return new DefaultHttpClient();
    }

    public HttpPost createHttpPost() {
        return new HttpPost();
    }

    public HttpGet createHttpGet() {
        return new HttpGet();
    }

    public MultipartEntity createMultipartEntity() {
        return new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
    }

    public StringBody createStringBody(String string) throws UnsupportedEncodingException {
        if (null == string || string.isEmpty()) {
            throw new IllegalArgumentException("string is null or empty");
        }

        return new StringBody(string);
    }
}
