/*
 * Copyright 2015 Agapsys Tecnologia Ltda-ME.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agapsys.http;

import com.agapsys.http.utils.NameValuePair;
import org.apache.http.HeaderElement;
import org.apache.http.ParseException;
import org.apache.http.message.BasicHeaderElement;

/** Represents a HTTP header. */
public class HttpHeader extends NameValuePair implements org.apache.http.Header {
    private final HeaderElement[] headerElements;

    public HttpHeader(String name, String value) {
        super(name, value);
        this.headerElements = new HeaderElement[] {new BasicHeaderElement(super.getValue(), null)};
    }

    HttpHeader(org.apache.http.Header header) {
        this(header.getName(), header.getValue());
    }

    @Override
    public HeaderElement[] getElements() throws ParseException {
        return headerElements;
    }
}