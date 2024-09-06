package server.http;

import lombok.Getter;

import java.io.InputStream;
import java.util.Map;

@Getter
public class HttpRequest {

	private final String uri;
	private final HttpMethod method;
	private final MediaType contentType;
	private final Map<String, String> headers;
	private final InputStream content;

	public HttpRequest(String uri, HttpMethod method, MediaType contentType, Map<String, String> headers, InputStream content) {
		this.uri = uri;
		this.method = method;
		this.headers = headers;
		this.content = content;
		this.contentType = contentType;
	}

}
