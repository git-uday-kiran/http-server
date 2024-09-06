package server.http;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum CompressionScheme {
	GZIP, COMPRESS, DEFLATE, IDENTITY;

	public static CompressionScheme[] fromString(String commaSeparatedSchemes) {
		Set<String> schemes = new HashSet<>();
		List<CompressionScheme> result = new ArrayList<>();

		if (commaSeparatedSchemes == null || commaSeparatedSchemes.isEmpty()) {
			return result.toArray(new CompressionScheme[0]);
		}
		for (String scheme : commaSeparatedSchemes.split(",")) {
			schemes.add(scheme.trim().toUpperCase());
		}
		for (var scheme : CompressionScheme.values()) {
			if (schemes.contains(scheme.name())) result.add(scheme);
		}
		return result.toArray(new CompressionScheme[0]);
	}
}
