package server.http.read_writers;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class StringBodyReader implements BodyReader<String> {
	@Override
	public String read(InputStream inputStream) throws IOException {
		byte[] readBytes = inputStream.readAllBytes();
		return new String(readBytes);
	}
}
