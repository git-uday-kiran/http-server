package server.http;

import lombok.Getter;
import lombok.Setter;
import server.http.read_writers.BodyWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class HttpResponse {

	private final HttpStatus status;
	private final Map<String, String> headers;
	private final ByteArrayOutputStream bodyOutputStream;

	public HttpResponse(HttpStatus status) {
		this.status = status;
		headers = new HashMap<>();
		bodyOutputStream = new ByteArrayOutputStream();
	}

	public InputStream getBody() {
		return new ByteArrayInputStream(bodyOutputStream.toByteArray());
	}

	public HttpResponse withHeader(String name, String value) {
		headers.put(name, value);
		return this;
	}

	public HttpResponse withHeaders(Map<String, String> headers) {
		this.headers.putAll(headers);
		return this;
	}

	public HttpResponse withBody(InputStream body) {
		try {
			body.transferTo(bodyOutputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	public HttpResponse withBody(String content) {
		return withBody(content.getBytes());
	}

	public HttpResponse withBody(byte[] body) {
		try {
			bodyOutputStream.write(body);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	public <T> HttpResponse withBody(BodyWriter<T> writer, T content) {
		try {
			writer.write(bodyOutputStream, content);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	public static HttpResponse of(HttpStatus status) {
		return new HttpResponse(status);
	}

	public static HttpResponse ok() {
		return new HttpResponse(HttpStatus.OK);
	}

	public static HttpResponse notFound() {
		return new HttpResponse(HttpStatus.NOT_FOUND);
	}

}
