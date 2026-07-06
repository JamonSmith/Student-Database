# StudentDatabaseSQL

Java console application that extends my original Student Database project by replacing 
text-file data storage with an SQLite relational database using JDBC. This version 
demonstrates database design, SQL querying, and Java database connectivity while 
maintaining the object-oriented structure of the original project.

## Features

- Display all students in the database (id number, first name, last name, and average
  grade).
- Display an individual student's transcript including courses, grades, and overall
  average.
- Add new students to the database.
- Rename existing students.
- Add courses and grades to an existing student.
- Update existing course grades in a student's records.
- Remove courses from a student's records.
- Remove a student and all their information.
- Sort students by first name, last name, or average grade.
- Confirmation prompts for destructive operations.
- Input validation and SQL exception handling.
- House all data in an SQLite relational database using JDBC.

## Technologies

- Java 21.0.11
- SQLite 3.53.2
- SQLite JDBC Driver (Xerial)

## SQL Skills Demonstrated

- SELECT
- INSERT
- DELETE
- UPDATE
- INNER JOIN
- LEFT JOIN
- GROUP BY
- Aggregate functions (AVG, COUNT, MIN, MAX)
- Prepared Statements
- Parameterized Queries

## Skills Demonstrated

- Object-Oriented Programming
- Relational Database Design
- SQL Query Development
- JDBC Database Connectivity
- CRUD Operations
- Exception Handling
- Input Validation
- Console-Based Application Development
- Refactoring and Code Reuse
- Defensive Programming

## Future Improvements

- Refactor into separate database, model, and user interface classes.
- Build web-based front end using HTML, CSS, and JavaScript while reusing existing
  database logic.

## Project Evolution

Version 1
- In-memory student management using Java Collections.

Version 2
- Persistent storase using text files and file I/O.

Version 3
- SQLite relational database with JDBC connectivity and SQL queries.
