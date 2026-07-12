// Element References

let firstBox = document.getElementById("firstName");
let lastBox = document.getElementById("lastName");
let studentIDBox = document.getElementById("studentID");

let addStudentButton = document.getElementById("addStudentButton");
let renameStudentButton = document.getElementById("renameStudentButton");
let removeStudentButton = document.getElementById("removeStudentButton");

let studentMessage = document.getElementById("studentMessage");
let studentTable = document.getElementById("studentTable");

let addCourseButton = document.getElementById("addCourseButton");
let updateGradeButton = document.getElementById("updateGradeButton");
let removeCourseButton = document.getElementById("removeCourseButton");

let allStudentsButton = document.getElementById("allStudentsButton");
let oneStudentButton = document.getElementById("oneStudentButton");
let sortStudentsButton = document.getElementById("sortStudentsButton");


// Application State

let nextStudentID = 10001;
let messageTimeout;


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

function findStudentRowByID(id)
{
	for (let i = 1; i < studentTable.rows.length; i++)
	{
		let currID = parseInt(studentTable.rows[i].cells[0].textContent);
		
		if (id === currID)
		{
			return studentTable.rows[i];
		}
	}
	
	return null;
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
	
	addStudentRow(parseInt(nextStudentID), first, last, "N/A");
	
	nextStudentID++;
	
	inputMessage("success", "Student successfully added!", firstBox, 2000);
	clearStudentForm();
	
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
	
	let row = findStudentRowByID(id);
	
	if (row === null)
	{
		inputMessage("error", "Student not found", studentIDBox, 2000);
		return;
	}
	
	if (last === "")
	{
		row.cells[1].textContent = first;
	}
	else if (first === "")
	{
		row.cells[2].textContent = last;
	}
	else
	{
		row.cells[1].textContent = first;
		row.cells[2].textContent = last;
	}
	
	inputMessage("success", "Student name updated!", firstBox, 2000);
	clearStudentForm();
}

function removeStudentRow()
{
	let id = getID();
	
	if (id === null)
	{
		return;
	}
	
	let row = findStudentRowByID(id);
	
	if (row === null)
	{
		inputMessage("error", "Student not found", studentIDBox, 2000);
		return;
	}
	
	let first = row.cells[1].textContent;
	let last = row.cells[2].textContent;
	
	let confirmed = confirm("Are you sure you want to remove: " + id + " " + first + " " + last + "?");
	
	if (!confirmed)
	{
		inputMessage("error", "Student removal canceled", studentIDBox, 2000);
		return;
	}
	
	row.remove();
	inputMessage("success", "Record removed!", studentIDBox, 2000);
	clearStudentForm();
}


// Event Listeners

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

allStudentsButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#7f7fff";});
allStudentsButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#0000ff";});

oneStudentButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#7f7fff";});
oneStudentButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#0000ff";});

sortStudentsButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#7f7fff";});
sortStudentsButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#0000ff";});


/*

DOM Manipulation
Incremental Development

*/