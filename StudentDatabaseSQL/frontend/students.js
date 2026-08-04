// Element References

let allStudentsButton = document.getElementById("viewStudentsButton");
let clearStudentsButton = document.getElementById("clearStudentsButton");
let studentTable = document.getElementById("studentTable");
let studentCount = document.getElementById("studentCount");

let idHeader = document.getElementById("idHeader");
let firstHeader = document.getElementById("firstHeader");
let lastHeader = document.getElementById("lastHeader");
let avgHeader = document.getElementById("avgHeader");


// Classes

class Student
{
	constructor (id, firstName, lastName, average = "N/A")
	{
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.average = average;
		this.courses = [];
	}
	
	rename(firstName, lastName)
	{
		if (firstName !== "")
		{
			this.firstName = firstName;
		}
		
		if (lastName !== "")
		{
			this.lastName = lastName;
		}
	}
	
	getFullName()
	{
		return this.firstName + " " + this.lastName;
	}
	
	findCourseIndex(name)
	{
		for (let i = 0; i < this.courses.length; i++)
		{
			if(this.courses[i].name.toLowerCase() === name.toLowerCase())
			{
				return i;
			}
		}
		
		return -1;
	}
	
	addCourse(name, grade = "N/A")
	{
		let ind = this.findCourseIndex(name);
		
		if (ind >= 0)
		{
			return false;
		}
		
		let course = new Course(name, grade);
		this.courses.push(course);
		
		this.calculateAverage();
		
		return true;
	}
	
	removeCourse(name)
	{
		let ind = this.findCourseIndex(name);
		
		if (ind < 0)
		{
			return false;
		}
		
		this.courses.splice(ind, 1);
		this.calculateAverage();
		
		return true;
	}
	
	updateCourseGrade(name, grade)
	{
		let ind = this.findCourseIndex(name)
		
		if (ind < 0)
		{
			return false;
		}
		
		let course = this.courses[ind];
		course.updateGrade(grade);
		this.calculateAverage();
		
		return true;
	}
	
	getCourseCount()
	{
		return this.courses.length;
	}
	
	calculateAverage()
	{
		let sum = 0.0;
		let numGrades = 0;
		
		for (let i = 0; i < this.courses.length; i++)
		{
			if (this.courses[i].grade === "N/A")
			{
				continue;
			}
			
			sum += this.courses[i].grade;
			numGrades++;
		}
		
		if (numGrades === 0)
		{
			this.average = "N/A";
			return;
		}
		
		this.average = sum / numGrades;
	}
}

class Course
{
	constructor(name, grade = "N/A")
	{
		this.name = name;
		this.grade = grade;
	}
	
	updateGrade(grade)
	{
		this.grade = grade;
	}
}


// Application State

let recordsMessageTimeout;
const IDCOL = 0;
const FIRSTNAMECOL = 1;
const LASTNAMECOL = 2;
const AVGCOL = 3;
let asc = true;
let currSortCol = IDCOL;
let students = [];
let selectedStudentID = null;


// Helper Functions

function updateStudentCount()
{
	if (!allStudentsButton.disabled)
	{
		studentCount.textContent = "";	
	}
	else
	{	
		studentCount.textContent = "Total Students: " + students.length;
	}
}

function clearAllStudents()
{
	for (let i = studentTable.rows.length - 1; i > 0; i--)
	{
		studentTable.rows[i].remove();
	}
	
	studentCount.textContent = "";
}

function refreshRecordsView()
{
	if (selectedStudentID === null)
	{
		renderAllStudents();
		return;
	}
}

async function loadStudentsFromBackend()
{
	try
	{
		let response = await fetch("http://localhost:8000/students");
		
		if(!response.ok)
		{
			throw new Error("HTTP Error: " + response.status);
		}
		
		let data = await response.json();
		
		students = data.map(function(studentData)
		{
			let student = new Student(
				studentData.id, 
				studentData.firstName, 
				studentData.lastName,
				studentData.average ?? "N/A"
			);
			
			student.courses = studentData.courses.map(function(courseData)
			{
				return new Course(
					courseData.name,
					courseData.grade ?? "N/A"
				);
			});
			
			return student;
		});
		
		renderAllStudents();
	}
	catch (error)
	{
		console.error("Error occurred: ", error);
	}
}

function buttonStates()
{
	allStudentsButton.disabled = (studentTable.rows.length > 1);
	clearStudentsButton.disabled = (studentTable.rows.length <= 1);
}


// Table Functions

function addStudentRow(id, first, last, avg)
{
	let newRow = studentTable.insertRow();
	
	let idCell = newRow.insertCell(0);
	let firstCell = newRow.insertCell(1);
	let lastCell = newRow.insertCell(2);
	let avgCell = newRow.insertCell(3);
	
	idCell.textContent = id;
	firstCell.textContent = first;
	lastCell.textContent = last;
	avgCell.textContent = avg;
}

function renderAllStudents()
{
	selectedStudentID = null;
	
	clearAllStudents();
	//clearAllCourses();
	
	for (const s of students)
	{
		addStudentRow(s.id, s.firstName, s.lastName, s.average);
	}
	
	buttonStates();
	updateStudentCount();
}

function sortStudents(col)
{
	if (currSortCol === col)
	{
		asc = !asc;
	}
	else 
	{
		currSortCol = col;
		asc = true;
	}
	
	students.sort(function(a, b) 
	{ 
		if(col === IDCOL)
		{
			let numA = a.id;
			let numB = b.id;
			
			if (Number.isNaN(numA) && Number.isNaN(numB))
			{
				return 0;
			}
			
			if (Number.isNaN(numA))
			{
				return 1;
			}
			
			if (Number.isNaN(numB))
			{
				return -1;
			}
			
			if (asc)
			{
				return numA - numB;
			}
			else
			{
				return numB - numA;
			}
		}
		else if (col === FIRSTNAMECOL)
		{
			let valA = a.firstName;
			let valB = b.firstName;
			
			if (asc)
			{
				return valA.localeCompare(valB); 
			}
			else
			{
				return valB.localeCompare(valA); 
			}
		}
		else if (col === LASTNAMECOL)
		{
			let valA = a.lastName;
			let valB = b.lastName;
			
			if (asc)
			{
				return valA.localeCompare(valB); 
			}
			else
			{
				return valB.localeCompare(valA); 
			}
		}
		else
		{
			let numA = parseFloat(a.average);
			let numB = parseFloat(b.average);
			
			if (Number.isNaN(numA) && Number.isNaN(numB))
			{
				return 0;
			}
			
			if (Number.isNaN(numA))
			{
				return 1;
			}
			
			if (Number.isNaN(numB))
			{
				return -1;
			}
			
			if (asc)
			{
				return numA - numB;
			}
			else
			{
				return numB - numA;
			}
		}
	});
	
	refreshRecordsView();
}


// Feature Functions

function clearStudentTable()
{
	clearAllStudents();
	
	buttonStates();
	updateStudentCount();
}


// Initial Page Setup

loadStudentsFromBackend();
buttonStates();


// Miscellaneous Debugging




// Event Listeners

allStudentsButton.addEventListener("click", renderAllStudents);
allStudentsButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#7f7fff";});
allStudentsButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#0000ff";});

clearStudentsButton.addEventListener("click", clearStudentTable);
clearStudentsButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#7f7fff";});
clearStudentsButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#0000ff";});

idHeader.addEventListener("click", function() { sortStudents(IDCOL); });
firstHeader.addEventListener("click", function() { sortStudents(FIRSTNAMECOL); });
lastHeader.addEventListener("click", function() { sortStudents(LASTNAMECOL); });
avgHeader.addEventListener("click", function() { sortStudents(AVGCOL); });
