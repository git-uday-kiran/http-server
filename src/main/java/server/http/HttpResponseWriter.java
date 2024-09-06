package server.http;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Map;
import java.util.StringJoiner;

@Service
public class HttpResponseWriter {

	private final String CRLF = "\r\n";

	public void write(HttpResponse response, OutputStream outputStream) throws IOException {
		response.withHeader("Content-Length", String.valueOf(response.getContentLength()));
		writeStatusLine(response, outputStream);
		writeHeaders(response, outputStream);
		try (InputStream body = response.getBody()) {
			body.transferTo(outputStream);
		}
	}

	private void writeStatusLine(HttpResponse response, OutputStream outputStream) throws IOException {
		HttpStatus status = response.getStatus();
		String statusLine = MessageFormat.format(
			"HTTP/{0} {1} {2}" + CRLF,
			1.1D,
			status.getStatusCode(),
			status.getReasonPhrase());
		outputStream.write(statusLine.getBytes());
	}

	private void writeHeaders(HttpResponse response, OutputStream outputStream) throws IOException {
		StringJoiner joiner = new StringJoiner(CRLF);
		for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
			joiner.add(header.getKey() + ": " + header.getValue());
		}
		String headerContent = joiner + CRLF + CRLF;
		outputStream.write(headerContent.getBytes());
	}

}
