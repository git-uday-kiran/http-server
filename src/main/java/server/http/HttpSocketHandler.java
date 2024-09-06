package server.http;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import server.http.read_writers.JsonBodyWriter;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class HttpSocketHandler {

	private final HttpRequestHandler httpRequestHandler;
	private final HttpRequestReader httpRequestReader;
	private final JsonBodyWriter jsonBodyWriter;
	private final HttpResponseWriter httpResponseWriter;

	public void handleSocket(Socket socket) {
		try (InputStream inputStream = socket.getInputStream();
			 OutputStream outputStream = socket.getOutputStream()) {

			if (inputStream.available() == 0) return;
			var request = httpRequestReader.read(inputStream);

			if (request.isPresent()) {
				httpRequestHandler.handle(request.get(), outputStream);
			} else {
				httpResponseWriter.write(httpRequestParseError(), outputStream); //TODO ~ close parse error response output stream
			}

		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private HttpResponse httpRequestParseError() {
		return HttpResponse.of(HttpStatus.BAD_REQUEST)
			.withBody(jsonBodyWriter, Map.of("Error", "Can't parse as http request"));
	}

}
