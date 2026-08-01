import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import com.google.gson.Gson;

import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CourseHandler implements HttpHandler
{
	private final String databaseURL;
	private final Gson gson = new Gson();
	
	public CourseHandler(String databaseURL)
	{
		this.databaseURL = databaseURL;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException
	{
		String method = HttpUtils.setupCorsAndGetMethod(exchange, "POST, PUT, DELETE, OPTIONS");
		
		if (HttpUtils.handleOptionsRequest(exchange))
		{
			return;
		}
		
		if (method.equals("POST"))
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
	
	private void handlePost(HttpExchange exchange) throws IOException
	{
		String response;
		int statusCode = 500;
		
		CourseRequest request = readCourseRequest(exchange);
		
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
				DatabaseResult result = SQLiteTest.addCourseToStudent(conn, request.getID(), request.getCourseName(), request.getGrade());
				
				if (result == DatabaseResult.SUCCESS)
				{
					response = """
							{
								"message": "Course successfully added"
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
				else if (result == DatabaseResult.EXISTS)
				{
					response = """
							{
								"error": "Student already has this course"
							}
							""";
							
					statusCode = 409;
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
		
		HttpUtils.sendJsonResponse(exchange, statusCode, response);
	}
	
	private void handlePut(HttpExchange exchange) throws IOException
	{
		String response;
		int statusCode = 500;
		
		CourseRequest request = readCourseRequest(exchange);
		
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
				DatabaseResult result = SQLiteTest.updateCourseGradeForStudent(conn, request.getID(), request.getCourseName(), request.getGrade());
				
				if (result == DatabaseResult.SUCCESS)
				{
					response = """
								{
									"message": "Course grade successfully updated"
								}
								""";
							
					statusCode = 200;
				}	
				else if (result == DatabaseResult.NOT_FOUND)
				{
					response = """
								{
									"error": "Student or course could not be found"
								}
								""";
					
					statusCode = 404;
				}
				else
				{
					response = """
								{
									"error": "Grade could not be updated"
								}
								""";
					
					statusCode = 500;
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
		
		HttpUtils.sendJsonResponse(exchange, statusCode, response);
	}
	
	private void handleDelete(HttpExchange exchange) throws IOException
	{
		String response;
		int statusCode = 500;
		
		CourseRequest request = readCourseRequest(exchange);
		
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
				DatabaseResult result = SQLiteTest.removeCourseFromStudent(conn, request.getID(), request.getCourseName());
				
				if (result == DatabaseResult.SUCCESS)
				{
					response = """
							{
								"message": "Course successfully removed"
							}
							""";
							
					statusCode = 200;
				}	
				else if (result == DatabaseResult.NOT_FOUND)
				{
					response = """
							{
								"error": "Student or course not found"
							}
							""";
							
					statusCode = 404;
				}
				else
				{
					response = """
							{
								"error": "Could not remove course"
							}
							""";
							
					statusCode = 500;
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
	
	private CourseRequest readCourseRequest(HttpExchange exchange) throws IOException
	{
		String requestBody = HttpUtils.readRequestBody(exchange);
			
		return gson.fromJson(requestBody, CourseRequest.class);
	}
}