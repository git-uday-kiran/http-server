package server.http.read_writers;

import java.io.IOException;
import java.io.InputStream;

public interface BodyReader<T> {

	T read(InputStream inputStream, Class<?> type) throws IOException;
	boolean canReadAs(Class<?> type);

}
