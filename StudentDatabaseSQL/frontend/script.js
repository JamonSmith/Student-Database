const reset = "\x1b[0m";
const red = "\x1b[31m";
const green = "\x1b[32m";
const blue = "\x1b[34m";

let firstBox = document.getElementById("firstName");
let lastBox = document.getElementById("lastName");
let addstudentbutton = document.getElementById("addStudentButton");
let renameStudentButton = document.getElementById("renameStudentButton");
let removeStudentButton = document.getElementById("removeStudentButton");

console.log("Script loaded");

let studentTable = document.getElementById("studentTable");

function addToTable()
{
	let first = firstBox.value;
	let last = lastBox.value;
	
	let newRow = studentTable.insertRow();
	
	let idCell = newRow.insertCell(0);
	let firstCell = newRow.insertCell(1);
	let lastCell = newRow.insertCell(2);
	let avgCell = newRow.insertCell(3);
	
	idCell.textContent = "TEMP";
	firstCell.textContent = first;
	lastCell.textContent = last;
	avgCell.textContent = "N/A";
}


function add()
{
	console.log(red + "TODO" + green + "\nAdded:" + reset);
	console.log(firstBox.value);
	console.log(lastBox.value);
	console.log("\n");
}

function rename()
{
	console.log(red + "TODO" + green + "\nRenamed:" + reset);
	console.log(firstBox.value);
	console.log(lastBox.value);
	console.log("\n");
}

function remove()
{
	console.log(red + "TODO" + green + "\nRemoved:" + reset);
	console.log(firstBox.value);
	console.log(lastBox.value);
	console.log("\n");
}

addStudentButton.addEventListener("click", addToTable);
renameStudentButton.addEventListener("click", rename);
removeStudentButton.addEventListener("click", remove);

/*

DOM Manipulation
Incremental Development

*/