package server.http;

import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public class HttpServer {

	private final AtomicBoolean running;
	private final ServerSocket serverSocket;
	private final ExecutorService executorService;
	private final AtomicInteger counter = new AtomicInteger(0);
	private final HttpSocketHandler httpSocketHandler;

	public HttpServer(int port, HttpSocketHandler httpSocketHandler) {
		this.httpSocketHandler = httpSocketHandler;

		try {
			serverSocket = new ServerSocket(port);
			serverSocket.setReuseAddress(true);
		} catch (IOException e) {
			throw new RuntimeException("Http server creation failed.", e);
		}

		executorService = Executors.newVirtualThreadPerTaskExecutor();
		running = new AtomicBoolean(false);
		Thread serverThread = Thread.ofPlatform().start(this::run);
		serverThread.setName("http-server");
	}

	private void run() {
		running.set(true);
		log.info("Http server listening on port {}", serverSocket.getLocalPort());
		try {
			while (running.get()) {
				Socket socket = serverSocket.accept();
				submitToHandler(socket);
			}
		} catch (IOException e) {
			log.error("Something went wrong, stopping http server.", e);
		} finally {
			running.set(false);
		}
	}

	private void submitToHandler(Socket socket) {
		executorService.submit(() -> {
			log.info("Connection-{} from {}", counter.incrementAndGet(), socket.getRemoteSocketAddress());
			httpSocketHandler.handleSocket(socket);
			log.info("Closing connection: {}", socket.getRemoteSocketAddress());
		});
	}

	@PreDestroy
	public void shutdown() throws Exception {
		running.set(false);
		serverSocket.close();
	}

}
