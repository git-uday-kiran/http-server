package server.http;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Map;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
public class HttpResponseWriter {

	private static final String CRLF = "\r\n";
	private final CompressionUtils compressionUtils;
	private final IOStreamUtils ioStreamUtils;

	public void write(HttpResponse response, OutputStream outputStream, CompressionScheme... compressionSchemes) throws IOException {
		InputStream body = getBody(response, compressionSchemes);
		writeStatusLine(response, outputStream);
		writeHeaders(response, outputStream);
		try (body) {
			body.transferTo(outputStream);
		}
	}

	public InputStream getBody(HttpResponse response, CompressionScheme... compressionSchemes) throws IOException {
		InputStream body = response.getBody();
		for (CompressionScheme compressionScheme : compressionSchemes) {
			if (compressionUtils.isSupported(compressionScheme)) {
				try (body) {
					File compressFile = compressionUtils.compress(compressionScheme, body);
					response.withHeader("Content-Length", String.valueOf(compressFile.length()));
					response.withHeader("Content-Encoding", compressionScheme.name().toLowerCase());
					return ioStreamUtils.inputStream(compressFile);
				}
			}
		}
		response.withHeader("Content-Length", String.valueOf(response.getContentLength()));
		return body;
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
