package server.http;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import server.http.read_writers.JsonBodyWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Component
@AllArgsConstructor
public class EndPoints {

	private final GlobalThings globalThings;
	private final JsonBodyWriter jsonBodyWriter;

	HttpResponse downloadFile(String fileName) throws IOException {
		System.out.println("File: " + fileName);
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