import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class HttpServer {

	private final AtomicBoolean running;
	private final ServerSocket serverSocket;
	private final Consumer<Socket> socketHandler;
	private final ExecutorService executorService;

	public HttpServer(int port, Consumer<Socket> socketHandler) {
		try {
			serverSocket = new ServerSocket(port);
			serverSocket.setReuseAddress(true);
		} catch (IOException e) {
			throw new RuntimeException("Http server creation failed.", e);
		}
		this.socketHandler = socketHandler;
		executorService = Executors.newVirtualThreadPerTaskExecutor();
		running = new AtomicBoolean(false);
		Thread.ofPlatform().start(this::run);
	}

	private void run() {
		running.set(true);
		System.out.println("Listening on port " + serverSocket.getLocalPort());
		try {
			while (running.get()) {
				Socket accepted = serverSocket.accept();
				executorService.submit(() -> socketHandler.accept(accepted));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			running.set(false);
		}
	}

	public void shutdown() throws Exception {
		running.set(false);
		serverSocket.close();
	}

	public static void runOn(int port, Consumer<Socket> socketHandler) {
		new HttpServer(port, socketHandler);
	}

}
