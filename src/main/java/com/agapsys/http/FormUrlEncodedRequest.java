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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;

public abstract class FormUrlEncodedRequest extends EntityRequest {
	// CLASS SCOPE =============================================================
	public static class FormUrlEncodedPost extends FormUrlEncodedRequest {

		public FormUrlEncodedPost(String uri, String charset) {
			super(uri, charset);
		}

		public FormUrlEncodedPost(String charset) {
			super(charset);
		}

		@Override
		protected HttpRequestBase getCoreRequest(String uri) {
			return new org.apache.http.client.methods.HttpPost(uri);
		}
	}
	
	public static class FormUrlEncodedPut extends FormUrlEncodedRequest {

		public FormUrlEncodedPut(String uri, String charset) {
			super(uri, charset);
		}

		public FormUrlEncodedPut(String charset) {
			super(charset);
		}

		@Override
		protected HttpRequestBase getCoreRequest(String uri) {
			return new org.apache.http.client.methods.HttpPut(uri);
		}
	}
	
	public static class FormUrlEncodedPatch extends FormUrlEncodedRequest {

		public FormUrlEncodedPatch(String uri, String charset) {
			super(uri, charset);
		}

		public FormUrlEncodedPatch(String charset) {
			super(charset);
		}

		@Override
		protected HttpRequestBase getCoreRequest(String uri) {
			return new org.apache.http.client.methods.HttpPatch(uri);
		}
	}
	// =========================================================================
	
	// INSTANCE SCOPE ==========================================================
	private final Map<String, String> params = new LinkedHashMap<>();
	private final String charset;

	public FormUrlEncodedRequest(String uri, String charset) {
		super(uri);
		if (charset == null || charset.trim().isEmpty())
			throw new IllegalArgumentException("Null/Empty charset");
		
		this.charset = charset;
	}
	
	public FormUrlEncodedRequest(String charset) {
		if (charset == null || charset.trim().isEmpty())
			throw new IllegalArgumentException("Null/Empty charset");
		
		this.charset = charset;
	}
	
	public String getCharset() {
		return charset;
	}
	
	public void addParameter(String name, String value) {
		if (name == null || name.trim().isEmpty())
			throw new IllegalArgumentException("Null/Empty name");
		
		if (value == null)
			value = "";
		
		params.put(name, value);
	}
	
	public String getParameter(String name) {
		return params.get(name);
	}

	@Override
	protected HttpEntity getEntity() {
		List<NameValuePair> urlParameters = new ArrayList<>();
		
		for (Map.Entry<String, String> entry : params.entrySet()) {
			urlParameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		
		try {
			return new UrlEncodedFormEntity(urlParameters, charset);
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		}
	}
	// =========================================================================
}
