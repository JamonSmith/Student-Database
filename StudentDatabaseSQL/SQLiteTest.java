import java.util.List;
import java.util.ArrayList;

import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public class SQLiteTest
{
	public static final String RESET = "\u001B[0m";
	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	
	private static final String url = "jdbc:sqlite:students.db";
	
	public static void loadStudentCourses(Connection conn, Student student)
	{
		String query = """
						SELECT course_name, grade
						FROM grades
						WHERE student_id = ?;
						""";
			
		try (PreparedStatement ps = conn.prepareStatement(query))
		{	
			ps.setInt(1, student.getID());
			
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					String course = rs.getString("course_name");
					double gradeVal = rs.getDouble("grade");
					
					Double grade = null;
					
					if (!rs.wasNull())
					{
						grade = gradeVal;
					}
					
					Course c = new Course(course, grade);
					
					student.getCourses().add(c);
				}
			}
		}
		catch (SQLException e)
		{
			System.err.println(RED + e.getMessage() + RESET + "\n");
		}
	}
	
	public static List<Student> getAllStudents(Connection conn)
	{
		List<Student> students = new ArrayList<>();
		
		String query = """
						SELECT students.student_id, first_name, last_name, ROUND(AVG(grade), 2) AS \"average\"
						FROM students
						LEFT JOIN grades
						ON students.student_id = grades.student_id
						GROUP BY students.student_id
						ORDER BY students.student_id ASC;
						""";
							
		try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(query))
		{
			while (rs.next())
			{
				int id = rs.getInt("student_id");
				String first = rs.getString("first_name");
				String last = rs.getString("last_name");
				double avg = rs.getDouble("average");
				
				Double average = null;
				
				if (!rs.wasNull())
				{
					average = avg;
				}
				
				Student stu = new Student(id, first, last, average);
				
				loadStudentCourses(conn, stu);
				
				students.add(stu);
			}
		}
		catch (SQLException e)
		{
			System.err.println(RED + e.getMessage() + RESET + "\n");
		}
		
		return students;
	}		
	
	public static boolean studentExists(Connection conn, int id) throws SQLException
	{
		String query = """
						SELECT * 
						FROM students
						WHERE student_id = ?;
						""";
						
		try (PreparedStatement ps = conn.prepareStatement(query))
		{
			ps.setInt(1, id);
			
			try (ResultSet rs = ps.executeQuery())
			{
				return rs.next();
			}
		}
	}
		
	public static DatabaseResult addStudent(Connection conn, String first, String last)
	{
		String query = """
						INSERT INTO students (first_name, last_name)
						VALUES (?, ?);
						""";
			
		try (PreparedStatement ps = conn.prepareStatement(query))
		{
			ps.setString(1, first.trim());
			ps.setString(2, last.trim());
			
			int rows = ps.executeUpdate();
			
			if (rows == 1)
			{
				return DatabaseResult.SUCCESS;
			}
			else
			{
				return DatabaseResult.ERROR;
			}
		}
		catch (SQLException e)
		{
			System.err.println(RED + e.getMessage() + RESET + "\n");
			return DatabaseResult.ERROR;
		}
	}
	
	public static DatabaseResult renameStudent(Connection conn, int id, String first, String last)
	{
		String query = """
						UPDATE students
						SET first_name = COALESCE(?, first_name), last_name = COALESCE(?, last_name) 
						WHERE student_id = ?;
						""";				
							
		try (PreparedStatement ps = conn.prepareStatement(query))
		{	
			if (!studentExists(conn, id))
			{
				return DatabaseResult.NOT_FOUND;
			}
		
			if (first == null || first.isBlank())
			{
				ps.setNull(1, java.sql.Types.VARCHAR);
			}
			else
			{
				ps.setString(1, first.trim());				
			}
			
			if (last == null || last.isBlank())
			{
				ps.setNull(2, java.sql.Types.VARCHAR);
			}
			else
			{
				ps.setString(2, last.trim());				
			}
			
			ps.setInt(3, id);
				
			ps.executeUpdate();			
			return DatabaseResult.SUCCESS;
		}
		catch (SQLException e)
		{
			System.err.println(RED + e.getMessage() + RESET + "\n");
			return DatabaseResult.ERROR;
		}
	}
	
	public static boolean courseExistsForStudent(Connection conn, int id, String course) throws SQLException
	{
		String query = """
						SELECT * 
						FROM grades
						WHERE student_id = ? AND course_name = ?;
						""";
						
		try (PreparedStatement ps = conn.prepareStatement(query))
		{
			ps.setInt(1, id);
			ps.setString(2, course);
			
			try (ResultSet rs = ps.executeQuery())
			{
				return rs.next();
			}
		}
	}
	
	public static DatabaseResult addCourseToStudent(Connection conn, int id, String course, Double grade)
	{
		String query = """
						INSERT INTO grades (student_id, course_name, grade)
						VALUES (?, ?, ?);
						""";
							
		try (PreparedStatement ps = conn.prepareStatement(query))
		{
			if (!studentExists(conn, id))
			{
				return DatabaseResult.NOT_FOUND;
			}
			
			if (courseExistsForStudent(conn, id, course))
			{
				return DatabaseResult.EXISTS;
			}
		
			ps.setInt(1, id);
			ps.setString(2, course);
			
			if (grade == null)
			{
				ps.setNull(3, java.sql.Types.REAL);
			}
			else
			{
				ps.setDouble(3, grade);
			}
			
			ps.executeUpdate();
			return DatabaseResult.SUCCESS;
		}
		catch (SQLException e)
		{
			System.err.println(RED + e.getMessage() + RESET + "\n");
			return DatabaseResult.ERROR;
		}
	}
	
	public static DatabaseResult updateCourseGradeForStudent(Connection conn, int id, String course, double newGrade)
	{
		String query = """
						UPDATE grades
						SET grade = ? 
						WHERE student_id = ?
						AND course_name = ?;
						""";
		
		try (PreparedStatement ps = conn.prepareStatement(query))
		{
			if (!studentExists(conn, id))
			{
				return DatabaseResult.NOT_FOUND;
			}
			
			if (!courseExistsForStudent(conn, id, course))
			{
				return DatabaseResult.NOT_FOUND;
			}
			
			ps.setDouble(1, newGrade);
			ps.setInt(2, id);
			ps.setString(3, course);
			
			ps.executeUpdate();
			return DatabaseResult.SUCCESS;
		}
		catch (SQLException e)
		{
			System.err.println(RED + e.getMessage() + RESET + "\n");
			return DatabaseResult.ERROR;
		}
	}
	
	public static DatabaseResult removeCourseFromStudent(Connection conn, int id, String course)
	{
		String query = """
						DELETE FROM grades 
						WHERE student_id = ?
						AND course_name = ?;
						""";
							
		try (PreparedStatement ps = conn.prepareStatement(query))
		{
			if (!studentExists(conn, id))
			{
				return DatabaseResult.NOT_FOUND;
			}
			
			if (!courseExistsForStudent(conn, id, course))
			{
				return DatabaseResult.NOT_FOUND;
			}
		
			ps.setInt(1, id);
			ps.setString(2, course);
			
			ps.executeUpdate();
			return DatabaseResult.SUCCESS;
		}
		catch (SQLException e)
		{
			System.err.println(RED + e.getMessage() + RESET + "\n");
			return DatabaseResult.ERROR;
		}
	}
	
	public static DatabaseResult removeStudent(Connection conn, int id)
	{
		String query = """
						DELETE FROM grades
						WHERE student_id = ?;
						""";
					
		String query2 = """
						DELETE FROM students
						WHERE student_id = ?;
						""";
					
		try
		{		
			if (!studentExists(conn, id))
			{
				return DatabaseResult.NOT_FOUND;
			}
			
			conn.setAutoCommit(false);

			try (PreparedStatement ps = conn.prepareStatement(query); PreparedStatement ps2 = conn.prepareStatement(query2))
			{
				ps.setInt(1, id);
	
				ps.executeUpdate();
				
				ps2.setInt(1, id);
				
				int rows = ps2.executeUpdate();
				
				if (rows != 1)
				{
					conn.rollback();
					return DatabaseResult.ERROR;
				}
			
				conn.commit();
				return DatabaseResult.SUCCESS;
			}
			catch (SQLException e)
			{
				conn.rollback();
				System.err.println(RED + e.getMessage() + RESET + "\n");
				return DatabaseResult.ERROR;
			}
			finally
			{
				conn.setAutoCommit(true);
			}
		}
		catch (SQLException e)
		{
			System.err.println(RED + e.getMessage() + RESET + "\n");
			return DatabaseResult.ERROR;
		}	
	}
	
	public static void main(String[] args)
	{
		System.out.println();
		
		try 
		{
			HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
			
			server.createContext("/students", new StudentHandler(url));
			server.createContext("/courses", new CourseHandler(url));
			
			server.start();
			
			System.out.println(GREEN + "json file at http://localhost:8000/students\n" + RESET);
			System.out.println(GREEN + "json file at http://localhost:8000/courses" + RESET);
		}
		catch (IOException e)
		{
			System.out.println(RED + "Could not start server");
			System.err.println(e.getMessage() + RESET + "\n");
		}
	}
}