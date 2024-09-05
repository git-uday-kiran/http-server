package server.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import server.http.read_writers.JsonBodyReader;
import server.http.read_writers.JsonBodyWriter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class HttpRequestHandler {

	private final EndPoints endPoints;
	private final JsonBodyWriter jsonBodyWriter;
	private final JsonBodyReader jsonBodyReader;

	public HttpResponse handle(HttpRequest httpRequest) throws IOException {
		log.info("Handling http request: {}", httpRequest);
		PathDetail pathDetail = new PathDetail(httpRequest.uri());
		HttpResponse response = handleRequest(httpRequest, pathDetail);
		httpRequest.content().close();
		return response;
	}

	public HttpResponse handleRequest(HttpRequest request, PathDetail pathDetail) {
		try {
			return handleEndPoints(request, pathDetail);
		} catch (Exception exception) {
			log.error("Error while handling request", exception);
			Map<String, Object> json = Map.of("Error", exception.getMessage());
			return HttpResponse.internalServerError().withBody(jsonBodyWriter, json);
		}
	}

	public HttpResponse handleEndPoints(HttpRequest request, PathDetail pathDetail) throws Exception {
		String endPoint = "/files/{fileName}";
		if (pathDetail.isMatched(endPoint)) {
			Optional<String> opFileName = pathDetail.getPathVariableValue(endPoint, "{fileName}");
			if (opFileName.isPresent()) {
				String fileName = opFileName.get();
				if (request.method() == HttpMethod.GET) {
					return endPoints.downloadFile(fileName);
				}
				if (request.method() == HttpMethod.POST) {
					return endPoints.uploadFile(request, fileName);
				}
			}
		}

		String testEndPoint = "/test";
		if (pathDetail.isMatched(testEndPoint)) {
			Map<String, Object> body = jsonBodyReader.read(request.content(), request.content().available());
			return endPoints.test(request, body);
		}

		return HttpResponse.notFound();
	}

}