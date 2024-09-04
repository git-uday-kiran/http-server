package server.http.read_writers;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

@Component
public class StringBodyWriter implements BodyWriter<String> {
	@Override
	public void write(OutputStream outputStream, String content) throws IOException {
		outputStream.write(content.getBytes());
	}
}
