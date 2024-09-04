package server.http.read_writers;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class StringBodyReader implements BodyReader<String> {
	@Override
	public String read(InputStream inputStream, int noOfBytes) throws IOException {
		byte[] readBytes = inputStream.readNBytes(noOfBytes);
		return new String(readBytes);
	}
}
