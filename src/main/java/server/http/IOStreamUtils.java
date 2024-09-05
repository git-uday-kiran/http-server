package server.http;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDateTime;
import java.util.Set;

import static java.nio.file.StandardOpenOption.*;

@Service
public class IOStreamUtils {

	public static final OpenOption[] WRITE_OPTIONS = {CREATE, WRITE, TRUNCATE_EXISTING};
	public static final OpenOption[] READ_OPTIONS = {READ};

	public InputStream inputStream(File file) {
		try {
			return Files.newInputStream(file.toPath(), READ_OPTIONS);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public InputStream tempInputStream(File file) {
		try {
			return tempInputStream(Files.newInputStream(file.toPath(), READ_OPTIONS));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public InputStream tempInputStream(InputStream inputStream) throws IOException {
		File tempFile = tempFile(inputStream);
		return Files.newInputStream(tempFile.toPath());
	}

	public InputStream tempInputStream(InputStream inputStream, int offset, int length) throws IOException {
		File tempFile = createTempFile();
		inputStream.skipNBytes(offset);

		try (OutputStream out = new FileOutputStream(tempFile)) {
			byte[] buffer = new byte[4096];
			int readBytes, nextRead = Math.min(buffer.length, length);

			while (length > 0 && (readBytes = inputStream.read(buffer, 0, nextRead)) != -1) {
				out.write(buffer, 0, readBytes);
				length -= readBytes;
				nextRead = Math.min(buffer.length, length);
			}
		}

		return Files.newInputStream(tempFile.toPath());
	}

	public File tempFile(InputStream inputStream) throws IOException {
		File tempFile = createTempFile();
		try (OutputStream out = Files.newOutputStream(tempFile.toPath(), WRITE_OPTIONS)) {
			inputStream.transferTo(out);
		}
		return tempFile;
	}

	public OutputStream outputStream(File file) {
		try {
			return Files.newOutputStream(file.toPath(), WRITE_OPTIONS);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public OutputStream tempOutputStream() throws IOException {
		File tempFile = createTempFile();
		return Files.newOutputStream(tempFile.toPath(), WRITE_OPTIONS);
	}

	public File createTempFile() {
		try {
			String tempFilePrefix = LocalDateTime.now().toString();
			return createTempFile(tempFilePrefix, "");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public File createTempFile(String prefix, String suffix) throws IOException {
		Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxrwxrwx");
		FileAttribute<Set<PosixFilePermission>> fileAttribute = PosixFilePermissions.asFileAttribute(perms);
		Path tempFile = Files.createTempFile(prefix, suffix, fileAttribute);
		return tempFile.toFile();
	}

}
