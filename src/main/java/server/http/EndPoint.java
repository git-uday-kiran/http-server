package server.http;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

public interface EndPoint {

	boolean matches(HttpRequest httpRequest);
	boolean matches(List<String> matchPaths, Set<String> matchQueries);
	RequestUriDetail getRequestUriDetail(HttpRequest request);
	Method getHanlderMethod();
	Object getHandlerController();

}
