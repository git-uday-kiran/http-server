package server.http;

import lombok.Getter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;

@Getter
@Component
public class GlobalThings implements CommandLineRunner {

	public File directory;

	@Override
	public void run(String... args) throws Exception {
		directory = new File(args[1]);
		if (!directory.exists()) {
			throw new FileNotFoundException(args[1]);
		}
		if (!directory.isDirectory()) {
			throw new RuntimeException("%s is not a directory ".formatted(args[1]));
		}
	}
}
