import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

import java.nio.charset.StandardCharsets;

public class TestHandler implements HttpHandler
{
	@Override
	public void handle(HttpExchange exchange) throws IOException
	{
		String response = "Student database backend is running";
		
		byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
		
		exchange.sendResponseHeaders(200, responseBytes.length);
		
		try (OutputStream output = exchange.getResponseBody())
		{
			output.write(responseBytes);
		}
	}
}