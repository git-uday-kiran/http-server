package server.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Beans {

	@Bean
	ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

	@Bean
	HttpServer httpServer(HttpSocketHandler httpSocketHandler) {
		return new HttpServer(4221, httpSocketHandler);
	}

}
