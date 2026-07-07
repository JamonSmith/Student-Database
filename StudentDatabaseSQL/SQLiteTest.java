import java.util.Scanner;
import java.util.InputMismatchException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
	
	public static int getIntInput(Scanner sc, String prompt)
	{
		while (true)
		{
			System.out.print(prompt);
			
			try
			{
				int val = sc.nextInt();
				sc.nextLine();
				System.out.println();
				return val;
			}
			catch (InputMismatchException e)
			{
				sc.nextLine();
				System.out.println(RED + "\nEnter a valid integer\n" + RESET);
			}
		}
	}
	
	public static String getStringInput(Scanner sc, String prompt)
	{
		while (true)
		{
			System.out.print(prompt);
			
			String val = sc.nextLine().trim();
			
			if (val.isEmpty())
			{
				System.out.println(RED + "\nPlease provide an input\n" + RESET);
				System.out.println();
				continue;
			}
			
			return val;
		}
	}
	
	public static double getDoubleInput(Scanner sc, String prompt)
	{
		while (true)
		{
			System.out.print(prompt);
			
			try
			{
				double val = sc.nextDouble();
				sc.nextLine();
				System.out.println();
				return val;
			}
			catch (InputMismatchException e)
			{
				sc.nextLine();
				System.out.println(RED + "\nEnter a valid input\n" + RESET);
			}
		}
	}
		
	public static void displayStudents(Connection conn)
	{
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
			
			int sum = 0;
			
			if (rs.next())
			{
				do
				{
					int id = rs.getInt("student_id");
					String first = rs.getString("first_name");
					String last = rs.getString("last_name");
					double avg = rs.getDouble("average");
					sum++;
					
					System.out.print(GREEN + id);
					System.out.print(CYAN + "\t" + last.substring(0, Math.min(7, last.length())));
					System.out.print("\t" + first.substring(0, Math.min(7, first.length())));
					System.out.println(RESET + "\t\t" + avg + "\n");
				}
				while(rs.next());
				
				System.out.println(PURPLE + "Total Students:\t\t\t" + RESET + sum + "\n");
			}
			else
			{
				System.out.println(RED + "No students found\n" + RESET);
			}	
			
			System.out.println("====================================================\n");
		}
		catch (SQLException e)
		{
			System.out.println(RED + "Something went wrong");
			System.out.println(e.getMessage() + RESET);
			System.out.println("\n");
		}	
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
		
	public static boolean confirmStudent(Connection conn, Scanner sc, int id)
	{
		try
		{
			String query = """
							SELECT first_name, last_name
							FROM students
							WHERE student_id = ?;
							""";
							
			PreparedStatement ps = conn.prepareStatement(query);
			
			ps.setInt(1, id);
			
			ResultSet rs = ps.executeQuery();
			
			if (!rs.next())
			{
				System.out.println("====================================================\n");
				System.out.println(id + RED + " not found" + RESET);
				return false;
			}
			
			String first = rs.getString("first_name");
			String last = rs.getString("last_name");
			
			System.out.println("====================================================\n");
			System.out.println("Selected Student:\n");
			System.out.println(CYAN + "Student: " + RESET + last + ", " + first + "\n");
			System.out.println(RED + "Continue? (y/n):" + RESET);
			
			String confirm = sc.nextLine().toLowerCase();
			
			return confirm.equals("y") ||  confirm.equals("yes");
		}
		catch (SQLException e)
		{
			System.out.println(RED + "Something went wrong");
			System.out.println(e.getMessage() + RESET);
			System.out.println("\n");
			return false;
		}	
	}
		
	public static void showOneStudent(Connection conn, int stuID)
	{
		try
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
			
			if (!rs.next())
			{
				System.out.println(stuID + RED + " not found\n" + RESET);
				System.out.println("====================================================\n");
				return;
			}	
			
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
			
			System.out.println("====================================================\n");
		}
		catch (SQLException e)
		{
			System.out.println(RED + "Something went wrong");
			System.out.println(e.getMessage() + RESET);
			System.out.println("\n");
		}	
	}
	
	public static void addStudent(Connection conn, String first, String last)
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
			}
			else
			{
				System.out.println(RED + "\nStudent could not be added\n" + RESET);
				System.out.println("====================================================\n\n");
			}
		}
		catch (SQLException e)
		{
			System.out.println(RED + "\tSomething went wrong");
			System.out.println("\t" + e.getMessage() + RESET);
			System.out.println("\n");
		}
	}
	
	public static void renameStudent(Connection conn, int id, String first, String last)
	{
		try 
		{
			if (!studentExists(conn, id))
			{
				System.out.println(RED + "\nStudent not found" + RESET);
				System.out.println("\n");
				return;
			}
			
			String query = """
							UPDATE students
							SET first_name = ?, last_name = ? 
							WHERE student_id = ?;
							""";				
							
			PreparedStatement ps = conn.prepareStatement(query);
			
			ps.setString(1, first.trim());
			ps.setString(2, last.trim());
			ps.setInt(3, id);
				
			ps.executeUpdate();
			
			System.out.println("\n====================================================\n");
			System.out.println(GREEN + "Student Name Updated:" + RESET);
			System.out.println(id + CYAN + " name changed to:\t" + RESET + last + ", " + first);
			System.out.println("\n====================================================\n\n");
		}
		catch (SQLException e)
		{
			System.out.println(RED + "Something went wrong");
			System.out.println(e.getMessage() + RESET);
			System.out.println("\n");
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
	
	public static void addCourseToStudent(Connection conn, int id, String course, double grade)
	{
		try
		{
			if (!studentExists(conn, id))
			{
				System.out.println(RED + "\nStudent not found" + RESET);
				System.out.println("\n");
				return;
			}
			
			if (courseExistsForStudent(conn, id, course))
			{
				System.out.println(RED + "Student has already taken " + RESET + course);
				System.out.println("\n====================================================\n");
				return;
			}
			
			String query = """
							INSERT INTO grades (student_id, course_name, grade)
							VALUES (?, ?, ?);
							""";
							
			PreparedStatement ps = conn.prepareStatement(query);
			
			ps.setInt(1, id);
			ps.setString(2, course);
			ps.setDouble(3, grade);
			
			ps.executeUpdate();
			
			System.out.println(GREEN + "Added:");
			System.out.println(CYAN + "Course:\t" + RESET + course);
			System.out.println(CYAN + "Grade:\t" + RESET + grade);
			System.out.println("\n====================================================\n\n");
		}
		catch (SQLException e)
		{
			System.out.println(RED + "Something went wrong");
			System.out.println(e.getMessage() + RESET);
			System.out.println("\n");
		}
	}
	
	public static void updateCourseGradeForStudent(Connection conn, int id, String course, double newGrade)
	{
		try
		{
			if (!studentExists(conn, id))
			{
				System.out.println(RED + "\nStudent not found" + RESET);
				System.out.println("\n====================================================\n\n");
				return;
			}
			
			if (!courseExistsForStudent(conn, id, course))
			{
				System.out.println(RED + "\nStudent has not taken " + RESET + course);
				System.out.println("\n====================================================\n\n");
				return;
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
		}
		catch (SQLException e)
		{
			System.out.println(RED + "Something went wrong");
			System.out.println(e.getMessage() + RESET);
			System.out.println("\n");
		}
	}
	
	public static void removeCourseFromStudent(Connection conn, int id, String course)
	{
		try
		{
			if (!studentExists(conn, id))
			{
				System.out.println(RED + "\nStudent not found" + RESET);
				return;
			}
			
			if (!courseExistsForStudent(conn, id, course))
			{
				System.out.println(RED + "\nStudent has not taken " + RESET + course);
				System.out.println("\n====================================================\n\n");
				return;
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
			
		}
		catch (SQLException e)
		{
			System.out.println(RED + "Something went wrong");
			System.out.println(e.getMessage() + RESET);
			System.out.println("\n");
		}
	}
	
	public static void removeStudent(Connection conn, int id)
	{
		try
		{		
			if (!studentExists(conn, id))
			{
				System.out.println(RED + "\nStudent not found" + RESET);
				System.out.println("\n====================================================\n\n");
				return;
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
			}
			else
			{
				System.out.println(RED + "\nStudent not found\n" + RESET);
				System.out.println("====================================================\n");
			}
		}
		catch (SQLException e)
		{
			System.out.println(RED + "Something went wrong");
			System.out.println(e.getMessage() + RESET);
			System.out.println("\n");
		}	
	}
	
	public static void sortedStudents(Connection conn, String sorter)
	{
		try
		{
			String str = "";
			
			if (sorter.equals("average"))
			{
				str = "average";
			}
			else if (sorter.equals("first_name"))
			{
				str = "first_name";
			}
			else if (sorter.equals("last_name"))
			{
				str = "last_name";
			}
			else
			{
				System.out.println(RED + "Invalid sorter, try again" + RESET);
				System.out.println("\n");
				return;
			}
			
			String query = "SELECT students.student_id, first_name, last_name, ROUND(AVG(grade), 2) AS \"average\" " +
							"FROM students " +
							"LEFT JOIN grades " +
							"ON students.student_id = grades.student_id " +
							"GROUP BY students.student_id " +
							"ORDER BY " +
							str + " ASC;";
				
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
		catch (SQLException e)
		{
			System.out.println(RED + "\tSomething went wrong");
			System.out.println("\t" + e.getMessage() + RESET);
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
				try 
				{
					System.out.println("1.) Show all students");
					System.out.println("2.) Show a student");
					System.out.println("3.) Add a student");
					System.out.println("4.) Rename a student");
					System.out.println("5.) Add course to a student");
					System.out.println("6.) Update a course grade for a student");
					System.out.println("7.) Remove a course from a student");
					System.out.println("8.) Remove a student");
					System.out.println("9.) Sort all students");
					System.out.println("0.) Exit");
					
					System.out.print(CYAN + "\nOption: " + RESET);
					int choice = sc.nextInt();
					sc.nextLine();
					System.out.println("\n\n====================================================\n");
					
					if (choice == 1)
					{
						System.out.println(UNDERLINE + "All Students\n" + RESET);
						
						displayStudents(conn);
						
						System.out.println();
					}
					else if (choice == 2)
					{
						System.out.println(UNDERLINE + "Display Individual Student\n" + RESET);
						
						int id = getIntInput(sc, (CYAN + "Student ID number: " + RESET));
						
						showOneStudent(conn, id);
						
						System.out.println();
					}
					else if (choice == 3)
					{
						System.out.println(UNDERLINE + "Add a Student\n" + RESET);
						
						String fn = getStringInput(sc, (CYAN + "First name:\t" + RESET));
						String ln = getStringInput(sc, (CYAN + "Last name:\t" + RESET));
						
						addStudent(conn, fn, ln);
					}
					else if (choice == 4)
					{
						System.out.println(UNDERLINE + "Rename a Student\n" + RESET);
						
						int id = getIntInput(sc, (CYAN + "Student ID number:\t" + RESET));
						
						boolean confirm = confirmStudent(conn, sc, id);
						
						if (!confirm)
						{
							System.out.println("\n====================================================\n\n");
							continue;
						}
						
						System.out.println("\n====================================================\n");
						String fn = getStringInput(sc, (CYAN + "New first name:\t" + RESET));
						String ln = getStringInput(sc, (CYAN + "New last name:\t" + RESET));						
						renameStudent(conn, id, fn, ln);
					}
					else if (choice == 5)
					{
						System.out.println(UNDERLINE + "Add a Course to a Student\n" + RESET);
						
						int id = getIntInput(sc, (CYAN + "Student ID number:\t" + RESET));
						
						boolean confirm = confirmStudent(conn, sc, id);
						
						if (!confirm)
						{
							System.out.println("\n====================================================\n\n");
							continue;
						}
						
						System.out.println("\n====================================================\n");
						String course = getStringInput(sc, (CYAN + "Course:\t" + RESET));
						double grade = getDoubleInput(sc, (CYAN + "Grade:\t" + RESET));
						
						System.out.println("====================================================\n");
						
						addCourseToStudent(conn, id, course, grade);
					}
					else if (choice == 6)
					{
						System.out.println(UNDERLINE + "Update a Student's Grade\n" + RESET);
						
						int id = getIntInput(sc, (CYAN + "Student ID:\t" + RESET));
						
						boolean confirm = confirmStudent(conn, sc, id);
						
						if (!confirm)
						{
							System.out.println("\n====================================================\n\n");
							continue;
						}
						
						System.out.println("\n====================================================\n");
						String course = getStringInput(sc, (CYAN + "Course:\t" + RESET));
						double grade = getDoubleInput(sc, (CYAN + "Grade:\t" + RESET));
						
						updateCourseGradeForStudent(conn, id, course, grade);
					}
					else if (choice == 7)
					{
						System.out.println(UNDERLINE + "Remove a Course from a Student\n" + RESET);
						
						int id = getIntInput(sc, (CYAN + "Student ID:\t" + RESET));
						
						boolean confirm = confirmStudent(conn, sc, id);
						
						if (!confirm)
						{
							System.out.println("\n====================================================\n\n");
							continue;
						}
						
						System.out.println("====================================================\n");
						String course = getStringInput(sc, (CYAN + "Course to remove:\t" + RESET));
						System.out.println("\n====================================================\n");
						
						removeCourseFromStudent(conn, id, course);
					}
					else if (choice == 8)
					{
						System.out.println(UNDERLINE + "Remove a Student From Records\n" + RESET);
						
						int id = getIntInput(sc, "Student ID:\t");
						
						boolean confirm = confirmStudent(conn, sc, id);
						
						if (!confirm)
						{
							System.out.println("\n====================================================\n\n");
							continue;
						}
						
						System.out.println("\n====================================================\n\n");
						removeStudent(conn, id);
					}
					else if (choice == 9)
					{
						System.out.println(UNDERLINE + "Order Student Records\n" + RESET);
						
						String sorter = getStringInput(sc, (CYAN + "Enter an attribute to sort by:\t" + RESET));
						System.out.println();
						
						sortedStudents(conn, sorter);
					}
					else if (choice == 0)
					{
						System.out.println(RED + "Are you sure you want to exit? (yes/no):" + RESET);
						
						String quit = sc.nextLine().toLowerCase();
						System.out.println();
						
						if (quit.trim().equals("yes"))
						{
							System.out.println(GREEN + "Thank you, good bye\n" + RESET);
							System.out.println("====================================================\n");
							break;
						}
						else
						{
							System.out.println(GREEN + "Back to Main Menu\n" + RESET);
							System.out.println("====================================================\n\n");
						}
					}
					else
					{
						System.out.println(RED + "Invalid request\n" + RESET);
					}
				}
				catch (InputMismatchException e)
				{
					sc.nextLine();
					System.out.println("\n\n====================================================\n");
					System.out.println(RED + "Could not convert that input" + RESET);
					System.out.println("\n====================================================\n\n");
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