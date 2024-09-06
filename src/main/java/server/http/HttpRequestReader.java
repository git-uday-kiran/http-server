package server.http;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
public class HttpRequestReader {

	public Optional<HttpRequest> read(InputStream inputStream) {
		try {
			var reader = new BufferedReader(new InputStreamReader(getRequestLineAndHeadersStream(inputStream)));

			String requestLine = reader.readLine();
			String[] split = requestLine.split(" ");

			HttpMethod method = HttpMethod.valueOf(split[0]);
			String url = URLDecoder.decode(split[1], StandardCharsets.UTF_8);
			double httpVersion = Double.parseDouble(split[2].substring("HTTP/".length()));

			Map<String, String> headers = readHeaders(reader);
			MediaType contentType = MediaType.fromString(headers.get("content-type"));

			InputStream requestBody = readRequestBody(headers, inputStream);
			return Optional.of(new HttpRequest(url, method, contentType, headers, requestBody));
		} catch (Exception exception) {
			log.error("Reading input stream as http request failed.", exception);
			return Optional.empty();
		}
	}

	private Map<String, String> readHeaders(BufferedReader reader) throws IOException {
		Map<String, String> headers = new HashMap<>();
		String headerLine;
		while (hasLength(headerLine = reader.readLine())) {
			int separatorIndex = headerLine.indexOf(':');
			String key = headerLine.substring(0, separatorIndex).toLowerCase().trim();
			String value = headerLine.substring(separatorIndex + 1).trim();
			headers.put(key, value);
		}
		return headers;
	}

	private InputStream readRequestBody(Map<String, String> headers, InputStream inputStream) throws IOException {
		int contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0").trim());
		return new ByteArrayInputStream(inputStream.readNBytes(contentLength));
	}

	private String getHeader(Map<String, String> headers, String headerName, String defaultValue) {
		return headers.getOrDefault(headerName.toLowerCase(), defaultValue);
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
