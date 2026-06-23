import java.util.HashMap;
import java.util.Scanner;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.InputMismatchException;
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
		return transcript.containsKey(course);
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
			
			double avg = sum / transcript.size();
			
			return Math.round(avg * 100.0) / 100.0;
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
		System.out.print("\t\t" + GREEN + id + "\t" + RESET);
		System.out.print(CYAN + name + "\t\t" + RESET);
		System.out.println(getAverage() + "\n");
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
	
	public static Comparator<Student> idc = new Comparator<Student>()
	{
		public int compare(Student s1, Student s2)
		{
			return s1.getID() - s2.getID();
		}
	};
	
	public static Comparator<Student> nac = new Comparator<Student>()
	{
		public int compare(Student s1, Student s2)
		{
			return s1.getName().compareTo(s2.getName());
		}
	};
	
	public static Comparator<Student> aac = new Comparator<Student>()
	{
		public int compare(Student s1, Student s2)
		{
			return Double.compare(s1.getAverage(), s2.getAverage());
		}
	};
	
	public static Comparator<Student> adc = new Comparator<Student>()
	{
		public int compare(Student s1, Student s2)
		{
			return Double.compare(s2.getAverage(), s1.getAverage());
		}
	};
	
	public static void displayStudents(HashMap<String, Student> student)
	{
		System.out.println("\tAll Students\n");
		
		if (student.size() == 0)
		{
			System.out.println(RED + "\t\tNo students found" + RESET);
		}
		else
		{
			ArrayList<Student> copy = new ArrayList<Student>(student.values());
		
			Collections.sort(copy, idc);
							
			for (Student s : copy)
			{
				s.printInfo();
			}
		}
	}
	
	public static boolean studentExists(HashMap<String, Student> student, String name)
	{
		return student.containsKey(name);
	}
	
	public static void showOneStudent(HashMap<String, Student> student, String name)
	{
		if (!studentExists(student, name))
		{
			System.out.println(CYAN + name + RESET + " does not exist in our records.\n");
		}
		else
		{
			System.out.println("ID\t: " + GREEN + student.get(name).getID() + RESET);
			System.out.println("Name\t: " + CYAN + student.get(name).getName() + RESET + "\n\n");
			System.out.println("==== Courses ====\n");
			
			HashMap<String, Double> transcript = student.get(name).getTranscript();
			
			if (transcript.size() > 0)
			{
				for (String course : transcript.keySet())
				{
					System.out.println(transcript.get(course) + "\t: " + CYAN + course + RESET + "\n");
				}
			}
			else
			{
				System.out.println("Student has not taken any courses yet\n");
			}
			
			System.out.println();
			System.out.println(student.get(name).getAverage() + "\t: " + PURPLE + "Average Grade\n" + RESET);
		}
	}
	
	public static boolean courseExistsForStudent(HashMap<String, Student> student, String name, String course)
	{
		return student.get(name).hasCourse(course);
	}
	
	public static void addStudent(HashMap<String, Student> student, String name, int id)
	{
		for (Student stu : student.values())
		{
			if (stu.getID() == id)
			{
				System.out.println(RED + "\tID number: " + RESET + id + RED + " already taken\n" + RESET);
				return;
			}
		}
		
		Student s = new Student(name, id);
		
		student.put(name, s);
		modified = true;
		
		System.out.println("\tAdded:");
		System.out.print("\t\t" + student.get(name).getID());
		System.out.print("\t" + student.get(name).getName());
		System.out.println("\n");
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
			System.out.print("\t\t" + GREEN + rm.getID() + "\t" + RESET);
			System.out.print(CYAN + rm.getName() + "\n" + RESET);
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
			modified = true;
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
			modified = true;
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
			modified = true;
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
				HashMap<String, Double> transcript = stu.getTranscript();
				
				if (transcript.size() > 0)
				{
					for (String course : transcript.keySet())
					{
						double grade = transcript.get(course);
						
						p.println(stu.getName() + "," + stu.getID() + "," + course + "," + grade);
					}
				}
				else
				{
					p.println(stu.getName() + "," + stu.getID());
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
			File f = new File(readFile);
			
			if (!f.exists())
			{
				System.out.println(YELLOW + "No database found. Creating new database for students" + RESET);
				
				return new HashMap<>();
			}
			
			Scanner sc = new Scanner(f);
			
			while (sc.hasNextLine())
			{
				String str = sc.nextLine();
				
				String[] fields = str.split(",");
				
				if (fields.length == 4)
				{
					String name = fields[0];
					int id = Integer.parseInt(fields[1]);
					String course = fields[2];
					double grade = Double.parseDouble(fields[3]);
					
					if (!studentExists(student, name))
					{
						Student s = new Student(name, id);
						student.put(name, s);
					}
					
					student.get(name).addCourse(course, grade);
				}
				else if (fields.length == 2)
				{
					String name = fields[0];
					int id = Integer.parseInt(fields[1]);
					
					if (!studentExists(student, name))
					{
						Student s = new Student(name, id);
						student.put(name, s);
					}
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
	
	public static void main(String[] args)
	{	
		System.out.println();
		System.out.println(GREEN + "==========================");
		System.out.println("==== Student Database ====");
		System.out.println("==========================" + RESET);
		System.out.println();
		
		System.out.println("1.) Display All Students");
		System.out.println("2.) Display One Student");
		System.out.println("3.) Add Student");
		System.out.println("4.) Rename Student");
		System.out.println("5.) Remove Student");
		System.out.println("6.) Add Course to Student");
		System.out.println("7.) Update Student Course Grade");
		System.out.println("8.) Remove Course");
		System.out.println("9.) Sort Students");
		System.out.println("0.) Save Student Data");
		System.out.println("-1.) Exit");
		System.out.println();
		
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
					System.out.println("Showing Students:");
					System.out.println();
					
					displayStudents(students);
				}
				else if (x == 2)
				{
					System.out.println("\tEnter student name:");
					System.out.print("\t  ");
					
					String stuName = sc.nextLine();
					System.out.println();
					
					showOneStudent(students, stuName);
				}
				else if (x == 3)
				{
					System.out.println("\tEnter student name:");
					System.out.print("\t  ");
					
					String stuName = sc.nextLine();
					
					if (studentExists(students, stuName))
					{
						System.out.println("\t" + stuName + " is already in the system");
						System.out.println();
					}
					else if (stuName.isEmpty())
					{
						System.out.println(RED + "Student name must be provided\n" + RESET);
					}
					else 
					{
						System.out.println("\tEnter student ID number:");
						System.out.print("\t  ");
						
						try
						{		
							int stuID = sc.nextInt();
							sc.nextLine();
							
							if (stuID < 10001 || stuID > 99999)
							{
								System.out.println(RED + "Student ID number must be within range [10001, 99999]\n" + RESET);
							}
							else
							{
								addStudent(students, stuName, stuID);
								System.out.println();
							}
						}
						catch (InputMismatchException e)
						{
							System.out.println(RED + "Student ID number must be a positive integer within range [10001, 99999]\n" + RESET);
							sc.nextLine();
						}
					}
				}
				else if (x == 4)
				{
					System.out.println("\tEnter student name:");
					System.out.print("\t  ");
					
					String stuName = sc.nextLine();
					
					if (studentExists(students, stuName))
					{
						System.out.println("\tEnter new student name:");
						System.out.print("\t  ");
						
						String newStuName = sc.nextLine();
						
						if (!studentExists(students, newStuName))
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
				else if (x == 5)
				{
					System.out.println("\tEnter student name:");
					System.out.print("\t  ");
					
					String stuName = sc.nextLine();
					
					removeStudent(students, stuName);
				}
				else if (x == 6)
				{
					System.out.println("\tEnter student name:");
					System.out.print("\t  ");
						
					String stuName = sc.nextLine();
					
					if (studentExists(students, stuName))
					{
						System.out.println("\tEnter course:");
						System.out.print("\t  ");
						
						String stuCourse = sc.nextLine();
						
						System.out.println("\tEnter student grade:");
						System.out.print("\t  ");
						
						try 
						{
							double stuGrade = sc.nextDouble();
							sc.nextLine();
								
							addCourseToStudent(students, stuName, stuCourse, stuGrade);
							System.out.println();
						}
						catch (InputMismatchException e)
						{
							System.out.println(RED + "Student grade must be a number\n" + RESET);
							sc.nextLine();
						}
					}
					else
					{
						System.out.println("\t" + stuName + " not in dataset");
						System.out.println();
					}
				}
				else if (x == 7)
				{
					System.out.println("\tEnter student name:");
					System.out.print("\t  ");
					
					String stuName = sc.nextLine();
					
					if (studentExists(students, stuName))
					{
						System.out.println("\tEnter student course:");
						System.out.print("\t  ");
						
						String stuCourse = sc.nextLine();
						
						if (courseExistsForStudent(students, stuName, stuCourse))
						{
							System.out.println("\tEnter new student grade:");
							System.out.print("\t  ");
							
							try
							{
								double stuGrade = sc.nextDouble();
								sc.nextLine();
								
								updateCourseGradeForStudent(students, stuName, stuCourse, stuGrade);
								System.out.println();
							}
							catch (InputMismatchException e)
							{
								System.out.println(RED + "Student grade must be a number\n" + RESET);
								sc.nextLine();
							}
						}
						else
						{
							System.out.println("\t" + stuName + " has not taken " + stuCourse);
							System.out.println();
						}
					}
					else
					{
						System.out.println("\t" + stuName + " not in dataset");
						System.out.println();
					}
				}
				else if (x == 8)
				{
					System.out.println("\tEnter student name:");
					System.out.print("\t  ");
					
					String stuName = sc.nextLine();
					
					if (studentExists(students, stuName))
					{
						System.out.println("\tEnter student course:");
						System.out.print("\t  ");
						
						String stuCourse = sc.nextLine();
						
						if (courseExistsForStudent(students, stuName, stuCourse))
						{
							removeCourseFromStudent(students, stuName, stuCourse);
							System.out.println();
						}
						else
						{
							System.out.println("\t" + stuName + " has not taken " + stuCourse);
							System.out.println();
						}
					}
					else
					{
					System.out.println("\t" + stuName + " not in dataset");
					System.out.println();
					}
				}
				else if (x == 9)
				{
					System.out.println("\tSort by grades ('asc' or 'desc') or by name ('name')?");
					System.out.print("\t  ");
					
					String sorting = sc.nextLine().toLowerCase();
					System.out.println();
					
					ArrayList<Student> studentsCopy = new ArrayList<Student>(students.values());
					
					if (sorting.equals("asc"))
					{
						Collections.sort(studentsCopy, aac);
						
						for (Student s : studentsCopy)
						{
							System.out.print("\t");
							s.printInfo();
						}
						System.out.println();
					}
					else if (sorting.equals("desc"))
					{
						Collections.sort(studentsCopy, adc);
						
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
					System.out.println(RED + "\tInvalid command, please select one of the numbers above" + RESET);
					System.out.println();
				}
			}
			catch (NumberFormatException e)
			{
				System.out.println();
				System.out.println(RED + "\tCannot convert that input to a numeric value" + RESET);
				System.out.println();
			}
		}
	}
}