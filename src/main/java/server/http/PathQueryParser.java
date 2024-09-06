package server.http;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PathQueryParser {

	public List<String> parsePaths(String requestUri) {
		List<String> result = new ArrayList<>();
		if (requestUri == null || !requestUri.startsWith("/")) return result;

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

	public Set<String> parseQueryKeys(String requestUri) {
		Set<String> queryKeys = new HashSet<>();

		int queryStart = requestUri.indexOf('?');
		String querySeparator = requestUri.contains("&") ? "&" : ";";

		if (queryStart != -1) {
			String queriesPath = requestUri.substring(queryStart + 1);
			for (String keyValue : queriesPath.split(querySeparator)) {
				getQuery(keyValue).ifPresent(query -> queryKeys.add(query.key()));
			}
		}
		return queryKeys;
	}

	public Map<String, String> getQueries(String requestUri) {
		Map<String, String> store = new HashMap<>();

		int queryStart = requestUri.indexOf('?');
		String querySeparator = requestUri.contains("&") ? "&" : ";";

		if ((queryStart != -1) && (queryStart != (requestUri.length() - 1))) {
			String queries = requestUri.substring(queryStart + 1);
			for (String keyValue : queries.split(querySeparator)) {
				getQuery(keyValue).ifPresent(query -> store.put(query.key(), query.value()));
			}
		}
		return store;
	}

	private Optional<Query> getQuery(String keyValue) {
		int idx = keyValue.indexOf('=');
		if (idx != -1) {
			String key = keyValue.substring(0, idx);
			String value = (idx == (keyValue.length() - 1)) ? "" : keyValue.substring(idx + 1);
			return Optional.of(new Query(key, value));
		}
		return Optional.empty();
	}

	private record Query(String key, String value) {}
}
