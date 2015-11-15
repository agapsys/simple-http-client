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

import java.io.File;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

public abstract class MultipartRequest extends EntityRequest {
	// CLASS SCOPE =============================================================
	public static class MultipartPost extends MultipartRequest {

		public MultipartPost(String uri, String...uriParams) {
			super(uri, uriParams);
		}

		public MultipartPost() {
			super();
		}

		@Override
		protected HttpRequestBase getCoreRequest(String uri) {
			return new org.apache.http.client.methods.HttpPost(uri);
		}
	}
	
	public static class MultipartPut extends MultipartRequest {

		public MultipartPut(String uri, String...uriParams) {
			super(uri, uriParams);
		}

		public MultipartPut() {
			super();
		}

		@Override
		protected HttpRequestBase getCoreRequest(String uri) {
			return new org.apache.http.client.methods.HttpPut(uri);
		}
	}
	
	public static class MultipartPatch extends MultipartRequest {

		public MultipartPatch(String uri, String...uriParams) {
			super(uri, uriParams);
		}

		public MultipartPatch() {
			super();
		}

		@Override
		protected HttpRequestBase getCoreRequest(String uri) {
			return new org.apache.http.client.methods.HttpPatch(uri);
		}
	}
	// =========================================================================
	
	// INSTANCE SCOPE ==========================================================
	private final MultipartEntityBuilder builder = MultipartEntityBuilder.create();

	public MultipartRequest(String uri, String...uriParams) {
		super(uri, uriParams);
	}
	
	public MultipartRequest() {}

	public void addFile(String name, File file, String mimeType) {
		builder.addBinaryBody(name, file, ContentType.create(mimeType), name);
	}
	
	public void addFile(String name, File file) {
		builder.addBinaryBody(name, file);
	}
	
	public void addFile(File file) {
		builder.addBinaryBody(file.getName(), file);
	}
	
	public void addFile(File file, String mimeType) {
		addFile(file.getName(), file, mimeType);
	}
	
	public void addString(String name, String value) {
		builder.addTextBody(name, value);
	}
	
	public void addString(String name, String value, String mimeType, String charset) {
		builder.addTextBody(name, value, ContentType.create(mimeType, charset));
	}
	
	@Override
	protected HttpEntity getEntity() {
		return builder.build();
	}
	// =========================================================================
}
