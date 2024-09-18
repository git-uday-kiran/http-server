package server.http;

import lombok.extern.log4j.Log4j2;
import server.http.annotations.RequestMapping;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@Log4j2
public class EndPointImpl implements EndPoint {

	private static final String PATH_SEPARATOR = "/";
	private static final char PATH_SEPARATOR_CHAR = '/';

	private final RequestMapping mapping;
	private final Method handlerMethod;
	private final Object handlerController;

	private final List<String> paths;
	private final Set<String> queries;
	private final PathQueryParser parser;

	public EndPointImpl(RequestMapping mapping, Method handlerMethod, Object handlerController, PathQueryParser pathQueryParser) {
		String requestUri = cleanPath(mapping.path());
		parser = pathQueryParser;
		this.mapping = mapping;
		this.handlerMethod = handlerMethod;
		this.handlerController = handlerController;

		paths = parser.parsePaths(requestUri);
		queries = new HashSet<>(Set.of(mapping.queries()));
		queries.addAll(queryKeys(handlerMethod.getParameters()));
	}

	public boolean matches(HttpRequest request) {
		String requestUri = request.getUri();
		if (requestUri == null || !requestUri.startsWith("/")) return false;

		if (mapping.method() != request.getMethod()) {
			return false; // TODO
		}
		if (!canConsume(request.getContentType())) {
			return false; // TODO
		}

		List<String> paths = parser.parsePaths(requestUri);
		Set<String> queries = parser.parseQueryKeys(requestUri);

		return matches(paths, queries);
	}

	public boolean matches(List<String> matchPaths, Set<String> matchQueries) {
		if (paths.size() != matchPaths.size()) return false;
		if (queries.size() != matchQueries.size()) return false;

		int pathsSize = paths.size();
		for (int i = 0; i < pathsSize; i++) {
			String path1 = paths.get(i), path2 = matchPaths.get(i);
			if (path1.startsWith("{") && path1.endsWith("}")) continue;
			if (!path1.equals(path2)) return false;
		}

		return queries.equals(matchQueries);
	}

	private Set<String> queryKeys(Parameter[] parameters) {
		Set<String> queryKeys = new HashSet<>();
		for (Parameter parameter : parameters) {
			if (parameter.isAnnotationPresent(QueryParam.class)) {
				QueryParam queryParam = parameter.getAnnotation(QueryParam.class);
				queryKeys.add(queryParam.value().isBlank() ? parameter.getName() : queryParam.value());
			}
		}
		return queryKeys;
	}

	private boolean canConsume(MediaType contentType) {
		for (MediaType consumes : mapping.consumes()) {
			if (consumes == MediaType.ALL || consumes == contentType) {
				return true;
			}
		}
		return false;
	}

	private boolean isConsumable(HttpMethod method) {
		Set<HttpMethod> cantConsumeMethods = Set.of(HttpMethod.GET, HttpMethod.DELETE);
		return !cantConsumeMethods.contains(method);
	}

	private void storePathsWithValues(List<String> values, Map<String, String> store) {
		int size = paths.size();
		for (int i = 0; i < size; i++) {
			store.put(paths.get(i), values.get(i));
		}
	}

	private void storeQueriesWithValues(Map<String, String> queries, Map<String, String> store) {
		for (String key : this.queries) {
			store.put(key, queries.get(key));
		}
	}

	private String cleanPath(String... path) {
		String fullPath = trimPathSeparator(String.join(PATH_SEPARATOR, path));
		return PATH_SEPARATOR + keepOnlyOneSeparator(fullPath);
	}

	private String keepOnlyOneSeparator(String path) {
		StringBuilder builder = new StringBuilder();
		int seqCount = 0;
		for (char ch : path.toCharArray()) {
			if (ch == EndPointImpl.PATH_SEPARATOR_CHAR) {
				seqCount++;
				if (seqCount == 1) builder.append(ch);
			} else {
				builder.append(ch);
				seqCount = 0;
			}
		}
		return builder.toString();
	}

	private String trimPathSeparator(String path) {
		int length = path.length();
		int start = 0, end = length - 1;

		while (start < length && path.charAt(start) == PATH_SEPARATOR_CHAR) start++;
		while (end >= 0 && path.charAt(end) == PATH_SEPARATOR_CHAR) end--;

		return (start <= end) ? path.substring(start, end + 1) : "";
	}

	public RequestUriDetail getRequestUriDetail(HttpRequest request) {
		String requestUri = request.getUri();

		if (requestUri == null || !requestUri.startsWith("/")) {
			throw new IllegalArgumentException("Request URI must start with /");
		}

		List<String> paths = parser.parsePaths(requestUri);
		Set<String> queries = parser.parseQueryKeys(requestUri);

		if (!matches(paths, queries)) {
			throw new IllegalArgumentException("Request URI %s did not match with given endpoint".formatted(requestUri));
		}

		Map<String, String> pathsStore = new HashMap<>();
		Map<String, String> queriesStore = new HashMap<>();

		storePathsWithValues(paths, pathsStore);
		storeQueriesWithValues(parser.getQueries(requestUri), queriesStore);
		return new RequestUriDetail(pathsStore, queriesStore);
	}

	@Override
	public Method getHanlderMethod() {
		return handlerMethod;
	}

	@Override
	public Object getHandlerController() {
		return handlerController;
	}

	@Override
	public String toString() {
		return "EndPointV2{" + "paths=" + paths + ", queries=" + queries + '}';
	}

}
