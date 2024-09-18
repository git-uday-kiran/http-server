package server.http;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import server.http.read_writers.BodyReader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
public class EndPointsHandler {

	private static final ApplicationContext appCtx = GlobalThings.appCtx;

	public HttpResponse handle(EndPoint endPoint, HttpRequest request) throws Exception {
		RequestUriDetail uriDetail = endPoint.getRequestUriDetail(request);

		Method handlerMethod = endPoint.getHanlderMethod();
		List<Object> args = getMethodArgsData(request, handlerMethod, uriDetail);
		Object returnData = handlerMethod.invoke(endPoint.getHandlerController(), args.toArray());

		return (HttpResponse) returnData;
	}

	private List<Object> getMethodArgsData(HttpRequest request, Method handlerMethod, RequestUriDetail uriDetail) throws IOException {
		List<Object> args = new ArrayList<>();

		for (Parameter parameter : handlerMethod.getParameters()) {
			if (parameter.getType() == HttpRequest.class) {
				args.add(request);

			} else if (parameter.isAnnotationPresent(RequestBody.class)) {
				args.add(read(request.getContent(), parameter.getType()));

			} else if (parameter.isAnnotationPresent(PathVariable.class)) {
				var pathVariable = parameter.getAnnotation(PathVariable.class);
				String pathVariableName = "{" + (pathVariable.value().isBlank() ? parameter.getName() : pathVariable.value()) + "}";
				args.add(uriDetail.getRequiredPathValue(pathVariableName));

			} else if (parameter.isAnnotationPresent(QueryParam.class)) {
				var queryParam = parameter.getAnnotation(QueryParam.class);
				String queryKeyName = queryParam.value().isBlank() ? parameter.getName() : queryParam.value();
				args.add(uriDetail.getRequiredQueryValue(queryKeyName));

			} else {
				throw new UnsupportedOperationException("Can not read to method parameter: " + parameter);
			}
		}

		return args;
	}

	private <T> Object read(InputStream inputStream, Class<T> type) throws IOException {
		var readerBeans = appCtx.getBeansOfType(BodyReader.class);
		for (BodyReader<?> reader : readerBeans.values()) {
			if (reader.canReadAs(type)) {
				return reader.read(inputStream, type);
			}
		}
		throw new RuntimeException("No reader found for type: " + type);
	}

}
