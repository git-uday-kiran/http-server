import java.util.Map;
import java.util.StringJoiner;

public class HttpResponseBuilder {

	private final StringBuilder builder = new StringBuilder();

	public HttpResponseBuilder statusLine(double httpVersion, int statusCode, String reasonPhrase) {
		builder.append("HTTP/").append(httpVersion).append(" ").append(statusCode);
		if (reasonPhrase != null && !reasonPhrase.isEmpty()) {
			builder.append(" ").append(reasonPhrase);
		}
		return this;
	}

	public HttpResponseBuilder header(String name, String value) {
		//TODO
		return this;
	}

	public HttpResponseBuilder body(Object body) {
		//TODO
		return this;
	}

	public <K extends CharSequence, V extends CharSequence> HttpResponseBuilder body(Map<K, V> body) {
		StringJoiner jsonBuilder = new StringJoiner(",", "{", "}");
		for (Map.Entry<K, V> entry : body.entrySet())
			jsonBuilder.add(entry.getKey() + ":" + entry.getValue());
		builder.append(jsonBuilder);
		return this;
	}

	public String getResponse() {
		return builder.toString();
	}

	public HttpResponseBuilder lineBreak() {
		builder.append("\r\n");
		return this;
	}

	public static HttpResponseBuilder create() {
		return new HttpResponseBuilder();
	}

}
