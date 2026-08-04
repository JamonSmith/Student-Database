let params = new URLSearchParams(window.location.search);
let studentID = parseInt(params.get("id"));

// Element References

let idNumber = document.getElementById("studentIDNumber");
let lastName = document.getElementById("studentLastName");
let firstName = document.getElementById("studentFirstName");
let courseTable = document.getElementById("courseTable");
let courseCount = document.getElementById("courseCount");
let studentAverage = document.getElementById("studentAverage");


// Helper Functions

function clearAllCourses()
{
	idNumber.textContent = "";
	lastName.textContent = "";
	firstName.textContent = "";
	
	for (let i = courseTable.rows.length - 1; i > 0; i--)
	{
		courseTable.rows[i].remove();
	}
	
	courseCount.textContent = "";
	studentAverage.textContent = "";
}

function addStudentCourseRow(course, grade)
{
	let newRow = courseTable.insertRow();
	
	let courseCell = newRow.insertCell(0);
	let gradeCell = newRow.insertCell(1);
	
	courseCell.textContent = course;
	gradeCell.textContent = grade;
}

function renderStudentCourses(student)
{
	clearAllCourses();
	
	idNumber.textContent = "Student ID: " + student.id;
	lastName.textContent = "Last Name: " + student.lastName;
	firstName.textContent = "First Name: " + student.firstName;
	
	for (const course of student.courses)
	{
		addStudentCourseRow(course.name, course.grade);
	}
	
	courseCount.textContent = "Courses Taken: " + student.courses.length;
	studentAverage.textContent = "Average: " + student.average;
}


// Feature Functions

async function loadStudent()
{
	let response = await fetch("http://localhost:8000/students");
	
	let data = await response.json();
	
	let student = data.find(function(studentData) { return studentData.id === studentID; });
	
	if (student === undefined)
	{
		console.log("student not found");
		return;
	}
	
	renderStudentCourses(student);
}


// Initial Page Setup

loadStudent();

