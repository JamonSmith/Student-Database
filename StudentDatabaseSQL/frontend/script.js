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

let studentIDBoxVR = document.getElementById("studentIDvr");

let recordsMessage = document.getElementById("recordsMessage");

let allStudentsButton = document.getElementById("allStudentsButton");
let oneStudentButton = document.getElementById("oneStudentButton");
let sortStudentsButton = document.getElementById("sortStudentsButton");

let idHeader = document.getElementById("idHeader");
let firstHeader = document.getElementById("firstHeader");
let lastHeader = document.getElementById("lastHeader");
let avgHeader = document.getElementById("avgHeader");

let studentTableTitle = document.getElementById("studentTableTitle");
let studentTable = document.getElementById("studentTable");
let studentCount = document.getElementById("studentCount");

let studentIDandName = document.getElementById("studentIDandName");

let courseHeader = document.getElementById("courseHeader");
let gradeHeader = document.getElementById("gradeHeader");

let courseTableTitle = document.getElementById("courseTableTitle");
let courseTable = document.getElementById("courseTable");
let studentAverage = document.getElementById("studentAverage");
let courseCount = document.getElementById("courseCount");


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

let studentMessageTimeout;
let courseMessageTimeout;
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

function timeoutRecordsMessage()
{
	recordsMessage.textContent = "";
	recordsMessage.className = "";
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

function inputRecordsMessage(type, message, focusElement, time)
{
	clearTimeout(recordsMessageTimeout);
	
	recordsMessage.className = type;
	recordsMessage.textContent = message;
	focusElement.focus();
	
	recordsMessageTimeout = setTimeout(timeoutRecordsMessage, time);
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
	if (!allStudentsButton.disabled)
	{
		studentCount.textContent = "";	
	}
	else
	{	
		studentCount.textContent = "Total Students: " + students.length;
	}
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

function recordsButtonStates()
{
	let id = studentIDBoxVR.value.trim();
	
	allStudentsButton.disabled = studentTable.rows.length !== 1;
	oneStudentButton.disabled = id === "";
	sortStudentsButton.disabled = studentTable.rows.length === 1 && courseTableTitle.textContent === "";
}

function refreshRecordsView()
{
	if (selectedStudentID === null)
	{
		renderAllStudents();
		return;
	}
	
	let ind = findStudentIndexByID(selectedStudentID);
	
	if (ind < 0)
	{
		selectedStudentID = null;
		renderAllStudents();
		return;
	}
	
	let student = students[ind];
	
	renderStudentCourses(student);
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
		
		refreshRecordsView();
	}
	catch (error)
	{
		console.error("Error occured: ", error);
	}
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
	clearAllCourses();
	
	for (const s of students)
	{
		addStudentRow(s.id, s.firstName, s.lastName, s.average);
	}
	
	allStudentsButton.disabled = true;
	updateStudentCount();
	
	recordsButtonStates();
}

function clearAllStudents()
{
	for (let i = studentTable.rows.length - 1; i > 0; i--)
	{
		studentTable.rows[i].remove();
	}
	
	studentCount.textContent = "";
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
	clearAllStudents();
	clearAllCourses();
	
	courseTableTitle.textContent = "Selected Student Info:";
	studentIDandName.textContent = student.id + ": " + student.lastName + ", " + student.firstName;
	
	for (const course of student.courses)
	{
		addStudentCourseRow(course.name, course.grade);
	}
	
	studentAverage.textContent = "Average: " + student.average;
	courseCount.textContent = "Courses taken: " + student.getCourseCount();
	
	recordsButtonStates();
}
 
function clearAllCourses()
{
	courseTableTitle.textContent = "";
	studentIDandName.textContent = "";
	
	for (let i = courseTable.rows.length - 1; i > 0; i--)
	{
		courseTable.rows[i].remove();
	}
	
	studentAverage.textContent = "";
	courseCount.textContent = "";
}


// Feature Functions

async function addToTable()
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
	
	try
	{
		let response = await fetch("http://localhost:8000/students", 
						{ 
							method: "POST", 
							headers: 
							{ 
								"Content-Type": "application/json" 
							}, 
							body: JSON.stringify( 
							{ 
								firstName: first, 
								lastName: last
							}) 
						});
						
		let data = await response.json();
		
		if (!response.ok)
		{
			throw new Error(data.error || "Could not add student");
		}
		
		console.log(data);
		
		await loadStudentsFromBackend();
		
		inputStudentMessage("success", "Student successfully added!", firstBox, 2000);	
		
		clearStudentForm();
		studentButtonStates();
	}
	catch (error)
	{
		console.error("Error occured: ", error);
		inputStudentMessage("error", error.message, firstBox, 2000);	
	}
	
	//alert("MEEHEEEHEEHEEHEE >:)");
}

async function renameStudentRow()
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
	
	try
	{
		let response = await fetch("http://localhost:8000/students", 
						{ 
							method: "PUT", 
							headers: 
							{ 
								"Content-Type": "application/json" 
							}, 
							body: JSON.stringify( 
							{ 
								studentID: id, 
								firstName: first, 
								lastName: last
							}) 
						});
						
		let data = await response.json();
		
		if (!response.ok)
		{
			throw new Error(data.error || "Could not rename student");
		}
		
		console.log(data);
		
		await loadStudentsFromBackend();
		
		inputStudentMessage("success", "Student successfully renamed!", studentIDBoxSM, 2000);	
		
		clearStudentForm();
		studentButtonStates();
	}
	catch (error)
	{
		console.error("Error occured: ", error);
		inputStudentMessage("error", error.message, firstBox, 2000);	
	}
}

async function removeStudentRow()
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
	}
	
	if (ind > -1)
	{
		let student = students[ind];
		
		let confirmed = confirm("Are you sure you want to remove: " + student.id + " " + student.getFullName() + "?");
	
		if (!confirmed)
		{
			inputStudentMessage("error", "Student removal canceled", studentIDBoxSM, 2000);
			return;
		}
	}
	
	try
	{
		let response = await fetch("http://localhost:8000/students", 
						{ 
							method: "DELETE", 
							headers: 
							{ 
								"Content-Type": "application/json" 
							}, 
							body: JSON.stringify( 
							{ 
								studentID: id
							}) 
						});
						
		let data = await response.json();
						
		if (!response.ok)
		{
			throw new Error(data.error || "Could not remove student");
		}
		
		console.log(data);
		
		await loadStudentsFromBackend();
		
		inputStudentMessage("success", "Record removed!", studentIDBoxSM, 2000);	
		
		clearStudentForm();
		studentButtonStates();
	}
	catch (error)
	{
		//console.error("Error occurred: ", error);
		inputStudentMessage("error", error.message, studentIDBoxSM, 2000);	
	}
}

async function addCourseToStudent()
{
	let id = parseInt(studentIDBoxCM.value);
	let name = courseBox.value.trim();
	let grade = gradeBox.value.trim();
	
	if (Number.isNaN(id))
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
	
	try
	{
		let response;
		
		if (grade === "")
		{
			response = await fetch("http://localhost:8000/courses", 	
						{ 
							method: "POST", 
							headers: 
							{ 
								"Content-Type": "application/json" 
							}, 
							body: JSON.stringify( 
							{ 
								studentID: id, 
								courseName: name
							}) 
						});
		}
		else
		{
			response = await fetch("http://localhost:8000/courses", 	
						{ 
							method: "POST", 
							headers: 
							{ 
								"Content-Type": "application/json" 
							}, 
							body: JSON.stringify( 
							{ 
								studentID: id, 
								courseName: name,
								courseGrade: g
							}) 
						});
		}
		
		let data = await response.json();
						
		if (!response.ok)
		{
			throw new Error(data.error || "Could not add course");
		}
		
		console.log(data);
		
		await loadStudentsFromBackend();
		
		inputCourseMessage("success", "Course added!", studentIDBoxCM, 2000);	
		
		studentIDBoxCM.value = "";
		courseBox.value = "";
		gradeBox.value = "";
		
		courseButtonStates();
	}
	catch (error)
	{
		console.error("Error occured: ", error);
		inputCourseMessage("error", error.message, studentIDBoxCM, 2000);	
	}
}

async function updateCourseGradeForStudent()
{
	let id = parseInt(studentIDBoxCM.value);
	let course = courseBox.value.trim();
	let grade = gradeBox.value.trim();
	
	if (Number.isNaN(id))
	{
		inputCourseMessage("error", "Please provide a valid ID number", studentIDBoxCM, 2000);
		return;
	}
	
	if (course === "")
	{
		inputCourseMessage("error", "Please provide a course name", courseBox, 2000);
		return;
	}
	
	if (grade === "")
	{
		inputCourseMessage("error", "Please provide a new course grade", gradeBox, 2000);
		return;
	}
	
	let g = parseFloat(grade);
		 
	if (Number.isNaN(g) || g > 100 || g < 0)
	{
		inputCourseMessage("error", "Grade must be a value within range [0, 100]", gradeBox, 2000);
		return;
	}
	
	let ind = findStudentIndexByID(id);
	
	if (ind < 0)
	{
		inputCourseMessage("error", "Student not found", studentIDBoxCM, 2000);
		return;
	}
	
	try
	{
		let response = await fetch("http://localhost:8000/courses", 	
						{ 
							method: "PUT", 
							headers: 
							{ 
								"Content-Type": "application/json" 
							}, 
							body: JSON.stringify( 
							{ 
								studentID: id, 
								courseName: course,
								courseGrade: g
							}) 
						});
		
		let data = await response.json();
						
		if (!response.ok)
		{
			throw new Error(data.error || "Could not update course grade");
		}
		
		console.log(data);
		
		await loadStudentsFromBackend();
		
		inputCourseMessage("success", "Course grade updated!", studentIDBoxCM, 2000);	
		
		studentIDBoxCM.value = "";
		courseBox.value = "";
		gradeBox.value = "";
		
		courseButtonStates();
	}
	catch (error)
	{
		console.error("Error occured: ", error);
		inputCourseMessage("error", error.message, studentIDBoxCM, 2000);	
	}
}

async function removeCourseFromStudent()
{
	let id = parseInt(studentIDBoxCM.value);
	let course = courseBox.value.trim();
	
	if (Number.isNaN(id))
	{
		inputCourseMessage("error", "Please provide a valid ID number", studentIDBoxCM, 2000);
		return;
	}
	
	if (course === "")
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
	
	try
	{
		let response = await fetch("http://localhost:8000/courses", 	
						{ 
							method: "DELETE", 
							headers: 
							{ 
								"Content-Type": "application/json" 
							}, 
							body: JSON.stringify( 
							{ 
								studentID: id, 
								courseName: course
							}) 
						});
		
		let data = await response.json();
						
		if (!response.ok)
		{
			throw new Error(data.error || "Could not remove course");
		}
		
		console.log(data);
		
		await loadStudentsFromBackend();
		
		inputCourseMessage("success", "Course removed!", studentIDBoxCM, 2000);	
		
		studentIDBoxCM.value = "";
		courseBox.value = "";
		
		courseButtonStates();
	}
	catch (error)
	{
		console.error("Error occured: ", error);
		inputCourseMessage("error", error.message, studentIDBoxCM, 2000);	
	}
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
	
	console.log(currSortCol);
	console.log(asc);
}

function showOneStudent()
{
	let id = parseInt(studentIDBoxVR.value);
	
	if (Number.isNaN(id))
	{
		inputRecordsMessage("error", "Please provide a valid ID number", studentIDBoxVR, 2000);
		return;
	}
	
	let ind = findStudentIndexByID(id);
	
	if (ind < 0)
	{
		inputRecordsMessage("error", "Student not found", studentIDBoxVR, 2000);
		return;
	}
	
	let student = students[ind];
	
	selectedStudentID = student.id;
	renderStudentCourses(student);
	
	studentIDBoxVR.value = "";
	studentIDBoxVR.focus();
	recordsButtonStates();
}

function clearStudentTable()
{
	clearAllStudents();
	
	allStudentsButton.disabled = false;
	updateStudentCount();
}

function clearCourseTable()
{
	clearAllCourses();
	
	recordsButtonStates();
}


// Miscellaneous Debug Section




// Initial Page Setup

studentButtonStates();
courseButtonStates();
recordsButtonStates();
loadStudentsFromBackend();


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

updateGradeButton.addEventListener("click", updateCourseGradeForStudent);
updateGradeButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#7fff7f";});
updateGradeButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#00ff00";});

removeCourseButton.addEventListener("click", removeCourseFromStudent);
removeCourseButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#7fff7f";});
removeCourseButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#00ff00";});

studentIDBoxVR.addEventListener("input", recordsButtonStates);

allStudentsButton.addEventListener("click", renderAllStudents);
allStudentsButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#7f7fff";});
allStudentsButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#0000ff";});

oneStudentButton.addEventListener("click", showOneStudent);
oneStudentButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#7f7fff";});
oneStudentButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#0000ff";});

sortStudentsButton.addEventListener("click", function() { clearStudentTable(); clearCourseTable(); });
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