package server.http;

import org.springframework.stereotype.Component;

@Component
public class EndPoints {

	HttpResponse endPointStage4(String data) {
		return HttpResponse.ok()
			.withHeader("Content-Type", "text/plain")
			.withBody(data);
	}

	HttpResponse endPointStage5(HttpRequest request) {
		String userAgent = request.headers().get("User-Agent");
		return HttpResponse.ok()
			.withHeader("Content-Type", "text/plain")
			.withBody(userAgent);
	}

}