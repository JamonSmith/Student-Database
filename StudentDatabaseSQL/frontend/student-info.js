let params = new URLSearchParams(window.location.search);
let studentID = parseInt(params.get("id"));

// Element References

let homeButton = document.getElementById("srBackButton");

let idNumber = document.getElementById("studentIDNumber");
let lastName = document.getElementById("studentLastName");
let firstName = document.getElementById("studentFirstName");
let courseTable = document.getElementById("courseTable");

let courseHeader = document.getElementById("courseHeader");
let gradeHeader = document.getElementById("gradeHeader");

let courseCount = document.getElementById("courseCount");
let studentAverage = document.getElementById("studentAverage");

let courseNameBox = document.getElementById("courseNameBox");
let clearCourseNameBoxButton = document.getElementById("clearCourseNameBoxButton");

let courseGradeBox = document.getElementById("courseGradeBox");
let clearCourseGradeBoxButton = document.getElementById("clearCourseGradeBoxButton");

let courseMessage = document.getElementById("courseMessage");

let addCourseButton = document.getElementById("addCourseButton");
let updateCourseButton = document.getElementById("updateCourseButton");
let removeCourseButton = document.getElementById("removeCourseButton");

let removeStudentButton = document.getElementById("removeStudentButton");

// Application State

let courseMessageTimeout;
let student = null;
const COURSECOL = 0;
const GRADECOL = 1;
let asc = true;
let currSortCol = COURSECOL;
let selectedRow =  null;


// Helper Functions

function timeoutCourseMessage()
{
	courseMessage.textContent = "";
	courseMessage.className = "";
}

function inputCourseMessage(type, message, focusElement, time)
{
	clearTimeout(courseMessageTimeout);
	
	courseMessage.className = type;
	courseMessage.textContent = message;
	focusElement.focus();
	
	courseMessageTimeout = setTimeout(timeoutCourseMessage, time);
}

function buttonStates()
{
	addCourseButton.disabled = courseNameBox.value === "";
	updateCourseButton.disabled = !(courseNameBox.value !== "" && courseGradeBox.value !== "");
	removeCourseButton.disabled = (courseNameBox.value === "" || courseGradeBox.value !== "");
}

function clearBoxes()
{
	courseNameBox.value = "";
	courseGradeBox.value = "";
}

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
	
	newRow.style.cursor = "pointer";
	
	newRow.addEventListener("click", function () 
									{ 
										populateCourseField(course); 
										buttonStates(); 
										
										if (selectedRow !== null)
										{
											selectedRow.classList.remove("selected-row");
										}
										
										selectedRow = newRow;
										selectedRow.classList.add("selected-row");
									});
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

function populateCourseField(course)
{
	courseNameBox.value = course;
}

function removeSelectedRow()
{
	if (selectedRow !== null)
	{
		selectedRow.classList.remove("selected-row");
		selectedRow = null;
	}
}

function handleDocumentClick(event)
{
	if (selectedRow !== null && event.target.closest("tr") !== selectedRow)
	{
		removeSelectedRow();
	}
}


// Feature Functions

async function loadStudent()
{
	let response = await fetch("http://localhost:8000/students");
	
	let data = await response.json();
	
	student = data.find(function(studentData) { return studentData.id === studentID; });
	
	if (student === undefined)
	{
		console.log("student not found");
		return;
	}
	
	renderStudentCourses(student);
}

async function addCourse()
{
	let name = courseNameBox.value.trim();
	let grade = courseGradeBox.value.trim();
	
	if (name === "")
	{
		inputCourseMessage("error", "Please provide a course name", courseNameBox, 2000);
		return;
	}
	
	let g = "N/A";
	
	if (grade !== "")
	{
		 g = parseFloat(grade);
		 
		 if (Number.isNaN(g) || g > 100 || g < 0)
		 {
			inputCourseMessage("error", "Grade must be a value within range [0, 100]", courseGradeBox, 2000);
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
								studentID: student.id, 
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
								studentID: student.id, 
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
		
		await loadStudent();
		
		inputCourseMessage("success", "Course added!", courseNameBox, 2000);	
		
		clearBoxes();
		buttonStates();
	}
	catch (error)
	{
		console.log("Error occured: ", error);
		inputCourseMessage("error", error.message, courseNameBox, 2000);	
	}
}

async function updateCourse()
{
	let course = courseNameBox.value.trim();
	let grade = courseGradeBox.value.trim();
	
	if (course === "")
	{
		inputCourseMessage("error", "Please provide a course name", courseNameBox, 2000);
		return;
	}
	
	if (grade === "")
	{
		inputCourseMessage("error", "Please provide a new course grade", courseGradeBox, 2000);
		return;
	}
	
	let g = parseFloat(grade);
		 
	if (Number.isNaN(g) || g > 100 || g < 0)
	{
		inputCourseMessage("error", "Grade must be a value within range [0, 100]", courseGradeBox, 2000);
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
								studentID: student.id, 
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
		
		await loadStudent();
		
		inputCourseMessage("success", "Course grade updated!", courseNameBox, 2000);	
		
		clearBoxes();
		buttonStates();
	}
	catch (error)
	{
		console.log("Error occured: ", error);
		inputCourseMessage("error", error.message, courseNameBox, 2000);	
	}
}

async function removeCourse()
{
	let course = courseNameBox.value.trim();
	
	if (course === "")
	{
		inputCourseMessage("error", "Please provide a course name", courseBox, 2000);
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
								studentID: student.id, 
								courseName: course
							}) 
						});
		
		let data = await response.json();
						
		if (!response.ok)
		{
			throw new Error(data.error || "Could not remove course");
		}
		
		console.log(data);
		
		await loadStudent();
		
		inputCourseMessage("success", "Course removed!", courseNameBox, 2000);	
		
		clearBoxes();
		buttonStates();
	}
	catch (error)
	{
		console.log("Error occured: ", error);
		inputCourseMessage("error", error.message, courseNameBox, 2000);	
	}
}

async function removeStudent()
{
	let confirmed = confirm("Are you sure you want to remove: Student " + student.id + " - " + student.lastName + ", " + student.firstName + "?");
	
	if (!confirmed)
	{
		inputCourseMessage("error", "Student removal canceled", courseNameBox, 2000);
		return;
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
								studentID: student.id
							}) 
						});
						
		let data = await response.json();
						
		if (!response.ok)
		{
			throw new Error(data.error || "Could not remove student");
		}
		
		console.log(data);
		
		window.location.href = "students.html";
	}
	catch (error)
	{
		console.error("Error occurred: ", error);
		inputCourseMessage("error", error.message, courseNameBox, 2000);	
	}
}

function sortCourses(col)
{
	removeSelectedRow();
	
	if (currSortCol === col)
	{
		asc = !asc;
	}
	else 
	{
		currSortCol = col;
		asc = true;
	}
	
	student.courses.sort(function(a, b) 
	{ 
		if (col === COURSECOL)
		{
			let valA = a.name;
			let valB = b.name;
			
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
			let numA = parseFloat(a.grade);
			let numB = parseFloat(b.grade);
			
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
	
	renderStudentCourses(student);
}


// Initial Page Setup

loadStudent();
buttonStates();


// Event Listeners

homeButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#7f7f7f";});
homeButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#000000";});

courseHeader.addEventListener("click", function () { sortCourses(COURSECOL); clearBoxes(); buttonStates(); });
gradeHeader.addEventListener("click", function () { sortCourses(GRADECOL); clearBoxes(); buttonStates(); });

document.addEventListener("click", handleDocumentClick);

courseNameBox.addEventListener("input", buttonStates);
clearCourseNameBoxButton.addEventListener("click", function () 
													{ 
														courseNameBox.value = ""; 
														buttonStates(); 
														removeSelectedRow();
													});
clearCourseNameBoxButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#bfbfbf";});
clearCourseNameBoxButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#ffffff";});

courseGradeBox.addEventListener("input", buttonStates);
clearCourseGradeBoxButton.addEventListener("click", function () { courseGradeBox.value = ""; buttonStates(); });
clearCourseGradeBoxButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#bfbfbf";});
clearCourseGradeBoxButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#ffffff";});

addCourseButton.addEventListener("click", addCourse);
addCourseButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#7fff7f";});
addCourseButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#00ff00";});

updateCourseButton.addEventListener("click", updateCourse);
updateCourseButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#7fff7f";});
updateCourseButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#00ff00";});

removeCourseButton.addEventListener("click", removeCourse);
removeCourseButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#7fff7f";});
removeCourseButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#00ff00";});

removeStudentButton.addEventListener("click", removeStudent);
removeStudentButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#ff7f7f";});
removeStudentButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#ff0000";});

