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
		
		String response;
		int statusCode = 500;
			
		if (method.equals("POST"))
		{
			String requestBody = HttpUtils.readRequestBody(exchange);
			
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
			String requestBody = HttpUtils.readRequestBody(exchange);
			
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
			String requestBody = HttpUtils.readRequestBody(exchange);
			
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
		
		HttpUtils.sendJsonResponse(exchange, statusCode, response);
	}
}