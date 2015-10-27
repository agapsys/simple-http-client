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

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

public abstract class StringEntityRequest extends EntityRequest {
	private final ContentType contentType;
	
	private String contentBody = "";

	public StringEntityRequest(String uri, String mimeType, String charset) {
		super(uri);
		contentType = ContentType.create(mimeType, charset);
	}
	
	public StringEntityRequest(String mimeType, String charset) {
		contentType = ContentType.create(mimeType, charset);
	}

	public final String getMimeType() {
		return contentType.getMimeType();
	}
	
	public final String getCharset() {
		return contentType.getCharset().name();
	}
		
	public final String getContentBody() {
		return contentBody;
	}
	
	public final void setContentBody(String contentBody) {
		if (contentType == null) throw new IllegalArgumentException("Null content body");
		this.contentBody = contentBody;
	}

	@Override
	protected HttpEntity getEntity() {
		return new StringEntity(contentBody, contentType);
	}
}
