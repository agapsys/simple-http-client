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

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * Represents an HTTP client.
 * A Client can keep connection state managing cookies like a normal web browser does
 */
public class HttpClient {
    private final List<HttpHeader> defaultHeaders = new LinkedList<>();
    private final boolean enableRedirects;

    private CloseableHttpClient wrappedClient = null;

    public HttpClient() {
        this(false);
    }

    public HttpClient(boolean enableRedirects) {
        this.enableRedirects = enableRedirects;
    }

    public final boolean areRedirectsEnabled() {
        return enableRedirects;
    }

    /**
     * Adds given default header to be sent on each request using this client.
     * @param name header name
     * @param value header value
     */
    public void addDefaultHeader(String name, String value) {
        defaultHeaders.add(new HttpHeader(name, value));
    }

    /**
     * Adds default headers to be sent on each request using this client.
     * @param headers Headers to be added
     * @throws IllegalArgumentException if no headers are passed or a null header is given
     */
    public void addDefaultHeaders(HttpHeader...headers) throws IllegalArgumentException {
        if (headers.length == 0)
            throw new IllegalArgumentException("Empty headers");

        int i = 0;
        for (HttpHeader header : headers) {
            if (header == null)
                throw new IllegalArgumentException("Null header on index " + i);

            addDefaultHeader(header.getName(), header.getValue());
            i++;
        }
    }

    /** Removes all registered default headers. */
    public void clearDefaultHeaders() {
        defaultHeaders.clear();
    }

    /**
     * Returns default headers associated with this client.
     * @return default headers associated with this client.
     */
    public List<HttpHeader> getDefaultHeaders() {
        return defaultHeaders;
    }

    /**
     * Releases all resources associated with this client.
     * Calling this method before
     * @throws java.io.IOException if there was an I/O error closing this client
     */
    public void close() throws IOException {
        if (wrappedClient == null) throw new IOException("Client was not initialized");

        wrappedClient.close();
        wrappedClient = null;
    }

    /**
     * Returns wrapped client instance.
     * @return wrapped client instance.
     */
    protected CloseableHttpClient getWrappedClient() {
        if (wrappedClient == null) {
            HttpClientBuilder builder = HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.custom().setRedirectsEnabled(areRedirectsEnabled()).build());
            builder.setDefaultHeaders(getDefaultHeaders());
            wrappedClient = builder.build();
        }

        return wrappedClient;
    }
}
