// Element References

let homeButton = document.getElementById("asBackButton");

let firstBox = document.getElementById("asFirstName");
let lastBox = document.getElementById("asLastName");

let addStudentButton = document.getElementById("addStudentButton");

let addStudentMessage = document.getElementById("addStudentMessage");


// Application State

let studentMessageTimeout;


// Helper Functions

function buttonState()
{
	let first = firstBox.value.trim();
	let last = lastBox.value.trim();
	
	addStudentButton.disabled = first === "" || last === "";
}

function clearBoxes()
{
	firstBox.value = "";
	lastBox.value = "";
}

function timeoutStudentMessage()
{
	addStudentMessage.textContent = "";
	addStudentMessage.className = "";
}

function inputStudentMessage(type, message, focusElement, time)
{
	clearTimeout(studentMessageTimeout);
	
	addStudentMessage.className = type;
	addStudentMessage.textContent = message;
	focusElement.focus();
	
	studentMessageTimeout = setTimeout(timeoutStudentMessage, time);
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
		
		inputStudentMessage("success", "Student successfully added!", firstBox, 2000);	
		
		clearBoxes();
		buttonState();
	}
	catch (error)
	{
		console.error("Error occurred: ", error);
		inputStudentMessage("error", error.message, firstBox, 2000);	
	}
}


// Initial Page Setup

buttonState();


// Event Listeners

homeButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#7f7f7f";});
homeButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#000000";});

addStudentButton.addEventListener("mouseover", (event) => {event.target.style.backgroundColor = "#ff7f7f";});
addStudentButton.addEventListener("mouseout", (event) => {event.target.style.backgroundColor = "#ff0000";});
addStudentButton.addEventListener("click", addToTable);

firstBox.addEventListener("input", buttonState);
lastBox.addEventListener("input", buttonState);