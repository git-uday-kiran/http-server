package server.http;

import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;

@Getter
@Component
public class GlobalThings implements CommandLineRunner, ApplicationContextAware {

	public File directory;

	public static ApplicationContext appCtx;

	@Override
	public void run(String... args) throws Exception {
		directory = Files.createTempDirectory("http-server-tmp-files").toFile();
		System.out.println("Temp directory: " + directory.getAbsolutePath());
	}

	@Override
	public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
		GlobalThings.appCtx = applicationContext;
	}
}
