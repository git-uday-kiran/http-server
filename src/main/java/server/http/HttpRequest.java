package server.http;

import java.io.InputStream;
import java.util.Map;

public record HttpRequest(
	String uri,
	double httpVersion,
	HttpMethod method,
	Map<String, String> headers,
	InputStream content
) {}
