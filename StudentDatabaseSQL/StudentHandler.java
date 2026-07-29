import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import com.google.gson.Gson;

import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.List;

public class StudentHandler implements HttpHandler
{
	private final String databaseURL;
	private final Gson gson = new Gson();
	
	public StudentHandler(String databaseURL)
	{
		this.databaseURL = databaseURL;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException
	{
		String method = HttpUtils.setupCorsAndGetMethod(exchange, "GET, POST, PUT, DELETE, OPTIONS");
		
		if (HttpUtils.handleOptionsRequest(exchange))
		{
			return;
		}
		
		if (method.equals("GET"))
		{
			handleGet(exchange);
		}	
		else if (method.equals("POST"))
		{
			handlePost(exchange);
		}	
		else if (method.equals("PUT"))
		{
			handlePut(exchange);
		}
		else if (method.equals("DELETE"))
		{
			handleDelete(exchange);
		}
		else		
		{
			handleInvalidMethod(exchange);
		}
	}
	
	private void handleGet(HttpExchange exchange) throws IOException
	{
		String response;
		int statusCode = 500;
		
		try (Connection conn = DriverManager.getConnection(databaseURL))
		{
			List<Student> students = SQLiteTest.getAllStudents(conn);
			
			response = gson.toJson(students);
			
			statusCode = 200;
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
			
		HttpUtils.sendJsonResponse(exchange, statusCode, response);
	}
	
	private void handlePost(HttpExchange exchange) throws IOException
	{
		String response;
		int statusCode = 500;
		
		StudentRequest request = readStudentRequest(exchange);
		
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
				DatabaseResult result = SQLiteTest.addStudent(conn, request.getFirstName(), request.getLastName());
				
				if (result == DatabaseResult.SUCCESS)
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
		
		HttpUtils.sendJsonResponse(exchange, statusCode, response);
	}
	
	private void handlePut(HttpExchange exchange) throws IOException
	{
		String response;
		int statusCode = 500;
		
		StudentRequest request = readStudentRequest(exchange);
		
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
				DatabaseResult result = SQLiteTest.renameStudent(conn, request.getID(), request.getFirstName(), request.getLastName());
				
				if (result == DatabaseResult.SUCCESS)
				{
					response = """
							{
								"message": "Student successfully renamed"
							}
							""";
					
					statusCode = 200;
				}	
				else if (result == DatabaseResult.NOT_FOUND)
				{
					response = """
							{
								"error": "Student not found"
							}
							""";
							
					statusCode = 404;
				}
				else
				{
					response = """
							{
								"error": "Student could not be renamed"
							}
							""";
							
					statusCode = 500;
				}
				
			}
			catch (SQLException e)
			{
				response = """
						{
							"error": "Student could not be renamed"
						}
						""";
	
				statusCode = 500;
			}
		}
		
		HttpUtils.sendJsonResponse(exchange, statusCode, response);
	}
	
	private void handleDelete(HttpExchange exchange) throws IOException
	{
		String response;
		int statusCode = 500;
		
		StudentRequest request = readStudentRequest(exchange);
		
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
				DatabaseResult result = SQLiteTest.removeStudent(conn, request.getID());
				
				if (result == DatabaseResult.SUCCESS)
				{
					response = """
							{
								"message": "Student successfully deleted"
							}
							""";
							
					statusCode = 200;
				}	
				else if (result == DatabaseResult.NOT_FOUND)
				{
					response = """
							{
								"error": "Student could not be found"
							}
							""";
							
					statusCode = 404;
				}	
				else
				{
					response = """
							{
								"error": "Could not delete student"
							}
							""";
							
					statusCode = 500;
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
		
		HttpUtils.sendJsonResponse(exchange, statusCode, response);
	}
	
	private void handleInvalidMethod(HttpExchange exchange) throws IOException
	{
		String response = """
						{
							"error": "Method not allowed"
						}
						""";
						
		int statusCode = 405;
			
		HttpUtils.sendJsonResponse(exchange, statusCode, response);
	}
	
	private StudentRequest readStudentRequest(HttpExchange exchange) throws IOException
	{
		String requestBody = HttpUtils.readRequestBody(exchange);
			
		return gson.fromJson(requestBody, StudentRequest.class);
	}
}