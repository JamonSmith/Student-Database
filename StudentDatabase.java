import java.util.HashMap;
import java.util.Scanner;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;

class Student implements Comparable<Student>
{
	public static final String RESET = "\u001B[0m";
	public static final String RED = "\u001B31m";
	public static final String GREEN = "\u001B[32m";
	public static final String YELLOW = "\u001B[33m";
	public static final String BLUE = "\u001B[34m";
	public static final String PURPLE = "\u001B[35m";
	public static final String CYAN = "\u001B[36m";
	
	private String name;
	private int id;
	private HashMap<String, Double> transcript = new HashMap<>();
	
	Student(String name, int id)
	{
		if (name.isEmpty() == true)
		{
			throw new IllegalArgumentException("Student not found in records");
		}
		
		if (id > 99999 || id < 10000)
		{
			throw new IllegalArgumentException("Invalid id number");
		}
		
		this.name = name;
		this.id = id;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setID(int id)
	{
		this.id = id;
	}
	
	public int getID()
	{
		return id;
	}
	
	public boolean hasCourse(String course)
	{
		Double curr = transcript.get(course);
		
		if (curr != null)
		{
			return true;
		}
		
		return false;
	}
	
	public void removeCourse(String course)
	{
		Double rm = transcript.remove(course);

		
		if (rm != null)
		{
			System.out.print("Course: " + course + " removed");
			System.out.println();
		}
		else
		{
			System.out.println("\t" + course + " not taken by this student");
			System.out.println();
		}
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
			throw new IllegalArgumentException("Must provide course name");
		}
		else if (hasCourse(course))
		{
			System.out.println(name + " already has course: " + course);
		}
		else
		{
			transcript.put(course, clampGrade(grade));
		}
	}
	
	public double getCourseGrade(String course)
	{
		Double grade = transcript.get(course);
		
		if (grade != null)
		{
			return grade;
		}
		
		return 0.0;
	}
	
	public void setCourseGrade(String course, double newGrade)
	{
		if (course.isEmpty())
		{
			throw new IllegalArgumentException("Must provide course name");
		}
		
		transcript.put(course, clampGrade(newGrade));
	}
	
	public double getAverage()
	{
		double sum = 0.0;
		
		if (transcript.size() == 0)
		{
			return 0.0;
		}
		else
		{	
			for (Double s : transcript.values())
			{
				sum += s;
			}
			
			return sum / transcript.size();
		}
	}
	
	public HashMap<String, Double> getTranscript()
	{
		return new HashMap<>(transcript);
	}
	
	public int compareTo(Student other)
	{
		return Double.compare(other.getAverage(), this.getAverage());
	}
	
	public void printInfo()
	{
		System.out.println();
		System.out.println(id + "\t\t" + CYAN + name + RESET + "\n");
		
		for (String s : transcript.keySet())
		{
			System.out.println(YELLOW + transcript.get(s) + RESET + ":\t" + s + "\n");

		}
		System.out.println("\n");
	}
}

public class StudentDatabase
{
	public static final String RESET = "\u001B[0m";
	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String YELLOW = "\u001B[33m";
	public static final String BLUE = "\u001B[34m";
	public static final String PURPLE = "\u001B[35m";
	public static final String CYAN = "\u001B[36m";
	
	private static boolean modified = false;
	
	/*
	public static Comparator<Student> nac = new Comparator<Student>()
	{
		public int compare(Student s1, Student s2)
		{
			return s1.getName().compareTo(s2.getName());
		}
	};
	
	public static Comparator<Student> gac = new Comparator<Student>()
	{
		public int compare(Student s1, Student s2)
		{
			return s1.getAverage() - s2.getAverage();
		}
	};
	
	public static Comparator<Student> gdc = new Comparator<Student>()
	{
		public int compare(Student s1, Student s2)
		{
			return s2.getAverage() - s1.getAverage();
		}
	};
	*/
	
	public static void displayStudents(HashMap<String, Student> student)
	{
		if (student.size() == 0)
		{
			System.out.println("No items to print");
		}
		else
		{
			for (String s : student.keySet())
			{
				System.out.print("\t");
				student.get(s).printInfo();
			}
		}
		System.out.println();
	}
	
	public static boolean studentExists(HashMap<String, Student> student, String name)
	{
		Student curr = student.get(name);
		
		if (curr != null)
		{
			return true;
		}
		
		return false;
	}
	
	public static boolean courseExistsForStudent(HashMap<String, Student> student, String name, String course)
	{
		return student.get(name).hasCourse(course);
	}
	
	public static void addStudent(HashMap<String, Student> student, String name, int id)
	{
		Student s = new Student(name, id);
		
		student.put(name, s);
		modified = true;
		
		System.out.println("\tAdded:");
		System.out.print("\t  ");
		student.get(name).printInfo();
	}
	
	public static void renameStudent(HashMap<String, Student> student, String name, String newName)
	{
		Student curr = student.remove(name);
		
		if (curr != null)
		{
			curr.setName(newName);
			student.put(newName, curr);
			modified = true;
			System.out.println("\tStudent Name Updated:");
			System.out.print("\t  ");
			curr.printInfo();
		}
		else
		{
			System.out.println("\t" + name + " not in dataset");
		}
	}
	
	public static void removeStudent(HashMap<String, Student> student, String removeName)
	{
		Student rm = student.remove(removeName);
		
		if (rm != null)
		{
			modified = true;
			System.out.println("\tRemoved: ");
			System.out.print("\t  ");
			rm.printInfo();
			System.out.println();
		}
		else
		{
			System.out.println("\t" + removeName + " not in dataset");
			System.out.println();
		}
	}
	
	public static void addCourseToStudent(HashMap<String, Student> student, String name, String course, double grade)
	{
		if (!studentExists(student, name))
		{
			System.out.println(CYAN + name + RESET + " does not exist in our records.");
		}
		else
		{
			student.get(name).addCourse(course, grade);
		}
	}
	
	public static void showCourseGradeForStudent(HashMap<String, Student> student, String name, String course)
	{
		if (!studentExists(student, name))
		{
			System.out.println(CYAN + name + RESET + " does not exist in our records.");
			return;
		}
		
		boolean courseWasTaken = courseExistsForStudent(student, name, course);
		
		if (courseWasTaken)
		{
			Double curr = student.get(name).getCourseGrade(course);
			System.out.println(name + "\'s " + course + " grade: " + GREEN + curr + RESET);
		}
		else
		{
			System.out.println(name + " has not taken " + PURPLE + course + RESET);
		}
	}
	
	public static void updateCourseGradeForStudent(HashMap<String, Student> student, String name, String course, double newGrade)
	{
		if (!studentExists(student, name))
		{
			System.out.println(CYAN + name + RESET + " does not exist in our records.");
			return;
		}
		
		boolean courseWasTaken = courseExistsForStudent(student, name, course);
		
		if (courseWasTaken)
		{		
			double currGrade = student.get(name).getCourseGrade(course);
			double ng = student.get(name).clampGrade(newGrade);
			
			System.out.println(PURPLE + course + RESET + " grade changed from " + GREEN + currGrade + RESET + " to " + GREEN + ng + RESET);
			student.get(name).setCourseGrade(course, newGrade);
		}
		else
		{
			System.out.println(name + " has not taken " + PURPLE + course + RESET);
		}
	}
	
	public static void removeCourseFromStudent(HashMap<String, Student> student, String name, String course)
	{
		if (!studentExists(student, name))
		{
			System.out.println(CYAN + name + RESET + " does not exist in our records.");
		}
		else 
		{
			student.get(name).removeCourse(course);
		}
	}
	
	public static void updateFile(HashMap<String, Student> student, String str)
	{
		if (!str.endsWith(".txt"))
		{
			System.out.println("Incorrect file format");
			return;
		}
		
		try
		{
			PrintWriter p = new PrintWriter(new File(str));
			
			for (Student stu : student.values())
			{
				for (String course : stu.getTranscript().keySet())
				{
					double grade = stu.getTranscript().get(course);
					
					p.println(stu.getName() + "," + stu.getID() + "," + course + "," + grade);
				}
			}
			
			modified = false;
			
			p.close();
		}
		catch (IOException e)
		{
			System.out.println("Could not write to file " + str);
			return;
		}
	}
	
	public static HashMap<String, Student> readFromFile(String readFile)
	{
		if (!readFile.endsWith(".txt"))
		{
			System.out.println("Incorrect file format");
			return new HashMap<>();
		}
		
		HashMap<String, Student> student = new HashMap<>();
		
		try
		{
			Scanner sc = new Scanner(new File(readFile));
			
			
			while (sc.hasNextLine())
			{
				String str = sc.nextLine();
				
				String[] fields = str.split(",");
				
				String name = fields[0];
				int id = Integer.parseInt(fields[1]);
				String course = fields[2];
				double grade = Double.parseDouble(fields[3]);
				
				if (student.containsKey(name))
				{
					student.get(name).addCourse(course, grade);
				}
				else
				{
					Student s = new Student(name, id);
					student.put(name, s);
					
					s.addCourse(course, grade);
				}
			}
			
			sc.close();
		}
		catch (IOException e)
		{
			System.out.println("Could not read file");
		}
		
		return student;
	}
	
	public static void testAllFunctions(HashMap<String, Student> students)
	{
		addStudent(students, "Jamon", 10001);
		
		addCourseToStudent(students, "Jamon", "Math", 100.0);
		addCourseToStudent(students, "Jamon", "Science", 95.0);
		addCourseToStudent(students, "Jamon", "History", 90.0);
		addCourseToStudent(students, "Jamona", "History", 90.0);
		
		Student anthony = new Student("Anthony", 10002);
		
		students.put("Anthony", anthony);
		
		for (String s : students.keySet())
		{
			students.get(s).printInfo();
			System.out.println("Student Average:\t" + students.get(s).getAverage());
		}
		
		System.out.println(GREEN + "== Successful ==\n" + RESET);
		
		System.out.println();
		System.out.println("============================================================");
		System.out.println(YELLOW + "Remove Course attempt\n" + RESET);
		
		removeCourseFromStudent(students, "Jamon", "History");
		
		for (String s : students.keySet())
		{
			students.get(s).printInfo();
			System.out.println("Student Average:\t" + students.get(s).getAverage());
		}
		
		System.out.println(GREEN + "== Successful ==\n" + RESET);
	
		System.out.println();
		System.out.println("============================================================");
		System.out.println(YELLOW + "Rename Student attempt\n" + RESET);
		
		System.out.println(PURPLE + "Before\n" + RESET);
		students.get("Jamon").printInfo();
		
		System.out.println(PURPLE + "After\n" + RESET);
		renameStudent(students, "Jamon", "Amon");
		
		System.out.println(GREEN + "== Successful ==\n" + RESET);
		
		System.out.println();
		System.out.println("============================================================");
		System.out.println(YELLOW + "Add Course to Student attempt\n" + RESET);
		
		addCourseToStudent(students, "Amon", "Math", 99);
		addCourseToStudent(students, "Amon", "Civics", 99);
		students.get("Amon").printInfo();
		
		System.out.println(GREEN + "== Successful ==\n" + RESET);
		
		System.out.println();
		System.out.println("============================================================");
		System.out.println(YELLOW + "Show Student course Grade attempt\n" + RESET);
		
		showCourseGradeForStudent(students, "Amon", "Science");
		showCourseGradeForStudent(students, "Amon", "Math");
		showCourseGradeForStudent(students, "Amon", "History");
		
		System.out.println(GREEN + "== Successful ==\n" + RESET);
		
		System.out.println();
		System.out.println("============================================================");
		System.out.println(YELLOW + "Update Student Course Grade attempt\n" + RESET);
		
		students.get("Amon").printInfo();
		
		updateCourseGradeForStudent(students, "Amon", "Science", 800.0);
		updateCourseGradeForStudent(students, "Jamon", "Science", 80.0);
		updateCourseGradeForStudent(students, "Amon", "Sciece", 80.0);
		
		students.get("Amon").printInfo();
	}
	
	public static void main(String[] args)
	{	
		System.out.println();
		System.out.println(GREEN + "==== Student Database ====" + RESET);
		System.out.println();
		
		HashMap<String, Student> students = new HashMap<>();
		
		addStudent(students, "Jamon", 10001);
		
		addCourseToStudent(students, "Jamon", "Intro to Comp Sci", 100.0);
		addCourseToStudent(students, "Jamon", "Data Structures", 89.0);
		addCourseToStudent(students, "Jamon", "Computer Graphics", 92.0);
		addCourseToStudent(students, "Jamon", "Object Oriented Programming", 98.0);
		addCourseToStudent(students, "Jamon", "Artifical Intelligence I", 89.0);
		addCourseToStudent(students, "Jamon", "Data Mining", 95.0);
		
		updateFile(students, "students.txt");
		
		HashMap<String, Student> studentsCopy = readFromFile("students.txt");
		
		for (Student stu : studentsCopy.values())
		{
			stu.printInfo();
			System.out.println();
			System.out.println(GREEN + "Student " + stu.getID() + CYAN + " " + stu.getName() + RESET + " grade Average: " + stu.getAverage());
			System.out.println("\n");
		}
		
		System.out.println("1.) Display All Students");
		System.out.println("2.) Add Student");
		System.out.println("3.) Rename Student");
		System.out.println("4.) Remove Student");
		System.out.println("5.) Add Course to Student");
		System.out.println("6.) Update Student Course Grade");
		System.out.println("7.) Remove Student Course Grade");
		System.out.println("8.) Show Student Course Grade");
		System.out.println("9.) Sort Students");
		System.out.println("0.) Save Student Data");
		System.out.println("-1.) Exit");
		System.out.println();
		
		/*
		HashMap<String, Student> students = readFromFile("students.txt");
		Scanner sc = new Scanner(System.in);
			
		while (true)
		{
			try
			{
				System.out.println("Please select one of the numbers above then press enter:");
				
				int x = Integer.parseInt(sc.nextLine());
				System.out.println();
				
				if (x == 1)
				{
					System.out.println("Showing Student Data:");
					System.out.println();
					
					if (students.size() != 0)
					{
						displayStudents(students);
					}
					else
					{
						System.out.println("No entries exist for this file");
					}
				}
				else if (x == 2)
				{
					System.out.println("\tEnter student name:");
					System.out.print("\t  ");
					
					String stuName = sc.nextLine();
					
					Student curr = students.get(stuName);
					
					if (curr == null)
					{
						System.out.println("\tEnter student grade:");
						System.out.print("\t  ");
						
						int stuGrade = sc.nextInt();
						sc.nextLine();
						
						addStudent(students, stuName, stuGrade);
						System.out.println();
					}
					else 
					{
						System.out.println("\t" + stuName + " is already in the system");
						System.out.println();
					}
				}	
				else if (x == 3)
				{
					System.out.println("\tEnter student name:");
					System.out.print("\t  ");
					
					String stuName = sc.nextLine();
					
					Student curr = students.get(stuName);
					
					if (curr != null)
					{
						System.out.println("\tEnter new student name:");
						System.out.print("\t  ");
						
						String newStuName = sc.nextLine();
						
						Student curr2 = students.get(newStuName);
						
						if (curr2 == null)
						{
							renameStudent(students, stuName, newStuName);
							System.out.println();
						}
						else
						{
							System.out.println("\t" + newStuName + " is already in the system");
							System.out.println();
						}
					}
					else
					{
						System.out.println("\t" + stuName + " not in dataset");
						System.out.println();
					}
				}
				else if (x == 4)
				{
					System.out.println("\tEnter student name:");
					System.out.print("\t  ");
					
					String stuName = sc.nextLine();
					
					removeStudent(students, stuName);
				}
				else if (x == 5)
				{
					System.out.println("\tEnter student name:");
					System.out.print("\t  ");
					
					String stuName = sc.nextLine();
					
					Student curr = students.get(stuName);
					
					if (curr != null)
					{
						System.out.println("\tEnter student grade:");
						System.out.print("\t  ");
						
						int stuGrade = sc.nextInt();
						sc.nextLine();
						
						updateGrade(students, stuName, stuGrade);
						System.out.println();
					}
					else
					{
						System.out.println("\t" + stuName + " not in dataset");
						System.out.println();
					}
				}
				//
				else if (x == 7)
				{
					System.out.println("\tSort by grades ('asc' or 'desc') or by name ('name')?");
					System.out.print("\t  ");
					
					String sorting = sc.nextLine().toLowerCase();
					System.out.println();
					
					ArrayList<Student> studentsCopy = new ArrayList<Student>(students.values());
					
					if (sorting.equals("asc"))
					{
						Collections.sort(studentsCopy, gac);
						
						for (Student s : studentsCopy)
						{
							System.out.print("\t");
							s.printInfo();
						}
						System.out.println();
					}
					else if (sorting.equals("desc"))
					{
						Collections.sort(studentsCopy, gdc);
						
						for (Student s : studentsCopy)
						{
							System.out.print("\t");
							s.printInfo();
						}
						System.out.println();
					}
					else if (sorting.equals("name"))
					{
						Collections.sort(studentsCopy, nac);
						
						for (Student s : studentsCopy)
						{
							System.out.print("\t");
							s.printInfo();
						}
						System.out.println();
					}
					else
					{
						System.out.println("\tInvalid sorting order, please choose 'asc', 'desc', or 'name' then press enter");
						System.out.println();
					}
				}
				//
				else if (x == 0)
				{
					System.out.println("\tStudent Data Updated");
					
					updateFile(students, "students.txt");
					System.out.println();
				}
				else if (x == -1)
				{
					if (modified == true)
					{
						System.out.println("\tDatabase changes have not been saved");
						System.out.println("\tAre you sure you want to exit?");
						System.out.print("\t  ");
						
						String quit = sc.nextLine().toLowerCase();
						System.out.println();
						
						if (quit.equals("yes"))
						{
							System.out.println("\tThank you, good bye");
							break;
						}
						else
						{
							System.out.println("\tBack to Main Menu");
							System.out.println();
						}
					}
					else
					{
						System.out.println("\tThank you, good bye");
						break;
					}
				}
				else
				{
					System.out.println("\tInvalid command, please select one of the numbers above");
					System.out.println();
				}
			}
			catch (NumberFormatException e)
			{
				System.out.println();
				System.out.println("\tCannot convert that input to a numeric value");
				System.out.println();
			}
		}
		*/
	}
}