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
	
	public StudentHandler(String databaseURL)
	{
		this.databaseURL = databaseURL;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException
	{
		exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
		exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
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
			
		if (method.equals("GET"))
		{
			try (Connection conn = DriverManager.getConnection(databaseURL))
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
				
				statusCode = 200;
				
				response = jsonArray.toString();
			}
			catch (SQLException e)
			{
				response = """
							{
								"error": "Could not retrieve data"
							}
							""";
							
				statusCode = 500;
			}
		}	
		else if (method.equals("POST"))
		{
			String requestBody = readRequestBody(exchange);
			
			Gson gson = new Gson();

			StudentRequest request = gson.fromJson(requestBody, StudentRequest.class);
			
			if (request.getFirstName() == null || request.getFirstName().isBlank() || request.getLastName() == null || request.getLastName().isBlank())
			{
				response = """
							{
								"error": "First name and last name must be provided"
							}
							""";
				
				statusCode = 400;
			}
			else
			{
				try (Connection conn = DriverManager.getConnection(databaseURL))
				{
					boolean added = SQLiteTest.addStudent(conn, request.getFirstName(), request.getLastName());
					
					if (added)
					{
						response = """
								{
									"message": "Student successfully added"
								}
								""";
						
						statusCode = 200;
					}
					else
					{
						response = """
									{
										"error": "Could not add student"
									}
									""";
	
						statusCode = 500;
					}
				}
				catch (SQLException e)
				{
					response = """
								{
									"error": "Could not add student"
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

			StudentRequest request = gson.fromJson(requestBody, StudentRequest.class);
			
			if (request.getID() == null)
			{
				response = """
						{
							"error": "Must provide ID number"
						}
						""";
	
				statusCode = 400;
			}
			else if ((request.getFirstName() == null || request.getFirstName().isBlank()) && (request.getLastName() == null || request.getLastName().isBlank()))
			{
				response = """
						{
							"error": "Must provide first name or last name"
						}
						""";
	
				statusCode = 400;
			}
			else
			{
				try (Connection conn = DriverManager.getConnection(databaseURL))
				{
					boolean renamed = SQLiteTest.renameStudent(conn, request.getID(), request.getFirstName(), request.getLastName());
					
					if (renamed)
					{
						response = """
								{
									"message": "Student successfully renamed"
								}
								""";
						
						statusCode = 200;
					}	
					else
					{
						response = """
								{
									"error": "Student could not be renamed"
								}
								""";
								
						statusCode = 404;
					}
					
				}
				catch (SQLException e)
				{
					response = """
							{
								"error": "Could not rename student"
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

			StudentRequest request = gson.fromJson(requestBody, StudentRequest.class);
			
			if (request.getID() == null)
			{
				response = """
						{
							"error": "Must provide ID number"
						}
						""";
						
				statusCode = 400;
			}
			else
			{
				try (Connection conn = DriverManager.getConnection(databaseURL))
				{
					boolean deleted = SQLiteTest.removeStudent(conn, request.getID());
					
					if (deleted)
					{
						response = """
								{
									"message": "Student successfully deleted"
								}
								""";
								
						statusCode = 200;
					}	
					else
					{
						response = """
								{
									"error": "Could not delete student"
								}
								""";
								
						statusCode = 404;
					}
					
				}
				catch (SQLException e)
				{
					response = """
								{
									"error": "Could not delete student"
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