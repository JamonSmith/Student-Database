import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

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
		String response;
		
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
		
		byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
		
		exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
		
		exchange.sendResponseHeaders(200, responseBytes.length);
		
		try (OutputStream output = exchange.getResponseBody())
		{
			output.write(responseBytes);
		}
	}
}