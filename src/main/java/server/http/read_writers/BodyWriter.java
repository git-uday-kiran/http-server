package server.http.read_writers;

import java.io.IOException;
import java.io.OutputStream;

public interface BodyWriter<T> {
	void write(OutputStream outputStream, T content) throws IOException;
}
