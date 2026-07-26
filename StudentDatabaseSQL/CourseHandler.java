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
		int statusCode = 500;
			
		if (method.equals("POST"))
		{
			String requestBody = readRequestBody(exchange);
			
			Gson gson = new Gson();

			CourseRequest request = gson.fromJson(requestBody, CourseRequest.class);
			
			if (request.getID() == null)
			{
				response = """
						{
							"error": "Must provide ID number"
						}
						""";
						
				statusCode = 400;
			}
			else if (request.getCourseName() == null || request.getCourseName().isBlank())
			{
				response = """
						{
							"error": "Must provide course name"
						}
						""";
	
				statusCode = 400;
			}
			else
			{
				try (Connection conn = DriverManager.getConnection(databaseURL))
				{
					boolean added = SQLiteTest.addCourseToStudent(conn, request.getID(), request.getCourseName(), request.getGrade());
					
					if (added)
					{
						response = """
								{
									"message": "Course successfully added"
								}
								""";
								
						statusCode = 200;
					}
					else
					{
						response = """
								{
									"error": "Could not add course"
								}
								""";
								
						statusCode = 500;
					}
				}
				catch (SQLException e)
				{
					response = """
								{
									"error": "Could not add course"
								}
								""";
								
					statusCode = 500;
				}
			}
		}	
		else if (method.equals("PUT"))
		{
			String requestBody = readRequestBody(exchange);
			
			Gson gson = new Gson();

			CourseRequest request = gson.fromJson(requestBody, CourseRequest.class);
			
			if (request.getID() == null || request.getCourseName() == null || request.getCourseName().isBlank() || request.getGrade() == null)
			{
				response = """
							{
								"error": "All fields must be provided"
							}
							""";
				
				statusCode = 400;
			}
			else
			{
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
										"error": "Grade could not be updated"
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
								
					statusCode = 500;
				}
			}	
		}
		else if (method.equals("DELETE"))
		{
			String requestBody = readRequestBody(exchange);
			
			Gson gson = new Gson();

			CourseRequest request = gson.fromJson(requestBody, CourseRequest.class);
			
			if (request.getID() == null)
			{
				response = """
						{
							"error": "Must provide ID number"
						}
						""";
						
				statusCode = 400;
			}
			else if (request.getCourseName() == null || request.getCourseName().isBlank())
			{
				response = """
						{
							"error": "Must provide course name"
						}
						""";
						
				statusCode = 400;
			}
			else
			{
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
									"error": "Could not remove course"
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
								
					statusCode = 500;
				}
			}
			
		}
		else		
		{
			response = """
						{
							"error": "Method not allowed"
						}
						""";
						
			statusCode = 405;
		}
		
		sendJsonResponse(exchange, statusCode, response);
	}
	
	private String readRequestBody(HttpExchange exchange) throws IOException
	{
		try (InputStream input = exchange.getRequestBody())
		{	
			byte[] requestBytes = input.readAllBytes();
			
			return new String(requestBytes, StandardCharsets.UTF_8);
		}
	}
	
	private void sendJsonResponse(HttpExchange exchange, int statusCode, String response) throws IOException
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