package server.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class HttpRequestHandler {

	private final EndPoints endPoints;

	public HttpResponse handle(HttpRequest httpRequest) {
		log.info("Handling http request: {}", httpRequest);
		PathDetail pathDetail = new PathDetail(httpRequest.uri());
		return handleRequest(httpRequest, pathDetail);
	}

	public HttpResponse handleRequest(HttpRequest request, PathDetail pathDetail) {
		String testEndPoint = "/echo/{string}";

		if (pathDetail.isMatched(testEndPoint)) {
			String pathVariableValue = pathDetail.getPathVariableValue(testEndPoint, "{string}").orElse("");
			return endPoints.endPoint1(request, pathVariableValue);
		}

		return HttpResponse.notFound();
	}

}