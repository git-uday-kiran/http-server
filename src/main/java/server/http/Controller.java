package server.http;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import server.http.read_writers.JsonBodyReader;
import server.http.read_writers.JsonBodyWriter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

@Component
@AllArgsConstructor
public class Controller {

	private final GlobalThings globalThings;
	private final IOStreamUtils ioStreamUtils;
	private final JsonBodyWriter jsonBodyWriter;
	private final JsonBodyReader jsonBodyReader;

	HttpResponse testGET(HttpRequest request) {
		return HttpResponse.ok()
			.withBody("Hey it's working...");
	}

	HttpResponse testPOST(HttpRequest request, Map<String, Object> body) {
		System.out.println("Received Body: " + body);
		return HttpResponse.ok()
			.withBody("Got it!");
	}

	HttpResponse echoString(String string) {
		return HttpResponse.ok()
			.withBody(string);
	}

	HttpResponse downloadFile(String fileName) throws IOException {
		Optional<File> opFile = findFile(fileName);
		if (opFile.isPresent()) {
			File file = opFile.get();
			byte[] bytes = Files.readAllBytes(Path.of(file.getAbsolutePath()));
			return HttpResponse.ok()
				.withHeader("Content-Type", "application/octet-stream")
				.withBody(bytes);
		}
		return HttpResponse.notFound();
	}

	HttpResponse uploadFile(HttpRequest request, String fileName) throws IOException {
		String filePath = globalThings.directory.getPath() + File.separator + fileName;
		Path path = (new File(filePath)).toPath();

		try (InputStream content = request.getContent()) {
			Files.write(path, content.readAllBytes(), IOStreamUtils.WRITE_OPTIONS);
		}

		return HttpResponse.of(HttpStatus.CREATED)
			.withBody("Created file with path " + filePath);
	}

	Optional<File> findFile(String fileName) {
		File[] allFiles = globalThings.getDirectory().listFiles();
		assert allFiles != null;
		for (File file : allFiles) {
			if (fileName.equals(file.getName())) {
				return Optional.of(file);
			}
		}
		return Optional.empty();
	}

}