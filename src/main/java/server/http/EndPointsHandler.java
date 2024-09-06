package server.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import server.http.read_writers.JsonBodyReader;
import server.http.read_writers.JsonBodyWriter;

import java.util.List;
import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class EndPointsHandler {

	private final Controller controller;
	private final JsonBodyReader jsonBodyReader;
	private final JsonBodyWriter jsonBodyWriter;

	private final EndPoint[] endPoints = {
		new EndPoint(List.of("files", "{fileName}"), HttpMethod.GET),
		new EndPoint(List.of("files", "{fileName}"), HttpMethod.POST),
		new EndPoint(List.of("test"), HttpMethod.POST),
		new EndPoint(List.of("test"), HttpMethod.GET),
		new EndPoint(List.of("echo", "{string}"), HttpMethod.GET),
	};

	public HttpResponse handleEndPoints(HttpRequest request) throws Exception {

		EndPoint downloadEndpoint = endPoints[0];
		if (downloadEndpoint.matches(request)) {
			RequestUriDetail uriDetail = downloadEndpoint.getRequestUriDetail(request);
			String fileName = uriDetail.getRequiredPathValue("{fileName}");
			return controller.downloadFile(fileName);
		}

		EndPoint uploadEndpoint = endPoints[1];
		if (uploadEndpoint.matches(request)) {
			var uriDetail = uploadEndpoint.getRequestUriDetail(request);
			var fileName = uriDetail.getRequiredPathValue("{fileName}");
			return controller.uploadFile(request, fileName);
		}

		EndPoint testPost = endPoints[2];
		if (testPost.matches(request)) {
			Map<String, Object> body = jsonBodyReader.read(request.getContent());
			return controller.testPOST(request, body);
		}

		EndPoint testGET = endPoints[3];
		if (testGET.matches(request)) {
			return controller.testGET(request);
		}

		EndPoint echoString = endPoints[4];
		if (echoString.matches(request)) {
			RequestUriDetail uriDetail = echoString.getRequestUriDetail(request);
			return controller.echoString(uriDetail.getRequiredPathValue("{string}"));
		}

		return HttpResponse.notFound();
	}

}
