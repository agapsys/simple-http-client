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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import org.apache.http.client.methods.HttpRequestBase;

/** Represents an HTTP request. */
public abstract class HttpRequest {
	private String uri;
	private HttpRequestBase coreRequest = null;
		
	/** 
	 * Constructor.
	 * @param uri request URI
	 * @param uriParams parameters passed to format the string passed as URI
	 */
	public HttpRequest(String uri, Object...uriParams) {
		if (uri == null || uri.trim().isEmpty()) 
			throw new IllegalArgumentException("Null/Empty URI");
		
		for (int i = 0; i < uriParams.length; i++) {
			
			if (uriParams[i] instanceof String) {
				try {
					uriParams[i] = URLEncoder.encode((String) uriParams[i], "utf-8");
				} catch (UnsupportedEncodingException ex) {
					throw new RuntimeException(ex);
				}
			}
		}
		uri = String.format(uri, uriParams);
		this.uri = uri;
	}
	
	public HttpRequest() {}
	
	@Override
	public String toString() {
		boolean coreRequestWasNull = (coreRequest == null);
		String str = String.format("%s %s", getMethod(), getUri());
		if (coreRequestWasNull)
			coreRequest = null;
		
		return str;
	}
	
	/**
	 * Returns the URI passed in constructor
	 * @return Request URI. */
	public String getUri() {
		return uri;
	}
	
	/**
	 * Sets request URI
	 * @param uri 
	 */
	public void setUri(String uri) {
		if (uri == null || uri.trim().isEmpty()) throw new IllegalArgumentException("Null/Empty URI");
		this.uri = uri;
	}
	
	/**
	 * Returns wrapped request instance
	 * @param uri request URI
	 * @return wrapped instance.
	 */
	protected abstract HttpRequestBase getCoreRequest(String uri);
	
	/** 
	 * Called before the request is sent.
	 * Default implementation does nothing.
	 */
	protected void beforeSend() {}
	
	/**
	 * Returns wrapped request instance.
	 * @return wrapped request instance.
	 */
	protected final HttpRequestBase getCoreRequest() {
		if (coreRequest == null)
			coreRequest = getCoreRequest(uri);
		
		return coreRequest;
	}
	
	/**
	 * Returns HTTP method associated with this request
	 * @return The name of HTTP method associated with this request
	 */
	public String getMethod() {
		return getCoreRequest().getMethod();
	}
	
	/**
	 * Adds given header to the request
	 * @param name header name
	 * @param value header value
	 */
	public void addHeader(String name, String value) {
		getCoreRequest().addHeader(name, value);
	}

	/**
	 *  Adds headers to this request
	 * @param headers Headers to be added
	 */
	public void addHeaders(HttpHeader...headers) {
		if (headers.length == 0)
			throw new IllegalArgumentException("Empty headers");
		
		int i = 0;
		for (HttpHeader header : headers) {
			if (header == null)
				throw new IllegalArgumentException("Null header on index " + i);
			
			addHeader(header.getName(), header.getValue());
			i++;
		}
	}

	/**
	 * Returns all headers associated to this request. 
	 * @return all headers associated to this request. 
	 */
	public List<HttpHeader> getHeaders() {
		List<HttpHeader> headerList = new LinkedList<>();
		
		org.apache.http.Header[] wrappedHeaders = getCoreRequest().getAllHeaders();
		for (org.apache.http.Header wrappedHeader : wrappedHeaders) {
			headerList.add(new HttpHeader(wrappedHeader.getName(), wrappedHeader.getValue()));
		}
		
		return headerList;
	}

	/** Removes all headers. */
	public void clearHeaders() {
		org.apache.http.Header[] wrappedHeaders = getCoreRequest().getAllHeaders();
		for (org.apache.http.Header wrappedHeader : wrappedHeaders) {
			getCoreRequest().removeHeader(wrappedHeader);
		}
	}

	/**
	 * Executes this request
	 * @param client client used in this request
	 * @return server response
	 * @throws IOException if there was an I/O error while executing the request. 
	 */
	public HttpResponse execute(HttpClient client) throws IOException {
		if (client == null)
			throw new IllegalArgumentException("Null client");
				
		List<HttpHeader> defaultHeaders = client.getDefaultHeaders();
		List<HttpHeader> requestHeaders = getHeaders();
		
		if (!defaultHeaders.isEmpty()) {
			// Adds default headers...
			clearHeaders();
			
			for (HttpHeader header : defaultHeaders) {
				addHeaders(header);
			}
			
			for (HttpHeader header : requestHeaders) {
				addHeaders(header);
			}
		}
		
		beforeSend();
		HttpResponse resp = new HttpResponse(client.getWrappedClient().execute(getCoreRequest()));
		
		if (!defaultHeaders.isEmpty()) {
			// Restore request headers...
			clearHeaders();
			
			for (HttpHeader header : requestHeaders) {
				addHeaders(header);
			}
		}
		
		return resp;
	}
}
