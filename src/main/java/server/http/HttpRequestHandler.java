package server.http;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import server.http.annotations.RequestMapping;
import server.http.annotations.RestController;
import server.http.read_writers.JsonBodyWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Log4j2
@Component
@RequiredArgsConstructor
public class HttpRequestHandler implements ApplicationContextAware {

	private static ApplicationContext appCtx;

	private final EndPointsHandler endPointsHandler;
	private final HttpResponseWriter httpResponseWriter;
	private final JsonBodyWriter jsonBodyWriter;

	private final PathQueryParser parser;
	private final Set<EndPoint> endPoints = new HashSet<>();

	@PostConstruct
	private Set<EndPoint> scanEndPoints() {
		log.info("Scanning endpoints...");
		Map<String, Object> endPointBeans = appCtx.getBeansWithAnnotation(RestController.class);
		log.info("Found controllers: {}", endPointBeans);
		endPointBeans.values().forEach(bean -> findEndPoints(bean, endPoints));
		log.info("Found endpoints: {}", endPoints);
		return endPoints;
	}

	private void findEndPoints(Object bean, Set<EndPoint> endPoints) {
		Class<?> clazz = bean.getClass();
		for (Method method : clazz.getDeclaredMethods()) {
			if (method.isAnnotationPresent(RequestMapping.class)) {
				var mapping = method.getAnnotation(RequestMapping.class);
				EndPoint endPoint = new EndPointImpl(mapping, method, bean, parser);
				endPoints.add(endPoint);
			}
		}
	}

	public void handle(HttpRequest request, OutputStream outputStream) throws IOException {
		HttpResponse response = handle(request);
		CompressionScheme[] compressionSchemes = CompressionScheme.fromString(request.getHeaders().get("accept-encoding"));
		httpResponseWriter.write(response, outputStream, compressionSchemes);
		request.getContent().close();
	}

	private HttpResponse handle(HttpRequest request) {
		log.info("Handling http request: {}", request);
		try {
			for (EndPoint endPoint : endPoints) {
				if (endPoint.matches(request)) {
					return endPointsHandler.handle(endPoint, request);
				}
			}
			return HttpResponse.notFound();
		} catch (Exception exception) {
			log.error("Error while handling request", exception);
			Map<String, Object> json = Map.of("Error", exception.getMessage());
			return HttpResponse.internalServerError().withBody(jsonBodyWriter, json);
		}
	}

	@Override
	public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
		HttpRequestHandler.appCtx = applicationContext;
	}
}