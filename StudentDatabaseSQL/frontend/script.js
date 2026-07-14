// Element References

let firstBox = document.getElementById("firstName");
let lastBox = document.getElementById("lastName");
let studentIDBox = document.getElementById("studentIDsm");

let addStudentButton = document.getElementById("addStudentButton");
let renameStudentButton = document.getElementById("renameStudentButton");
let removeStudentButton = document.getElementById("removeStudentButton");

let studentMessage = document.getElementById("studentMessage");
let studentTable = document.getElementById("studentTable");
let idHeader = document.getElementById("idHeader");
let firstHeader = document.getElementById("firstHeader");
let lastHeader = document.getElementById("lastHeader");
let avgHeader = document.getElementById("avgHeader");

let addCourseButton = document.getElementById("addCourseButton");
let updateGradeButton = document.getElementById("updateGradeButton");
let removeCourseButton = document.getElementById("removeCourseButton");

let allStudentsButton = document.getElementById("allStudentsButton");
let oneStudentButton = document.getElementById("oneStudentButton");
let sortStudentsButton = document.getElementById("sortStudentsButton");

let studentCount = document.getElementById("studentCount");


// Application State

let nextStudentID = 10001;
let messageTimeout;
const IDCOL = 0;
const FIRSTNAMECOL = 1;
const LASTNAMECOL = 2;
const AVGCOL = 3;
let asc = true;
let currSortCol = IDCOL;
let students = [{
					id: 10000,
					firstName: "Jamon",
					lastName: "Smith",
					average: 99.20
				}];


// Helper Functions

function clearStudentForm()
{
	firstBox.value = "";
	lastBox.value = "";
	studentIDBox.value = "";
}

function timeoutMessage()
{
	studentMessage.textContent = "";
	studentMessage.className = "";
}

function inputMessage(type, message, focusElement, time)
{
	clearTimeout(messageTimeout);
	
	studentMessage.className = type;
	studentMessage.textContent = message;
	focusElement.focus();
	
	messageTimeout = setTimeout(timeoutMessage, time);
}

function getID()
{
	let id = parseInt(studentIDBox.value);
	
	if (Number.isNaN(id))
	{
		inputMessage("error", "Please provide an ID number", studentIDBox, 4000);
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

function buttonStates()
{
	let first = firstBox.value.trim();
	let last = lastBox.value.trim();
	let id = studentIDBox.value.trim();
	
	addStudentButton.disabled = (first === "" || last === "") || id !== "";
	renameStudentButton.disabled = (first === "" && last === "") || id === "";
	removeStudentButton.disabled = !((first === "" && last === "") && id !== "");
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
		inputMessage("error", "Please provide a first name", firstBox, 2000);
		return;
	}
	
	if (last === "")
	{
		inputMessage("error", "Please provide a last name", lastBox, 2000);
		return;
	}
	
	//addStudentRow(parseInt(nextStudentID), first, last, "N/A");
	
	let newStudent = {
						id: nextStudentID,
						firstName: first,
						lastName: last,
						average: "N/A"
					};
	
	students.push(newStudent);
	nextStudentID++;
	
	renderAllStudents();
	inputMessage("success", "Student successfully added!", firstBox, 2000);
	clearStudentForm();
	buttonStates();
	
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
		inputMessage("error", "Please provide the name you wish to change in the corresponding box above", firstBox, 4000);
		return;
	}
	
	let ind = findStudentIndexByID(id);
	
	if (ind < 0)
	{
		inputMessage("error", "Student not found", studentIDBox, 2000);
		return;
	}
	
	let student = students[ind];
	
	if (last === "")
	{
		student.firstName = first;
	}
	else if (first === "")
	{
		student.lastName = last;
	}
	else
	{
		student.firstName = first;
		student.lastName = last;
	}
	
	renderAllStudents();
	
	inputMessage("success", "Student name updated!", firstBox, 2000);
	clearStudentForm();
	buttonStates();
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
		inputMessage("error", "Student not found", studentIDBox, 2000);
		return;
	}
	
	let student = students[ind];
	
	let confirmed = confirm("Are you sure you want to remove: " + student.id + " " + student.first + " " + student.last + "?");
	
	if (!confirmed)
	{
		inputMessage("error", "Student removal canceled", studentIDBox, 2000);
		return;
	}
	
	students.splice(ind, 1);
	renderAllStudents();
	
	inputMessage("success", "Record removed!", studentIDBox, 2000);
	clearStudentForm();
	buttonStates();
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

buttonStates();
studentCount.textContent = "Total Students: " + (studentTable.rows.length - 1);
renderAllStudents();


// Event Listeners

idHeader.addEventListener("click", function() { sortStudents(IDCOL); });
firstHeader.addEventListener("click", function() { sortStudents(FIRSTNAMECOL); });
lastHeader.addEventListener("click", function() { sortStudents(LASTNAMECOL); });
avgHeader.addEventListener("click", function() { sortStudents(AVGCOL); });

firstBox.addEventListener("input", buttonStates);
lastBox.addEventListener("input", buttonStates);
studentIDBox.addEventListener("input", buttonStates);

addStudentButton.addEventListener("click", addToTable);
addStudentButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#ff7f7f";});
addStudentButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#ff0000";});

renameStudentButton.addEventListener("click", renameStudentRow);
renameStudentButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#ff7f7f";});
renameStudentButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#ff0000";});

removeStudentButton.addEventListener("click", removeStudentRow);
removeStudentButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#ff7f7f";});
removeStudentButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#ff0000";});

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


/*

DOM Manipulation
Incremental Development

*/