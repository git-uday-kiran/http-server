package server.http;

import org.springframework.stereotype.Component;
import server.http.read_writers.JsonBodyWriter;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

@Component
public class HttpSocketHandler {

	private final String CRLF = "\r\n";
	private final HttpRequestHandler requestHandler;
	private final JsonBodyWriter jsonBodyWriter;

	public HttpSocketHandler(HttpRequestHandler urlHandler, JsonBodyWriter jsonBodyWriter) {
		this.requestHandler = urlHandler;
		this.jsonBodyWriter = jsonBodyWriter;
	}

	public void handle(Socket socket) {
		try (InputStream inputStream = socket.getInputStream();
			 OutputStream outputStream = socket.getOutputStream()) {

			if (inputStream.available() == 0) return;

			Optional<HttpRequest> request = readRequest(inputStream);
			HttpResponse response = request.isPresent() ? (requestHandler.handle(request.get())) : parsingErrorResponse();
			writeResponse(response, outputStream);

		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private HttpResponse parsingErrorResponse() {
		return HttpResponse.of(HttpStatus.BAD_REQUEST)
			.withBody(jsonBodyWriter, Map.of("Error", "Can't parse request body"));
	}

	private void writeResponse(HttpResponse response, OutputStream outputStream) throws IOException {
		byte[] bodyBytes = response.getBody().readAllBytes();
		response.withHeader("Content-Length", String.valueOf(bodyBytes.length));

		writeStatusLine(response, outputStream);
		writeHeaders(response, outputStream);
		outputStream.write(bodyBytes);
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

	private Optional<HttpRequest> readRequest(InputStream inputStream) {
		try {
			var reader = new BufferedReader(new InputStreamReader(getRequestLineAndHeadersStream(inputStream)));

			String requestLine = reader.readLine();
			String[] split = requestLine.split(" ");

			HttpMethod method = HttpMethod.valueOf(split[0]);
			String url = URLDecoder.decode(split[1], StandardCharsets.UTF_8);
			double httpVersion = Double.parseDouble(split[2].substring("HTTP/".length()));

			Map<String, String> headers = readHeaders(reader);
			return Optional.of(new HttpRequest(url, httpVersion, method, headers, inputStream));

		} catch (Exception exception) {
			exception.printStackTrace();
			return Optional.empty();
		}
	}

	private Map<String, String> readHeaders(BufferedReader reader) throws IOException {
		Map<String, String> headers = new HashMap<>();
		String headerLine;
		while (hasLength(headerLine = reader.readLine())) {
			int separatorIndex = headerLine.indexOf(':');
			String key = headerLine.substring(0, separatorIndex);
			String value = headerLine.substring(separatorIndex + 1);
			headers.put(key, value);
		}
		return headers;
	}

	private InputStream getRequestLineAndHeadersStream(InputStream inputStream) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int carrierReturn = '\r', byteRead;
		int normalByteCount = 0;

		while ((byteRead = inputStream.read()) != -1) {
			out.write(byteRead);
			if (byteRead == carrierReturn) {
				out.write(inputStream.read());
				if (normalByteCount == 0) break;
				normalByteCount = 0;
			} else {
				normalByteCount++;
			}
		}

		return new ByteArrayInputStream(out.toByteArray());
	}

	private boolean hasLength(String string) {
		return string != null && !string.isEmpty() && !"\r\n".equals(string);
	}

}
