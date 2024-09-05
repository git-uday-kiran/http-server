package server.http;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.util.*;

@Log4j2
@Getter
@ToString
public class PathDetail {

	private String path;
	private final List<String> pathVariables;
	private final Map<String, String> queryParameters;

	public PathDetail(String requestUri) {
		if (requestUri.startsWith("/")) {
			pathVariables = readPathVariables(requestUri);
			queryParameters = readQueryParameters(requestUri);
			path = "/" + String.join("/", pathVariables);
		} else {
			pathVariables = new ArrayList<>();
			queryParameters = new HashMap<>();
			log.warn("Path did not start with / for {}", requestUri);
		}
	}

	public boolean isMatched(String endPoint) {
		List<String> epPathVariables = readPathVariables(endPoint);
		if (epPathVariables.size() != pathVariables.size()) return false;

		for (int i = 0; i < pathVariables.size(); i++) {
			String epPathVariable = epPathVariables.get(i);
			String pathVariable = pathVariables.get(i);
			if (epPathVariable.startsWith("{") && epPathVariable.endsWith("}")) continue;
			if (!epPathVariable.equals(pathVariable)) return false;
		}

		return true;
	}

	public Optional<String> getPathVariableValue(String endPoint, String pathVariable) {
		if (!isMatched(endPoint)) return Optional.empty();
		List<String> endPointPathVariables = readPathVariables(endPoint);
		int index = endPointPathVariables.indexOf(pathVariable);
		if (index != -1 && index < pathVariables.size())
			return Optional.of(pathVariables.get(index));
		return Optional.empty();
	}

	private List<String> readPathVariables(String requestUri) {
		List<String> result = new ArrayList<>();

		char pathSeparator = '/', queryStarter = '?';
		StringBuilder builder = new StringBuilder();

		for (int i = 1; i < requestUri.length(); i++) {
			char cur = requestUri.charAt(i);
			if (cur == queryStarter) {
				result.add(builder.toString());
				break;
			}
			if (cur == pathSeparator) {
				result.add(builder.toString());
				builder.setLength(0);
			} else {
				builder.append(cur);
				if (i == (requestUri.length() - 1)) {
					result.add(builder.toString());
				}
			}
		}
		return result;
	}

	private Map<String, String> readQueryParameters(String requestUri) {
		Map<String, String> result = new HashMap<>();

		int queryStart = requestUri.indexOf('?');
		String querySeparator = requestUri.contains("&") ? "&" : ";";

		if (containsQueryParameters(queryStart, requestUri.length())) {
			String queryPath = requestUri.substring(queryStart + 1);

			for (String queryParam : queryPath.split(querySeparator)) {
				var qp = getQueryParameter(queryParam);
				if (qp.isPresent()) {
					var queryParameter = qp.get();
					result.put(queryParameter.key(), queryParameter.value());
				}
			}
		}
		return result;
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
