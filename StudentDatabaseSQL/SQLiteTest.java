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
	public static final String UNDERLINE = "\u001B[4m";
	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String YELLOW = "\u001B[33m";
	public static final String BLUE = "\u001B[34m";
	public static final String PURPLE = "\u001B[35m";
	public static final String CYAN = "\u001B[36m";
	
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
			System.out.println(RED + "Could not retrieve student's courses");
			System.out.println(e.getMessage() + RESET + "\n");
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
			
			System.out.println(RED + "Could not retrieve students");
			System.out.println(e.getMessage() + RESET + "\n");
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
			System.out.println(RED + "Something went wrong");
			System.out.println(e.getMessage() + RESET);
			System.out.println("\n");
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
				System.out.println("\n====================================================\n");
				System.out.println(GREEN + "Added:");
				System.out.println(CYAN + "Student:\t\t" + RESET + last + ", " + first + "\n");
				System.out.println("====================================================\n\n");
				return true;
			}
			else
			{
				System.out.println(RED + "\nStudent could not be added\n" + RESET);
				System.out.println("====================================================\n\n");
				return false;
			}
		}
		catch (SQLException e)
		{
			System.out.println(RED + "\tSomething went wrong");
			System.out.println("\t" + e.getMessage() + RESET);
			System.out.println("\n");
			return false;
		}
	}
	
	public static boolean renameStudent(Connection conn, int id, String first, String last)
	{
		try 
		{
			if (!studentExists(conn, id))
			{
				System.out.println(RED + "\nStudent not found" + RESET);
				System.out.println("\n");
				return false;
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
			
			System.out.println("\n====================================================\n");
			System.out.println(GREEN + "Student Name Updated:" + RESET);
			System.out.println(id + CYAN + " name changed to:\t" + RESET + last + ", " + first);
			System.out.println("\n====================================================\n\n");
			return true;
		}
		catch (SQLException e)
		{
			System.out.println(RED + "Could not rename student");
			System.out.println(e.getMessage() + RESET);
			System.out.println("\n");
			return false;
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
			System.out.println(RED + "Something went wrong");
			System.out.println(e.getMessage() + RESET);
			System.out.println("\n");
			return false;
		}
	}
	
	public static boolean addCourseToStudent(Connection conn, int id, String course, Double grade)
	{
		try
		{
			if (!studentExists(conn, id))
			{
				System.out.println(RED + "\nStudent not found" + RESET);
				System.out.println("\n");
				return false;
			}
			
			if (courseExistsForStudent(conn, id, course))
			{
				System.out.println(RED + "Student has already taken " + RESET + course);
				System.out.println("\n====================================================\n");
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
			
			System.out.println(GREEN + "Added:");
			System.out.println(CYAN + "Course:\t" + RESET + course);
			System.out.println(CYAN + "Grade:\t" + RESET + grade);
			System.out.println("\n====================================================\n\n");
			return true;
		}
		catch (SQLException e)
		{
			System.out.println(RED + "Something went wrong");
			System.out.println(e.getMessage() + RESET);
			System.out.println("\n");
			return false;
		}
	}
	
	public static boolean updateCourseGradeForStudent(Connection conn, int id, String course, double newGrade)
	{
		try
		{
			if (!studentExists(conn, id))
			{
				System.out.println(RED + "\nStudent not found" + RESET);
				System.out.println("\n====================================================\n\n");
				return false;
			}
			
			if (!courseExistsForStudent(conn, id, course))
			{
				System.out.println(RED + "\nStudent has not taken " + RESET + course);
				System.out.println("\n====================================================\n\n");
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
			
			System.out.println("\n====================================================\n");
			System.out.println(GREEN + "Updated: ");
			System.out.println(CYAN + "Student: " + RESET + id);
			System.out.println(course + CYAN + " grade changed to: " + RESET + newGrade);
			System.out.println("\n====================================================\n\n");
			return true;
		}
		catch (SQLException e)
		{
			System.out.println(RED + "Something went wrong");
			System.out.println(e.getMessage() + RESET);
			System.out.println("\n");
			return false;
		}
	}
	
	public static boolean removeCourseFromStudent(Connection conn, int id, String course)
	{
		try
		{
			if (!studentExists(conn, id))
			{
				System.out.println(RED + "\nStudent not found" + RESET);
				return false;
			}
			
			if (!courseExistsForStudent(conn, id, course))
			{
				System.out.println(RED + "\nStudent has not taken " + RESET + course);
				System.out.println("\n====================================================\n\n");
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
			
			System.out.println(GREEN + "\nRemoved:");
			System.out.println(CYAN + "Course:\t" + RESET + course);
			System.out.println("\n====================================================\n\n");				
			return true;
		}
		catch (SQLException e)
		{
			System.out.println(RED + "Something went wrong");
			System.out.println(e.getMessage() + RESET);
			System.out.println("\n");
			return false;
		}
	}
	
	public static boolean removeStudent(Connection conn, int id)
	{
		try
		{		
			if (!studentExists(conn, id))
			{
				System.out.println(RED + "\nStudent not found" + RESET);
				System.out.println("\n====================================================\n\n");
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
				System.out.println(GREEN + "Removed:");
				System.out.println(CYAN + "Student: " + RESET + id);
				System.out.println("\n====================================================\n");
				return true;
			}
			else
			{
				System.out.println(RED + "\nStudent not found\n" + RESET);
				System.out.println("====================================================\n");
				return false;
			}
		}
		catch (SQLException e)
		{
			System.out.println(RED + "Something went wrong");
			System.out.println(e.getMessage() + RESET);
			System.out.println("\n");
			return false;
		}	
	}
	
	public static void main(String[] args)
	{
		System.out.println();
		
		try 
		{
			HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
			
			server.createContext("/test", new TestHandler());
			server.createContext("/students", new StudentHandler(url));
			server.createContext("/courses", new CourseHandler(url));
			
			server.start();
			
			System.out.println(GREEN + "Server running at http://localhost:8000/test\n" + RESET);
			
			System.out.println(GREEN + "json file at http://localhost:8000/students" + RESET);
		}
		catch (IOException e)
		{
			System.out.println(RED + "Could not start server");
			System.out.println(e.getMessage() + RESET);
		}
	}
}