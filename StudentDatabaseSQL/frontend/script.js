// Element References

let firstBox = document.getElementById("firstName");
let lastBox = document.getElementById("lastName");
let studentIDBoxSM = document.getElementById("studentIDsm");

let studentMessage = document.getElementById("studentMessage");

let addStudentButton = document.getElementById("addStudentButton");
let renameStudentButton = document.getElementById("renameStudentButton");
let removeStudentButton = document.getElementById("removeStudentButton");

let studentIDBoxCM = document.getElementById("studentIDcm");
let courseBox = document.getElementById("courseName");
let gradeBox = document.getElementById("courseGrade");

let courseMessage = document.getElementById("courseMessage");

let addCourseButton = document.getElementById("addCourseButton");
let updateGradeButton = document.getElementById("updateGradeButton");
let removeCourseButton = document.getElementById("removeCourseButton");

let allStudentsButton = document.getElementById("allStudentsButton");
let oneStudentButton = document.getElementById("oneStudentButton");
let sortStudentsButton = document.getElementById("sortStudentsButton");

let idHeader = document.getElementById("idHeader");
let firstHeader = document.getElementById("firstHeader");
let lastHeader = document.getElementById("lastHeader");
let avgHeader = document.getElementById("avgHeader");

let studentTable = document.getElementById("studentTable");
let studentCount = document.getElementById("studentCount");


// Class

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
	
	addCourse(name, grade = "N/A")
	{
		let course = new Course(name, grade);
		this.courses.push(course);
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

let nextStudentID = 10001;
let studentMessageTimeout;
let courseMessageTimeout;
const IDCOL = 0;
const FIRSTNAMECOL = 1;
const LASTNAMECOL = 2;
const AVGCOL = 3;
let asc = true;
let currSortCol = IDCOL;
let students = [new Student(10000, "Jamon", "Smith", 99.25)];


// Helper Functions

function clearStudentForm()
{
	firstBox.value = "";
	lastBox.value = "";
	studentIDBoxSM.value = "";
}

function clearCourseForm()
{
	studentIDBoxCM.value = "";
	courseBox.value = "";
	gradeBox.value = "";
}

function timeoutStudentMessage()
{
	studentMessage.textContent = "";
	studentMessage.className = "";
}

function timeoutCourseMessage()
{
	courseMessage.textContent = "";
	courseMessage.className = "";
}

function inputStudentMessage(type, message, focusElement, time)
{
	clearTimeout(studentMessageTimeout);
	
	studentMessage.className = type;
	studentMessage.textContent = message;
	focusElement.focus();
	
	studentMessageTimeout = setTimeout(timeoutStudentMessage, time);
}

function inputCourseMessage(type, message, focusElement, time)
{
	clearTimeout(courseMessageTimeout);
	
	courseMessage.className = type;
	courseMessage.textContent = message;
	focusElement.focus();
	
	courseMessageTimeout = setTimeout(timeoutCourseMessage, time);
}

function getID()
{
	let id = parseInt(studentIDBoxSM.value);
	
	if (Number.isNaN(id))
	{
		inputStudentMessage("error", "Please provide an ID number", studentIDBoxSM, 4000);
		return null;
	}
	
	return id;
}

function findStudentIndexByID(id)
{
	for (let i = 0; i < students.length; i++)
	{
		if (id === students[i].id)
		{
			return i;
		}
	}
	
	return -1;
}

function updateStudentCount()
{
	studentCount.textContent = "Total Students: " + students.length;
}

function studentButtonStates()
{
	let first = firstBox.value.trim();
	let last = lastBox.value.trim();
	let id = studentIDBoxSM.value.trim();
	
	addStudentButton.disabled = (first === "" || last === "") || id !== "";
	renameStudentButton.disabled = (first === "" && last === "") || id === "";
	removeStudentButton.disabled = !((first === "" && last === "") && id !== "");
}

function courseButtonStates()
{
	let id = studentIDBoxCM.value.trim();
	let name = courseBox.value.trim();
	let grade = gradeBox.value.trim();
	
	addCourseButton.disabled = id === "" || name === "";
	updateGradeButton.disabled = id === "" || name === "" || grade === "";
	removeCourseButton.disabled = (id === "" || name === "") || grade !== "";
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
	clearAllStudents();
	
	for (const s of students)
	{
		addStudentRow(s.id, s.firstName, s.lastName, s.average);
	}
	
	allStudentsButton.disabled = true;
	updateStudentCount();
}

function clearAllStudents()
{
	for (let i = studentTable.rows.length - 1; i > 0; i--)
	{
		studentTable.rows[i].remove();
	}
}


// Feature Functions

function addToTable()
{
	let first = firstBox.value.trim();
	let last = lastBox.value.trim();
	
	if (first === "")
	{
		inputStudentMessage("error", "Please provide a first name", firstBox, 2000);
		return;
	}
	
	if (last === "")
	{
		inputStudentMessage("error", "Please provide a last name", lastBox, 2000);
		return;
	}
	
	//addStudentRow(parseInt(nextStudentID), first, last, "N/A");
	
	let newStudent = new Student(nextStudentID, first, last);
	
	students.push(newStudent);
	nextStudentID++;
	
	renderAllStudents();
	inputStudentMessage("success", "Student successfully added!", firstBox, 2000);
	clearStudentForm();
	studentButtonStates();
	
	//alert("MEEHEEEHEEHEEHEE >:)");
}

function renameStudentRow()
{
	let id = getID();
	let first = firstBox.value.trim();
	let last = lastBox.value.trim();
	
	if (id === null)
	{
		return;
	}
	
	if (first === "" && last === "")
	{
		inputStudentMessage("error", "Please provide the name you wish to change in the corresponding box above", firstBox, 4000);
		return;
	}
	
	let ind = findStudentIndexByID(id);
	
	if (ind < 0)
	{
		inputStudentMessage("error", "Student not found", studentIDBoxSM, 2000);
		return;
	}
	
	let student = students[ind];
	
	student.rename(first, last);
	
	renderAllStudents();
	
	inputStudentMessage("success", "Student name updated!", firstBox, 2000);
	clearStudentForm();
	studentButtonStates();
}

function removeStudentRow()
{
	let id = getID();
	
	if (id === null)
	{
		return;
	}
	
	let ind = findStudentIndexByID(id);
	
	if (ind < 0)
	{
		inputStudentMessage("error", "Student not found", studentIDBoxSM, 2000);
		return;
	}
	
	let student = students[ind];
	
	let confirmed = confirm("Are you sure you want to remove: " + student.id + " " + student.getFullName() + "?");
	
	if (!confirmed)
	{
		inputStudentMessage("error", "Student removal canceled", studentIDBoxSM, 2000);
		return;
	}
	
	students.splice(ind, 1);
	renderAllStudents();
	
	inputStudentMessage("success", "Record removed!", studentIDBoxSM, 2000);
	clearStudentForm();
	studentButtonStates();
}

function addCourseToStudent()
{
	let id = parseInt(studentIDBoxCM.value);
	let name = courseBox.value.trim();
	let grade = gradeBox.value.trim();
	
	if (Number.isNaN(id) || id === "")
	{
		inputCourseMessage("error", "Please provide a valid ID number", studentIDBoxCM, 2000);
		return;
	}
	
	if (name === "")
	{
		inputCourseMessage("error", "Please provide a course name", courseBox, 2000);
		return;
	}
	
	let ind = findStudentIndexByID(id);
	
	if (ind < 0)
	{
		inputCourseMessage("error", "Student not found", studentIDBoxCM, 2000);
		return;
	}
	
	let g = "N/A";
	
	if (grade !== "")
	{
		 g = parseFloat(grade);
		 
		 if (Number.isNaN(g) || g > 100 || g < 0)
		 {
			inputCourseMessage("error", "Grade must be a value within range [0, 100]", gradeBox, 2000);
			return;
		 }
	}
	
	let student = students[ind];
	
	student.addCourse(name, g);
	inputCourseMessage("success", "Course added", studentIDBoxCM, 2000);
	clearCourseForm();
	courseButtonStates();
	
	console.log(student.courses);
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
	
	renderAllStudents();
	
	console.log(currSortCol);
	console.log(asc);
}

function clearStudentTable()
{
	clearAllStudents();
	
	allStudentsButton.disabled = false;
	updateStudentCount();
}


// Miscellaneous Debug Section




// Initial Page Setup

studentButtonStates();
courseButtonStates();
studentCount.textContent = "Total Students: " + (studentTable.rows.length - 1);
renderAllStudents();


// Event Listeners

firstBox.addEventListener("input", studentButtonStates);
lastBox.addEventListener("input", studentButtonStates);
studentIDBoxSM.addEventListener("input", studentButtonStates);

addStudentButton.addEventListener("click", addToTable);
addStudentButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#ff7f7f";});
addStudentButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#ff0000";});

renameStudentButton.addEventListener("click", renameStudentRow);
renameStudentButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#ff7f7f";});
renameStudentButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#ff0000";});

removeStudentButton.addEventListener("click", removeStudentRow);
removeStudentButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#ff7f7f";});
removeStudentButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#ff0000";});

studentIDBoxCM.addEventListener("input", courseButtonStates);
courseBox.addEventListener("input", courseButtonStates);
gradeBox.addEventListener("input", courseButtonStates);

addCourseButton.addEventListener("click", addCourseToStudent);
addCourseButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#7fff7f";});
addCourseButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#00ff00";});

updateGradeButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#7fff7f";});
updateGradeButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#00ff00";});

removeCourseButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#7fff7f";});
removeCourseButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#00ff00";});

allStudentsButton.addEventListener("click", renderAllStudents);
allStudentsButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#7f7fff";});
allStudentsButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#0000ff";});

oneStudentButton.addEventListener("click", clearStudentTable);
oneStudentButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#7f7fff";});
oneStudentButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#0000ff";});

//sortStudentsButton.addEventListener("click", sortStudents);
sortStudentsButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#7f7fff";});
sortStudentsButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#0000ff";});

idHeader.addEventListener("click", function() { sortStudents(IDCOL); });
firstHeader.addEventListener("click", function() { sortStudents(FIRSTNAMECOL); });
lastHeader.addEventListener("click", function() { sortStudents(LASTNAMECOL); });
avgHeader.addEventListener("click", function() { sortStudents(AVGCOL); });


/*

DOM Manipulation
Incremental Development

*/