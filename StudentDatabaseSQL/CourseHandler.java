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

public class CourseHandler implements HttpHandler
{
	private String databaseURL;
	
	public CourseHandler(String databaseURL)
	{
		this.databaseURL = databaseURL;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException
	{
		exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
		exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, PUT, DELETE, OPTIONS");
		exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
		
		String method = exchange.getRequestMethod();
		
		if (method.equals("OPTIONS"))
		{
			exchange.sendResponseHeaders(204, -1);
			exchange.close();
			return;
		}
		
		String response;
		int statusCode = 0;
			
		if (method.equals("POST"))
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
			
			CourseRequest request = gson.fromJson(requestBody, CourseRequest.class);
			
			System.out.println(request.getID());
			System.out.println(request.getCourseName());
			System.out.println(request.getGrade() + "\n");
			
			try (Connection conn = DriverManager.getConnection(databaseURL))
			{
				SQLiteTest.addCourseToStudent(conn, request.getID(), request.getCourseName(), request.getGrade());
				
				response = """
						{
							"message": "Course successfully added"
						}
						""";
			}
			catch (SQLException e)
			{
				response = """
							{
								"error": "Could not add course"
							}
							""";
			}
			
			/*
			*/
		}	
		else if (method.equals("PUT"))
		{
			InputStream input = exchange.getRequestBody();
			
			byte[] requestBytes = input.readAllBytes();
			
			String requestBody = new String(requestBytes, StandardCharsets.UTF_8);
			
			System.out.println("PUT body: ");
			System.out.println(requestBody + "\n");
			
			response = """
						{
							"message": "POST request received >:("
						}
						""";
						
			Gson gson = new Gson();

			System.out.println("Gson loaded: " + (gson instanceof Gson) + "\n");
			
			CourseRequest request = gson.fromJson(requestBody, CourseRequest.class);
			
			System.out.println(request.getID());
			System.out.println(request.getCourseName());
			System.out.println(request.getGrade() + "\n");
			
			try (Connection conn = DriverManager.getConnection(databaseURL))
			{
				boolean updated = SQLiteTest.updateCourseGradeForStudent(conn, request.getID(), request.getCourseName(), request.getGrade());
				
				if (updated)
				{
					response = """
							{
								"message": "Course grade successfully updated"
							}
							""";
							
					statusCode = 200;
				}	
				else
				{
					response = """
							{
								"message": "Grade could not be updated"
							}
							""";
					
					statusCode = 404;
				}
				
			}
			catch (SQLException e)
			{
				response = """
							{
								"error": "Grade could not be updated"
							}
							""";
			}
		}
		else if (method.equals("DELETE"))
		{
			InputStream input = exchange.getRequestBody();
			
			byte[] requestBytes = input.readAllBytes();
			
			String requestBody = new String(requestBytes, StandardCharsets.UTF_8);
			
			System.out.println("DELETE body: ");
			System.out.println(requestBody + "\n");
			
			response = """
						{
							"message": "POST request received >:("
						}
						""";
						
			Gson gson = new Gson();

			System.out.println("Gson loaded: " + (gson instanceof Gson) + "\n");
			
			CourseRequest request = gson.fromJson(requestBody, CourseRequest.class);
			
			System.out.println(request.getID());
			System.out.println(request.getCourseName());
			System.out.println(request.getGrade() + "\n");
			
			try (Connection conn = DriverManager.getConnection(databaseURL))
			{
				boolean deleted = SQLiteTest.removeCourseFromStudent(conn, request.getID(), request.getCourseName());
				
				if (deleted)
				{
					response = """
							{
								"message": "Course successfully removed"
							}
							""";
							
					statusCode = 200;
				}	
				else
				{
					response = """
							{
								"message": "Could not remove course"
							}
							""";
							
					statusCode = 404;
				}
				
			}
			catch (SQLException e)
			{
				response = """
							{
								"error": "Could not remove course"
							}
							""";
			}
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

		exchange.sendResponseHeaders(statusCode, responseBytes.length);
		
		try (OutputStream output = exchange.getResponseBody())
		{
			output.write(responseBytes);
		}
	}
}