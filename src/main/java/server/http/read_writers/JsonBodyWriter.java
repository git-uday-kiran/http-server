package server.http.read_writers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JsonBodyWriter implements BodyWriter<Map<String, Object>> {

	private final ObjectMapper objectMapper;

	@Override
	public void write(OutputStream outputStream, Map<String, Object> content) throws IOException {
		objectMapper.writeValue(outputStream, content);
	}

}
