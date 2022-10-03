# Multiple-Processes-and-IPC-Project
The following text contains instructions on how to compile and run the project
as well as a list of the various files packaged alongside this README text
file.

"CPU.java" - The CPU driver program which fetches and executes
	     instruction from memory.
	     
"Memory.java" - The Memory program which serves to read/write
		values into/from an array of 2000 integers.
		
"sample1.txt" - Input data file which prints out A-Z and 0-9 followed by a newline 

"sample2.txt" - Input data file which prints out a smiley face in the form of 
		ASCII art
		
"sample3.txt" - Input data file which prints out a sequence of A's and numbers
		where the numbers differ based on the timer interval passed in 
		command line. A shorter timer would result in faster increments of the
		numbers while a longer timer would result in shorter increments.
"sample4.txt" - Input data file which prints out a series of numbers followed by a 
		user memory violation that prints out to terminal.
		
"sample5.txt" - Input data file which prints out the linux penguin in the form of
		ASCII art utilizing various symbols. Additionally prints out the 
		statement "Linux Rules!". By altering the value of the timer,
		different amounts of exclamation marks are printed out. A large
		timer value (such as 100) decreases the amount of exclamation marks while a
		smaller value (for example, 30) increases the amount of exclamation
		marks printed out.
		
"Project Summary" - Detailed description about the focus of the project, how it was
		    implemented, and my experience throughout the length of the project.

To compile the project, go to the command line and proceed to enter the directory
of where the source files are currently at. From there, enter the command
	javac CPU.java Memory.java
which compiles both java files into class files to be executed. To run the commands,
enter the following:
	java CPU <input_file> <timer_interval>
Where input_file is the name of the file you wish to run the user program and the
<timer_interval> sets the value of the timer which will run in the CPU program.

Be sure to set the input file in the same directory as the source files. In Eclipse,
you would place the file under the "src" folder in the Java Project folder. For any
UNIX environment, simply have the source files and input file in the same working
directory and execute the commands listed above.

It should be noted that the timer must be set to a value that is not a  
multiple to the number of instructions in the system code or in the interrupt 
handler. Choosing not to do so will result in an infinite loop which the timer 
goes off within the system call and will queue up infinitely many system calls.
For this, just simply change the value of the timer to a value larger than the one
that creates this infinite stall.
