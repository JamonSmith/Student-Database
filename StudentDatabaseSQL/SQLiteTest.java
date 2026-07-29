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
		try	
		{
			String query = """
							SELECT course_name, grade
							FROM grades
							WHERE student_id = ?;
							""";
			
			PreparedStatement ps = conn.prepareStatement(query);
			
			ps.setInt(1, student.getID());
			
			ResultSet rs = ps.executeQuery();
			
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
		catch (SQLException e)
		{
			System.err.println(RED + e.getMessage() + RESET + "\n");
		}
	}
	
	public static List<Student> getAllStudents(Connection conn)
	{
		List<Student> students = new ArrayList<>();
		
		try 
		{
			String query = """
							SELECT students.student_id, first_name, last_name, ROUND(AVG(grade), 2) AS \"average\"
							FROM students
							LEFT JOIN grades
							ON students.student_id = grades.student_id
							GROUP BY students.student_id
							ORDER BY students.student_id ASC;
							""";
							
			Statement s = conn.createStatement();
			
			ResultSet rs = s.executeQuery(query);
			
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
			
	public static boolean studentExists(Connection conn, int id)
	{
		try
		{
			String query = """
							SELECT * 
							FROM students
							WHERE student_id = ?;
							""";
							
			PreparedStatement ps = conn.prepareStatement(query);
							
			ps.setInt(1, id);
			
			ResultSet rs = ps.executeQuery();
			
			return rs.next();
		}
		catch (SQLException e)
		{
			System.err.println(RED + e.getMessage() + RESET + "\n");
			return false;
		}	
	}
		
	public static boolean addStudent(Connection conn, String first, String last)
	{
		try
		{
			String query = """
							INSERT INTO students (first_name, last_name)
							VALUES (?, ?);
							""";
							
			PreparedStatement ps = conn.prepareStatement(query);
			
			ps.setString(1, first.trim());
			ps.setString(2, last.trim());
			
			int rows = ps.executeUpdate();
			
			if (rows == 1)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (SQLException e)
		{
			System.err.println(RED + e.getMessage() + RESET + "\n");
			return false;
		}
	}
	
	public static DatabaseResult renameStudent(Connection conn, int id, String first, String last)
	{
		try 
		{
			if (!studentExists(conn, id))
			{
				return DatabaseResult.NOT_FOUND;
			}
			
			String query = """
							UPDATE students
							SET first_name = COALESCE(?, first_name), last_name = COALESCE(?, last_name) 
							WHERE student_id = ?;
							""";				
							
			PreparedStatement ps = conn.prepareStatement(query);
			
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
	
	public static boolean courseExistsForStudent(Connection conn, int id, String course)
	{
		try 
		{
			String query = """
							SELECT * 
							FROM grades
							WHERE student_id = ? AND course_name = ?;
							""";
							
			PreparedStatement ps = conn.prepareStatement(query);

			ps.setInt(1, id);
			ps.setString(2, course);
			
			ResultSet rs = ps.executeQuery();
			
			return rs.next();
		}
		catch (SQLException e)
		{
			System.err.println(RED + e.getMessage() + RESET + "\n");
			return false;
		}
	}
	
	public static boolean addCourseToStudent(Connection conn, int id, String course, Double grade)
	{
		try
		{
			if (!studentExists(conn, id))
			{
				return false;
			}
			
			if (courseExistsForStudent(conn, id, course))
			{
				return false;
			}
			
			String query = """
							INSERT INTO grades (student_id, course_name, grade)
							VALUES (?, ?, ?);
							""";
							
			PreparedStatement ps = conn.prepareStatement(query);
			
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
			return true;
		}
		catch (SQLException e)
		{
			System.err.println(RED + e.getMessage() + RESET + "\n");
			return false;
		}
	}
	
	public static boolean updateCourseGradeForStudent(Connection conn, int id, String course, double newGrade)
	{
		try
		{
			if (!studentExists(conn, id))
			{
				return false;
			}
			
			if (!courseExistsForStudent(conn, id, course))
			{
				return false;
			}
			
			String query = """
							UPDATE grades
							SET grade = ? 
							WHERE student_id = ?
							AND course_name = ?;
							""";
							
			PreparedStatement ps = conn.prepareStatement(query);
	
			ps.setDouble(1, newGrade);
			ps.setInt(2, id);
			ps.setString(3, course);
			
			ps.executeUpdate();
			return true;
		}
		catch (SQLException e)
		{
			System.err.println(RED + e.getMessage() + RESET + "\n");
			return false;
		}
	}
	
	public static boolean removeCourseFromStudent(Connection conn, int id, String course)
	{
		try
		{
			if (!studentExists(conn, id))
			{
				return false;
			}
			
			if (!courseExistsForStudent(conn, id, course))
			{
				return false;
			}
			
			String query = """
							DELETE FROM grades 
							WHERE student_id = ?
							AND course_name = ?;
							""";
							
			PreparedStatement ps = conn.prepareStatement(query);
			
			ps.setInt(1, id);
			ps.setString(2, course);
			
			ps.executeUpdate();
			return true;
		}
		catch (SQLException e)
		{
			System.err.println(RED + e.getMessage() + RESET + "\n");
			return false;
		}
	}
	
	public static boolean removeStudent(Connection conn, int id)
	{
		try
		{		
			if (!studentExists(conn, id))
			{
				return false;
			}
			
			String query = """
							DELETE FROM grades
							WHERE student_id = ?;
							""";
							
			PreparedStatement ps = conn.prepareStatement(query);
				
			ps.setInt(1, id);
	
			ps.executeUpdate();
			
			String query2 = """
							DELETE FROM students
							WHERE student_id = ?;
							""";
							
			PreparedStatement ps2 = conn.prepareStatement(query2);
			
			ps2.setInt(1, id);
			
			int rows = ps2.executeUpdate();
			
			if (rows == 1)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (SQLException e)
		{
			System.err.println(RED + e.getMessage() + RESET + "\n");
			return false;
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