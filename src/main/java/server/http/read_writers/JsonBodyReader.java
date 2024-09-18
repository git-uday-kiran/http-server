package server.http.read_writers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JsonBodyReader implements BodyReader<Map<String, Object>> {

	private final ObjectMapper objectMapper;
	private final StringBodyReader stringBodyReader;

	@Override
	public Map<String, Object> read(InputStream inputStream, Class<?> type) throws IOException {
		String body = stringBodyReader.read(inputStream, String.class);
		TypeReference<Map<String, Object>> jsonType = new TypeReference<>() {};
		return objectMapper.readValue(body, jsonType);
	}

	@Override
	public boolean canReadAs(Class<?> type) {
		return Map.class.isAssignableFrom(type);
	}
}
