import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class HttpUtils
{
	public static String setupCorsAndGetMethod(HttpExchange exchange, String methods)
	{
		exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
		exchange.getResponseHeaders().set("Access-Control-Allow-Methods", methods);
		exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
		
		return exchange.getRequestMethod();
	}
	
	public static boolean handleOptionsRequest(HttpExchange exchange) throws IOException
	{
		if(exchange.getRequestMethod().equals("OPTIONS"))
		{
			exchange.sendResponseHeaders(204, -1);
			exchange.close();
			return true;
		}
		
		return false;
	}
	
	public static String readRequestBody(HttpExchange exchange) throws IOException
	{
		try (InputStream input = exchange.getRequestBody())
		{	
			byte[] requestBytes = input.readAllBytes();
			
			return new String(requestBytes, StandardCharsets.UTF_8);
		}
	}
	
	public static void sendJsonResponse(HttpExchange exchange, int statusCode, String response) throws IOException
	{
		byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
		
		exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
		exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

		exchange.sendResponseHeaders(statusCode, responseBytes.length);
		
		try (OutputStream output = exchange.getResponseBody())
		{
			output.write(responseBytes);
		}
	}
}