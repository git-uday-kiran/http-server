package server.http;

import org.springframework.stereotype.Component;

@Component
public class EndPoints {

	HttpResponse endPoint1(HttpRequest request, String data) {
		return HttpResponse.ok().withBody(data);
	}

}