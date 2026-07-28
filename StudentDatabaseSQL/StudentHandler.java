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
		
		String response;
		int statusCode = 500;
			
		if (method.equals("GET"))
		{
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
		}	
		else if (method.equals("POST"))
		{
			String requestBody = HttpUtils.readRequestBody(exchange);
			
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
			String requestBody = HttpUtils.readRequestBody(exchange);
			
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
			String requestBody = HttpUtils.readRequestBody(exchange);
			
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
		
		HttpUtils.sendJsonResponse(exchange, statusCode, response);
	}
}