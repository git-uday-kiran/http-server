import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Main {
	public static void main(String[] args) {
		System.out.println("Application started!");
		HttpServer.runOn(4221, Main::handleSocket);
	}

	public static void handleSocket(Socket socket) {
		System.out.println("New connection from " + socket.getRemoteSocketAddress());
		try {
			OutputStream outputStream = socket.getOutputStream();
			String response = HttpResponseBuilder.create()
				.statusLine(1.1D, 200, "Ok")
				.lineBreak()
//				.body(Map.of("Hello", "World"))
				.lineBreak()
				.getResponse();
			outputStream.write(response.getBytes());
			outputStream.flush();
			socket.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
