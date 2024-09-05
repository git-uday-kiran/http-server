package server.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import server.http.read_writers.JsonBodyWriter;

import java.util.Map;
import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class HttpRequestHandler {

	private final EndPoints endPoints;
	private final JsonBodyWriter jsonBodyWriter;

	public HttpResponse handle(HttpRequest httpRequest) {
		log.info("Handling http request: {}", httpRequest);
		PathDetail pathDetail = new PathDetail(httpRequest.uri());
		return handleRequest(httpRequest, pathDetail);
	}

	public HttpResponse handleRequest(HttpRequest request, PathDetail pathDetail) {
		try {
			String endPoint = "/files/{fileName}";
			Optional<String> opFileName = pathDetail.getPathVariableValue(endPoint, "{fileName}");
			if (opFileName.isPresent()) {
				return endPoints.downloadFile(opFileName.get());
			}
		} catch (Exception exception) {
			log.error("Error while handling request", exception);
			return HttpResponse.internalServerError()
				.withBody(jsonBodyWriter, Map.of("Error", exception.getMessage()));
		}
		return HttpResponse.notFound();
	}

}