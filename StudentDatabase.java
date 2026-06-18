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
	
	public void removeCourse(String course)
	{
		Double rm = transcript.remove(course);

		
		if (rm != null)
		{
			System.out.print("Course Removed");
			System.out.println();
		}
		else
		{
			System.out.println("\t" + course + " not taken by this student");
			System.out.println();
		}
	}
	
	public void updateTranscript(String course, double grade)
	{
		if (course.isEmpty() == true)
		{
			throw new IllegalArgumentException("Must provide course name");
		}
		
		if (grade > 100.0)
		{
			grade = 100.0;
		}
		else if (grade < 0.0)
		{
			grade = 0.0;
		}
		
		transcript.put(course, grade);
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
	
	public boolean hasCourse(String course)
	{
		Double curr = transcript.get(course);
		
		if (curr != null)
		{
			return true;
		}
		
		return false;
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
			System.out.print(s + "\t\t");
		}
		System.out.println();
		
		for (double s : transcript.values())
		{
			System.out.print(s + "\t\t");
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
	
	public static void addStudent(HashMap<String, Student> student, String name, int id, HashMap<String, Double> transcript)
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
	
	/*
	public static void updateGrade(HashMap<String, Student> student, String name, int grade)
	{
		Student curr = student.get(name);
		
		if (curr != null)
		{
			curr.setGrade(grade);
			modified = true;
			System.out.println("\tGrade Updated:");
			System.out.print("\t  ");
			curr.printInfo();
		}
		else
		{
			System.out.println("\t" + name + " not in dataset");
			System.out.println();
		}
	}
	*/
	
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
				p.println(stu.getName() + "," + stu.getAverage());
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
	
	/*
	public static HashMap<String, Student> readFromFile(String readFile)
	{
		HashMap<String, Student> student = new HashMap<>();
		
		try
		{
			Scanner sc = new Scanner(new File(readFile));
			
			
			while (sc.hasNextLine())
			{
				String str = sc.nextLine();
				
				String[] fields = str.split(",");
				
				String name = fields[0];
				int grade = Integer.parseInt(fields[1]);
				
				Student s = new Student(name, grade);
				student.put(name, s);
			}
			
			sc.close();
		}
		catch (IOException e)
		{
			System.out.println("Could not read file");
		}
		
		return student;
	}
	*/
	
	public static void main(String[] args)
	{	
		System.out.println();
		System.out.println(GREEN + "==== Student Database ====" + RESET);
		System.out.println();
		
		HashMap<String, Student> students = new HashMap<>();
		
		Student jamon = new Student("Jamon", 10001);
		
		students.put("Jamon", jamon);
		
		jamon.updateTranscript("Math", 100.0);
		jamon.updateTranscript("Science", 95.0);
		jamon.updateTranscript("History", 90.0);
		
		Student anthony = new Student("Anthony", 10002);
		
		students.put("Anthony", anthony);
		
		
		for (String s : students.keySet())
		{
			students.get(s).printInfo();
			System.out.println("Student Average:\t" + students.get(s).getAverage());
		}
		
		System.out.println();
		System.out.println("============================================================");
		System.out.println(YELLOW + "Debugging attempt\n" + RESET);
		
		students.get("Jamon").removeCourse("History");
		
		for (String s : students.keySet())
		{
			students.get(s).printInfo();
			System.out.println("Student Average:\t" + students.get(s).getAverage());
		}
		
		System.out.println();
		System.out.println("============================================================");
		System.out.println(YELLOW + "Debugging attempt\n" + RESET);
		
		System.out.println("Jamon\'s Math course grade: " + GREEN + students.get("Jamon").getCourseGrade("English") + RESET);
		System.out.println("Jamon\'s takes English: " + RED + students.get("Jamon").hasCourse("English") + RESET);
		
		/*
		System.out.println("1.) Display Students");
		System.out.println("2.) Add Student");
		System.out.println("3.) Rename Student");
		System.out.println("4.) Remove Student");
		System.out.println("5.) Update Grade");
		System.out.println("6.) Save Student Data");
		System.out.println("7.) Sort Students");
		System.out.println("0.) Exit");
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
				else if (x == 6)
				{
					System.out.println("\tStudent Data Updated");
					
					updateFile(students, "students.txt");
					System.out.println();
				}
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
				else if (x == 0)
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