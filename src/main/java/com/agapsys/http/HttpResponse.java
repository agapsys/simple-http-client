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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

/** *  Represents a response to a {@link HttpRequest} */
public class HttpResponse {
	// CLASS SCOPE =============================================================
	/** String representation of a response. */
	public static final class StringResponse extends HttpResponse {
		private final String content;
		private final String charset;
		private final InputStream contentInputStream;
		private final int contentLength;
		
		private StringResponse(HttpResponse response, String charset, long maxLength) throws IOException {
			super(response.getWrappedResponse());
			content = consumeEntity(maxLength);
			this.charset = charset;

			byte[] contentBytes = content.getBytes(charset);
			contentInputStream = new ByteArrayInputStream(contentBytes);
			this.contentLength = contentBytes.length;
		}

		/**
		* Consumes entity content and returns its string representation
		* @return response content string representation
		* @param maxLength maximum accepted content-length
		* @throws IOException if there was an I/O error while processing the response.
		*/
		private String consumeEntity(long maxLength) throws IOException {
		   HttpEntity entity = getWrappedResponse().getEntity();
		   String responseBody = null;

		   if (entity != null) {
			   long length = entity.getContentLength();
			   if (maxLength == -1 || length <= maxLength) {
				   responseBody = EntityUtils.toString(entity);
			   } else {
				   throw new IOException(String.format("entity length (%d) is greater than maximum allowed length (%d)", length, maxLength));
			   }
		   }

		   return responseBody;
	   }
		
		public String getCharset() {
			return charset;
		}
		
		@Override
		public InputStream getContentInputStream() throws IOException {
			return contentInputStream;
		}

		@Override
		public long getContentLength() {
			return contentLength;
		}
		
		public String getContentString() {
			return content;
		}
	}
	
	/**
	 * Executes given request, consume the response and returns a string representation of it.
	 * @param request HTTP request to be executed
	 * @param respCharset response string charset
	 * @param maxLength maximum accepted response length. If response is bigger than such length, an I/O error will be generated
	 * @return string representation of resulting response
	 * @throws IOException if there is an I/O error while processing the request/response
	 */
	public static StringResponse getStringResponse(HttpRequest request, String respCharset, long maxLength) throws IOException {
		HttpClient client = new HttpClient();
		StringResponse response = getStringResponse(client, request, respCharset, maxLength);
		client.close();
		return response;
	}
	
	/**
	 * Executes given request, consume the response and returns a string representation of it.
	 * @param client client used to execute the request.
	 * @param request HTTP request to be executed
	 * @param respCharset response string charset
	 * @param maxLength maximum accepted response length. If response is bigger than such length, an I/O error will be generated
	 * @return string representation of resulting response
	 * @throws IOException if there is an I/O error while processing the request/response
	 */
	public static StringResponse getStringResponse(HttpClient client, HttpRequest request, String respCharset, long maxLength) throws IOException {
		HttpResponse response = request.execute(client);
		StringResponse strResp = new StringResponse(response, respCharset, maxLength);
		response.close();
		return strResp;
	}
	// =========================================================================
	
	// INSTANCE SCOPE ==========================================================
	private final CloseableHttpResponse wrappedResponse;
	
	private List<HttpHeader> headers = null;
	
	/** 
	 * Constructor.
	 * Wraps a {@linkplain org.apache.http.HttpResponse} instance.
	 * @param wrappedResponse wrapped response
	 */
	protected HttpResponse(CloseableHttpResponse wrappedResponse) {
		if (wrappedResponse == null) throw new IllegalArgumentException("Wrapped response cannot be null");
		this.wrappedResponse = wrappedResponse;
	}
	
	/**
	 * Returns wrapped response passed in constructor.
	 * @return wrapped response passed in constructor.
	 */
	protected CloseableHttpResponse getWrappedResponse() {
		return wrappedResponse;
	}
	
	/**
	 * Return the HTTP status code.
	 * @return HTTP status code.
	 */
	public int getStatusCode() {
		return wrappedResponse.getStatusLine().getStatusCode();
	}
	
	/**
	 * Returns the input stream associated with response content
	 * @return the input stream associated with response content
	 * @throws IOException if there was an I/O error while processing the response.
	 */
	public InputStream getContentInputStream() throws IOException {
		HttpEntity entity = wrappedResponse.getEntity();
		return entity.getContent();
	}
	
	/** 
	 * Returns content encoding. 
	 * @return content encoding.
	 */
	public String getContentEncoding() {
		return wrappedResponse.getEntity().getContentEncoding().getValue();
	}
	
	/**
	 * Returns content length
	 * @return content length
	 */
	public long getContentLength() {
		return wrappedResponse.getEntity().getContentLength();
	}
	
	/**
	 * Returns content type
	 * @return content type
	 */
	public String getContentType() {
		return wrappedResponse.getEntity().getContentType().getValue();
	}
		
	/** 
	 * Return response locale.
	 * @return response locale.
	 */
	public Locale getLocale() {
		return wrappedResponse.getLocale();
	}

	/**
	 * Return the version of the protocol.
	 * @return the version of the protocol.
	 */
	public String getProtocolVersion() {
		ProtocolVersion pv = wrappedResponse.getProtocolVersion();
		return String.format("%s.%s", pv.getMajor(), pv.getMinor());
	}

	/**
	 * Returns the first header with given name
	 * @param name header name
	 * @return first header with given name identified in the response or null if there is no such header
	 */
	public HttpHeader getFirstHeader(String name) {
		for (HttpHeader header : getHeaders()) {
			if (header.getName().equals(name)) {
				return header;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns all headers
	 * @return all the headers of this message.
	 */
	public List<HttpHeader> getHeaders() {
		if (headers == null) {
			org.apache.http.Header[] wrappedHeaders = wrappedResponse.getAllHeaders();
			List<HttpHeader> _headers = new LinkedList<>();
			for (org.apache.http.Header wrappedHeader : wrappedHeaders) {
				_headers.add(new HttpHeader(wrappedHeader.getName(), wrappedHeader.getValue()));
			}
			headers = Collections.unmodifiableList(_headers);
		}
		
		
		return headers;
	}
	
	/**
	 * Returns all headers with given name.
	 * @param name header name
	 * @return list of header with given name
	 */
	public List<HttpHeader> getHeaders(String name) {
		List<HttpHeader> filteredHeaders = new LinkedList<>();
		
		for (HttpHeader header : getHeaders()) {
			if (header.getName().equals(name)) {
				filteredHeaders.add(header);
			}
		}
		
		return Collections.unmodifiableList(filteredHeaders);
	}
	
	/**
	 * Closes this response
	 * @throws IOException if an I/O error happened during response closing.
	 */
	public void close() throws IOException {
		wrappedResponse.close();
	}
}
