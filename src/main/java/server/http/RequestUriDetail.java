package server.http;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.Optional;

@Log4j2
@ToString
public class RequestUriDetail {

	private final Map<String, String> pathsStore;
	private final Map<String, String> queriesStore;

	public RequestUriDetail(Map<String, String> pathsStore, Map<String, String> queriesStore) {
		this.pathsStore = pathsStore;
		this.queriesStore = queriesStore;
	}

	public String getRequiredPathValue(String path) {
		return getPathValue(path).orElseThrow();
	}

	public Optional<String> getPathValue(String path) {
		return Optional.ofNullable(pathsStore.get(path));
	}

	public String getRequiredQueryValue(String queryKey) {
		return getQueryValue(queryKey).orElseThrow();
	}

	public Optional<String> getQueryValue(String queryKey) {
		return Optional.ofNullable(queriesStore.get(queryKey));
	}

}
