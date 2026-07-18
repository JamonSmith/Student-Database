public class Course
{
	private String name;
	private Double grade;
	
	public Course(String name, Double grade)
	{
		this.name = name;
		this.grade = grade;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setGrade(Double grade)
	{
		this.grade = grade;
	}
	
	public Double getGrade()
	{
		return grade;
	}
	
	public String toJSON()
	{
		return "{ " +
					"\"name\": \"" + name + "\", " +
					"\"grade\": " + grade +
			   " }";	
	}
}