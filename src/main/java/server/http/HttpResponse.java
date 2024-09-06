package server.http;

import lombok.Getter;
import lombok.Setter;
import server.http.read_writers.BodyWriter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class HttpResponse {

	private final HttpStatus status;
	private final Map<String, String> headers;

	private final File tempFile;
	private final OutputStream outputStream;
	private final IOStreamUtils ioStreamUtils;

	public HttpResponse(HttpStatus status) {
		this.status = status;
		headers = new HashMap<>();
		ioStreamUtils = GlobalThings.appCtx.getBean(IOStreamUtils.class);
		tempFile = ioStreamUtils.createTempFile();
		outputStream = ioStreamUtils.outputStream(tempFile);
	}

	/**
	 * creates a new input stream and returns it, must be close the stream your self
	 */
	public InputStream getBody() {
		try {
			outputStream.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return ioStreamUtils.inputStream(tempFile);
	}

	public long getContentLength() {
		return tempFile.length();
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
			body.transferTo(outputStream);
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
			outputStream.write(body);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	public <T> HttpResponse withBody(BodyWriter<T> writer, T content) {
		try {
			writer.write(outputStream, content);
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

	public static HttpResponse internalServerError() {
		return new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
