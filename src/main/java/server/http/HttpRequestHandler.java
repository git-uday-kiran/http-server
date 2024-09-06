package server.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import server.http.read_writers.JsonBodyWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class HttpRequestHandler {

	private final EndPointsHandler endPointsHandler;
	private final HttpResponseWriter httpResponseWriter;
	private final JsonBodyWriter jsonBodyWriter;

	public void handle(HttpRequest request, OutputStream outputStream) throws IOException {
		HttpResponse response = handle(request);
		CompressionScheme[] compressionSchemes = CompressionScheme.fromString(request.getHeaders().get("accept-encoding"));
		httpResponseWriter.write(response, outputStream, compressionSchemes);
		request.getContent().close();
	}

	private HttpResponse handle(HttpRequest request) {
		log.info("Handling http request: {}", request);
		try {
			return endPointsHandler.handleEndPoints(request);
		} catch (Exception exception) {
			log.error("Error while handling request", exception);
			Map<String, Object> json = Map.of("Error", exception.getMessage());
			return HttpResponse.internalServerError().withBody(jsonBodyWriter, json);
		}
	}

}