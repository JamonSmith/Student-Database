import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

class Student
{
	public static final String RESET = "\u001B[0m";
	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String YELLOW = "\u001B[33m";
	public static final String BLUE = "\u001B[34m";
	public static final String PURPLE = "\u001B[35m";
	public static final String CYAN = "\u001B[36m";
	
	private int id;
	private String firstName;
	private String lastName;
	private HashMap<String, Double> transcript = new HashMap<>();
	
	Student(int id, String firstName, String lastName)
	{
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public int getID()
	{
		return id;
	}
	
	public void setFName(String fn)
	{
		firstName = fn;
	}
	
	public String getFName()
	{
		return firstName;
	}
	
	public void setLName(String ln)
	{
		lastName = ln;
	}
	
	public String getLName()
	{
		return lastName;
	}
	
	public boolean hasCourse(String course)
	{
		return transcript.containsKey(course);
	}
	
	public double clampGrade(double grade)
	{
		if (grade > 100.0)
		{
			return 100.0;
		}
		else if (grade < 0.0)
		{
			return 0.0;
		}
		
		return grade;
	}
	
	public void addCourse(String course, double grade)
	{
		if (course.isEmpty() == true)
		{
			System.out.println("Must provide course name");
		}
		else if (hasCourse(course))
		{
			System.out.println(lastName + ", " + firstName + " already has course: " + course);
		}
		else
		{
			transcript.put(course, clampGrade(grade));
		}
	}
	
	public HashMap<String, Double> getTranscript()
	{
		return new HashMap<>(transcript);
	}
	
	public void printInfo()
	{
		System.out.println(id + "\t" + lastName + "\t" + firstName);
	}
}

public class SQLiteTest
{
	public static final String RESET = "\u001B[0m";
	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String YELLOW = "\u001B[33m";
	public static final String BLUE = "\u001B[34m";
	public static final String PURPLE = "\u001B[35m";
	public static final String CYAN = "\u001B[36m";
	
	private static final String url = "jdbc:sqlite:students.db";
		
	public static void displayStudents(Connection conn) throws SQLException
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
		
		System.out.println("====================================================\n");
		
		System.out.println("All Students\n");
		
		if (rs.next())
		{
			do
			{
				int id = rs.getInt("student_id");
				String first = rs.getString("first_name");
				String last = rs.getString("last_name");
				double avg = rs.getDouble("average");
				
				System.out.print(GREEN + "\t" + id);
				System.out.print(CYAN + "\t" + last.substring(0, Math.min(7, last.length())));
				System.out.print("\t" + first.substring(0, Math.min(7, first.length())));
				System.out.println(RESET + "\t\t" + avg + "\n");
			}
			while(rs.next());
		}
		else
		{
			System.out.println("\t" + RED + "No students found\n" + RESET);
		}	
		
		System.out.println();
		System.out.println("====================================================\n");
	}		
		
	public static void showOneStudent(Connection conn, int stuID) throws SQLException
	{
		String query = """
						SELECT students.student_id, first_name, last_name, course_name, grade
						FROM students
						LEFT JOIN grades
						ON students.student_id = grades.student_id
						WHERE students.student_id = ?;
						""";
			
		PreparedStatement ps = conn.prepareStatement(query);
		
		ps.setInt(1, stuID);
				
		ResultSet rs = ps.executeQuery();
		
		System.out.println("====================================================\n");
		
		if (rs.next())
		{
			int id = rs.getInt("student_id");
			String first = rs.getString("first_name");
			String last = rs.getString("last_name");
			
			System.out.println(GREEN + id + "\n");
			System.out.println(CYAN + last + ", " + first + "\n" + RESET);
			System.out.println();
			
			do
			{
				String course = rs.getString("course_name");
				
				if (course != null)
				{
					double grade = rs.getDouble("grade");
					
					System.out.println(course.substring(0, Math.min(7, course.length())) + "\t\t" + grade);
					System.out.println();
				}
				else
				{
					System.out.println("No classes taken yet\n");
				}
			}
			while(rs.next());
			
			String query2 = """
							SELECT ROUND(AVG(grade), 2) AS \"average\"
							FROM grades
							WHERE student_id = ?;
							""";
							
			PreparedStatement ps2 = conn.prepareStatement(query2);
			
			ps2.setInt(1, stuID);

			ResultSet rs2 = ps2.executeQuery();
			
			if (rs2.next())
			{
				double avg = rs2.getDouble("average");
			
				System.out.println(PURPLE + "Average\t\t" + RESET + avg + "\n" + RESET);
			}
		}
		else
		{
			System.out.println("\t" + stuID + RED + " not found\n" + RESET);
		}	
		
		System.out.println("\n====================================================\n");
	}
	
	public static void addStudent(Connection conn, String first, String last) throws SQLException
	{
		String query = """
						INSERT INTO students (first_name, last_name)
						VALUES (?, ?);
						""";
						
		PreparedStatement ps = conn.prepareStatement(query);
		
		ps.setString(1, first);
		ps.setString(2, last);
		
		int rows = ps.executeUpdate();
		
		if (rows == 1)
		{
			System.out.println(CYAN + "\nRows affected: " + RESET + rows);
			System.out.println(CYAN + "Added:\t\t" + RESET + last + ", " + first + "\n");
		}
		else
		{
			System.out.println(RED + "\nStudent could not be added" + RESET);
			System.out.println("\n");
		}
	}
	
	public static void renameStudent(Connection conn, int id, String first, String last) throws SQLException
	{
		String query = """
						UPDATE students
						SET first_name = ?, last_name = ? 
						WHERE student_id = ?;
						""";				
						
		PreparedStatement ps = conn.prepareStatement(query);
		
		ps.setString(1, first);
		ps.setString(2, last);
		ps.setInt(3, id);
			
		int rows = ps.executeUpdate();
		
		if (rows == 1)
		{
			System.out.println(CYAN + "\nRows affected: " + RESET + rows);
			System.out.println(id + CYAN + " name changed to:\t" + RESET + last + ", " + first);
			System.out.println("\n");
		}
		else
		{
			System.out.println(RED + "\nStudent not found" + RESET);
			System.out.println("\n");
		}
	}
	
	public static void addCourseToStudent(Connection conn, int id, String course, double grade) throws SQLException
	{
		String query = """
						SELECT * 
						FROM students
						WHERE student_id = ?;
						""";
						
		PreparedStatement ps = conn.prepareStatement(query);
	
		ps.setInt(1, id);
		
		ResultSet rs = ps.executeQuery();
		
		if (rs.next())
		{
			String query2 = """
							INSERT INTO grades (student_id, course_name, grade)
							VALUES (?, ?, ?);
							""";
							
			PreparedStatement ps2 = conn.prepareStatement(query2);
			
			ps2.setInt(1, id);
			ps2.setString(2, course);
			ps2.setDouble(3, grade);
			
			int rows = ps2.executeUpdate();
			
			System.out.println(CYAN + "\nRows affected: " + RESET + rows);
			System.out.println(CYAN + "Student: " + RESET + id);
			System.out.println(CYAN + "Course:\t" + RESET + course);
			System.out.println(CYAN + "Grade:\t" + RESET + grade);
			System.out.println("\n");
		}
		else
		{
			System.out.println(RED + "\nStudent not found" + RESET);
			System.out.println("\n");
		}
	}
	
	public static void main(String[] args)
	{
		System.out.println();
		
		try (Connection conn = DriverManager.getConnection(url))
		{
			System.out.println(GREEN + "Connection to " + RESET + url + GREEN + " successful!\n" + RESET);
			
			Scanner sc = new Scanner(System.in);
			
			while (true)
			{
				System.out.println("1.) Show all students");
				System.out.println("2.) Add a student");
				System.out.println("3.) Rename a student");
				System.out.println("4.) Add course to a student");
				System.out.println("0.) Show a student");
				System.out.println("-1.)\tPress \'-1\' then \'Enter\' to exit");
				
				int choice = sc.nextInt();
				sc.nextLine();
				System.out.println();
				
				
				if (choice == 1)
				{
					displayStudents(conn);
					
					System.out.println();
				}
				else if (choice == 2)
				{
					String fn = sc.nextLine();
					String ln = sc.nextLine();
					
					addStudent(conn, fn, ln);
				}
				else if (choice == 3)
				{
					int id = sc.nextInt();
					sc.nextLine();
					String fn = sc.nextLine();
					String ln = sc.nextLine();
					
					renameStudent(conn, id, fn, ln);
				}
				else if (choice == 4)
				{
					int id = sc.nextInt();
					sc.nextLine();
					String course = sc.nextLine();
					double grade = sc.nextDouble();
					sc.nextLine();
					
					addCourseToStudent(conn, id, course, grade);
				}
				else if (choice == 0)
				{
					System.out.print(CYAN + "ID number to search: " + RESET);
					
					int input = sc.nextInt();
					sc.nextLine();
					System.out.println();
					
					showOneStudent(conn, input);
					
					System.out.println();
				}
				else if (choice == -1)
				{
					System.out.println(GREEN + "Thank you, goodbye\n" + RESET);
					break;
				}
				else
				{
					System.out.println(RED + "Invalid request\n" + RESET);
				}
			}
		}
		catch (SQLException e)
		{
			System.out.println(RED + "Connection to " + RESET + url + RED + " failed");
			System.out.println(e.getMessage() + RESET);
		}
	}
}