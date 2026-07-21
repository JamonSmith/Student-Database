import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.List;

public class StudentHandler implements HttpHandler
{
	private String databaseURL;
	private static final String url = "jdbc:sqlite:students.db";
	
	public StudentHandler(String databaseURL)
	{
		this.databaseURL = databaseURL;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException
	{
		exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
		exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
		
		String method = exchange.getRequestMethod();
		
		if (method.equals("OPTIONS"))
		{
			exchange.sendResponseHeaders(204, -1);
			exchange.close();
			return;
		}
		
		String response;
			
		if (method.equals("GET"))
		{
			try (Connection conn = DriverManager.getConnection(url))
			{
				List<Student> students = SQLiteTest.getAllStudents(conn);
				
				StringBuilder jsonArray = new StringBuilder("[\n");
				
				for (int i = 0; i < students.size(); i++)
				{
					jsonArray.append(students.get(i).toJSON());
					
					if (i < students.size() - 1)
					{
						jsonArray.append(",\n");
					}
				}
				
				jsonArray.append("\n]");
				
				response = jsonArray.toString();
			}
			catch (SQLException e)
			{
				response = """
							{
								"error": "Could not retrieve data"
							}
							""";
			}
		}	
		else if (method.equals("POST"))
		{
			InputStream input = exchange.getRequestBody();
			
			byte[] requestBytes = input.readAllBytes();
			
			String requestBody = new String(requestBytes, StandardCharsets.UTF_8);
			
			System.out.println("POST body: ");
			System.out.println(requestBody + "\n");
			
			response = """
						{
							"message": "POST request received >:("
						}
						""";
						
			Gson gson = new Gson();

			System.out.println("Gson loaded: " + (gson instanceof Gson) + "\n");
			
			StudentRequest request = gson.fromJson(requestBody, StudentRequest.class);
			
			System.out.println(request.getFirstName());
			System.out.println(request.getLastName() + "\n");
			
			try (Connection conn = DriverManager.getConnection(url))
			{
				SQLiteTest.addStudent(conn, request.getFirstName(), request.getLastName());
				
				response = """
						{
							"message": "Student successfully added"
						}
						""";
			}
			catch (SQLException e)
			{
				response = """
							{
								"error": "Could not add student"
							}
							""";
			}
			
			/*
			*/
		}		
		else		
		{
			response = """
						{
							"error": "Method not allowed"
						}
						""";
		}
		
		byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
		
		exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
		exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

		exchange.sendResponseHeaders(200, responseBytes.length);
		
		try (OutputStream output = exchange.getResponseBody())
		{
			output.write(responseBytes);
		}
	}
}