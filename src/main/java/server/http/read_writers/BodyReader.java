package server.http.read_writers;

import java.io.IOException;
import java.io.InputStream;

public interface BodyReader<T> {

	T read(InputStream inputStream) throws IOException;
}
