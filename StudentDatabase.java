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
	private String name;
	private int grade;
	
	Student(String name, int grade)
	{
		this.name = name;
		
		if (grade > 100)
		{
			grade = 100;
		}
		else if (grade < 0)
		{
			grade = 0;
		}
		this.grade = grade;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setGrade(int grade)
	{
		if (grade > 100)
		{
			grade = 100;
		}
		else if (grade < 0)
		{
			grade = 0;
		}
		
		this.grade = grade;
	}
	
	public int getGrade()
	{
		return grade;
	}
	
	public int compareTo(Student other)
	{
		return other.grade - this.grade;
	}
	
	public void printInfo()
	{
		System.out.println(name + " : " + grade);
	}
}

public class StudentDatabase
{
	private static boolean modified = false;
	
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
			return s1.getGrade() - s2.getGrade();
		}
	};
	
	public static Comparator<Student> gdc = new Comparator<Student>()
	{
		public int compare(Student s1, Student s2)
		{
			return s2.getGrade() - s1.getGrade();
		}
	};
	
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
	
	public static void addStudent(HashMap<String, Student> student, String name, int grade)
	{
		Student s = new Student(name, grade);
		
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
				p.println(stu.getName() + "," + stu.getGrade());
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
	
	public static void main(String[] args)
	{
		System.out.println();
		System.out.println("=== Student Database ===");
		System.out.println();
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
	}
}