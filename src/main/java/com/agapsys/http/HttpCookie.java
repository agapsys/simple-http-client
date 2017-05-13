/*
 * Copyright 2017 Agapsys Tecnologia Ltda-ME.
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
import java.util.regex.Pattern;

public class HttpCookie {

    private static NameValuePair __getEntry(String token, String delimiter) {
        String[] mTokens = token.split(Pattern.quote(delimiter));

        return new NameValuePair(mTokens[0], mTokens.length > 1 ? mTokens[1] : null);
    }

    public String  name;
    public String  value;
    public String  expires;
    public int     maxAge;
    public String  domain;
    public String  path;
    public boolean secure;
    public boolean httpOnly;

    public HttpCookie() {}

    HttpCookie(String headerLine) {
        _setValues(headerLine);
    }

    final void _setValues(String headerLine) {
        String[] tokens = headerLine.split(Pattern.quote(";"));

        int i = 0;
        for (String token : tokens) {
            NameValuePair entry = __getEntry(token, "=");

            if (i == 0) {
                this.name = entry.getName();
                this.value = entry.getValue();
            } else {
                switch (entry.getName().toLowerCase()) {
                    case "expires":
                        this.expires = entry.getValue();
                        break;

                    case "max-age":
                        this.maxAge = Integer.parseInt(entry.getValue());
                        break;

                    case "domain":
                        this.domain = entry.getValue();
                        break;

                    case "path":
                        this.path = entry.getValue();
                        break;

                    case "secure":
                        this.secure = true;
                        break;

                    case "httponly":
                        this.httpOnly = true;
                        break;
                }
            }

            i++;
        }
    }

}
