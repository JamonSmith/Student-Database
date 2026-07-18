import java.util.ArrayList;

public class Student
{
	private int id;
	private String firstName;
	private String lastName;
	private Double average;
	private ArrayList<Course> courses = new ArrayList<>();
	
	public Student(int id, String firstName, String lastName, Double average)
	{
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.average = average;
	}
	
	public int getID()
	{
		return id;
	}
	
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}
	
	public String getFirstName()
	{
		return firstName;
	}
	
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}
	
	public String getLastName()
	{
		return lastName;
	}
	
	public void setAverage(Double average)
	{
		this.average = average;
	}
	
	public Double getAverage()
	{
		return average;
	}
	
	public ArrayList<Course> getCourses()
	{
		return courses;
	}
	
	public String toJSON()
	{
		StringBuilder json = new StringBuilder("{\n");
		
		StringBuilder jsonCourseArray = new StringBuilder("[\n");
			
			for (int i = 0; i < courses.size(); i++)
			{
				jsonCourseArray.append(courses.get(i).toJSON());
				
				if (i < courses.size() - 1)
				{
					jsonCourseArray.append(",\n");
				}
			}
			
			jsonCourseArray.append("\n]");
		
		json.append("\"id\": " + id + ", ");
		json.append("\"firstName\": \"" + firstName + "\", ");
		json.append("\"lastName\": \"" + lastName + "\", ");
		json.append("\"average\": " + average + ", ");
		json.append("\"courses\": " + jsonCourseArray.toString());
		json.append("\n}");
		
		return json.toString();
	}
}