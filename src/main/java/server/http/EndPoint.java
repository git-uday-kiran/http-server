package server.http;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.*;

@Log4j2
@Getter
public class EndPoint {

	private final HttpMethod method;
	private final MediaType consumes;
	private final MediaType produces;
	private final List<String> paths;
	private final Set<String> queries;

	private final PathQueryParser pathQueryParser = GlobalThings.appCtx.getBean(PathQueryParser.class);

	public EndPoint(List<String> paths) {
		this(paths, Collections.emptySet());
	}

	public EndPoint(List<String> paths, Set<String> queries) {
		this(paths, queries, HttpMethod.GET, MediaType.ALL, MediaType.ALL);
	}

	public EndPoint(List<String> paths, HttpMethod method) {
		this(paths, Collections.emptySet(), method, MediaType.ALL, MediaType.ALL);
	}

	public EndPoint(List<String> paths, HttpMethod method, MediaType consumes, MediaType produces) {
		this(paths, Collections.emptySet(), method, consumes, produces);
	}

	public EndPoint(List<String> paths, Set<String> queries, HttpMethod method, MediaType consumes, MediaType produces) {
		this.paths = new ArrayList<>(paths);
		this.queries = new HashSet<>(queries);
		this.method = method;
		this.consumes = consumes;
		this.produces = produces;
	}

	public boolean matches(HttpRequest request) {
		String requestUri = request.getUri();
		if (requestUri == null || !requestUri.startsWith("/")) return false;

		if (method != request.getMethod()) return false;
		if (method != HttpMethod.GET && consumes != MediaType.ALL && consumes != request.getContentType()) return false;

		List<String> paths = pathQueryParser.parsePaths(requestUri);
		Set<String> queries = pathQueryParser.parseQueryKeys(requestUri);
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

	public RequestUriDetail getRequestUriDetail(HttpRequest request) {
		String requestUri = request.getUri();

		if (requestUri == null || !requestUri.startsWith("/")) {
			throw new IllegalArgumentException("Request URI must start with /");
		}

		List<String> paths = pathQueryParser.parsePaths(requestUri);
		Set<String> queries = pathQueryParser.parseQueryKeys(requestUri);

		if (!matches(paths, queries)) {
			throw new IllegalArgumentException("Request URI %s did not match with given endpoint".formatted(requestUri));
		}

		Map<String, String> pathsStore = new HashMap<>();
		Map<String, String> queriesStore = new HashMap<>();

		storePathValues(paths, pathsStore);
		storeQueryValues(pathQueryParser.getQueries(requestUri), queriesStore);
		return new RequestUriDetail(pathsStore, queriesStore);
	}

	private void storePathValues(List<String> values, Map<String, String> store) {
		int size = paths.size();
		for (int i = 0; i < size; i++) {
			store.put(paths.get(i), values.get(i));
		}
	}

	private void storeQueryValues(Map<String, String> queries, Map<String, String> store) {
		for (String key : this.queries) {
			store.put(key, queries.get(key));
		}
	}
}
