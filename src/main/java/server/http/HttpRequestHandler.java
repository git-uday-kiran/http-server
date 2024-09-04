package server.http;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.*;

@Log4j2
@Component
@RequiredArgsConstructor
public class HttpRequestHandler {

	public HttpResponse handle(HttpRequest httpRequest) {
		log.info("Handling http request: {}", httpRequest);
		PathDetail pathDetail = new PathDetail(httpRequest.uri());
		if (!pathDetail.pathVariables.isEmpty()) {
			return HttpResponse.notFound();
		}
		return HttpResponse.ok();
	}

	@Log4j2
	@Getter
	@ToString
	public static class PathDetail {
		private final List<String> pathVariables;
		private final Map<String, String> queryParameters;

		public PathDetail(String path) {
			pathVariables = new ArrayList<>();
			queryParameters = new HashMap<>();
			if (path.startsWith("/")) {
				readPathVariables(path);
				readQueryParameters(path);
			} else {
				log.warn("Path did not start with / for {}", path);
			}
		}

		private void readPathVariables(String path) {
			char pathSeparator = '/', queryStarter = '?';
			StringBuilder builder = new StringBuilder();

			for (int i = 1; i < path.length(); i++) {
				char cur = path.charAt(i);
				if (cur == queryStarter) {
					pathVariables.add(builder.toString());
					break;
				}
				if (cur == pathSeparator) {
					pathVariables.add(builder.toString());
					builder.setLength(0);
				} else {
					builder.append(cur);
					if (i == (path.length() - 1)) {
						pathVariables.add(builder.toString());
					}
				}
			}
		}

		private void readQueryParameters(String path) {
			int queryStart = path.indexOf('?');
			String querySeparator = path.contains("&") ? "&" : ";";

			if (containsQueryParameters(queryStart, path.length())) {
				String queryPath = path.substring(queryStart + 1);

				for (String queryParam : queryPath.split(querySeparator)) {
					var qp = getQueryParameter(queryParam);
					if (qp.isPresent()) {
						var queryParameter = qp.get();
						queryParameters.put(queryParameter.key(), queryParameter.value());
					}
				}
			}
		}

		private boolean containsQueryParameters(int queryStart, int length) {
			return (queryStart != -1) && (queryStart != (length - 1));
		}

		private Optional<QueryParameter> getQueryParameter(String queryParam) {
			int idx = queryParam.indexOf('=');
			if (idx != -1) {
				String key = queryParam.substring(0, idx);
				String value = (idx == (queryParam.length() - 1)) ? "" : queryParam.substring(idx + 1);
				return Optional.of(new QueryParameter(key, value));
			}
			return Optional.empty();
		}

		private record QueryParameter(String key, String value) {}
	}
}