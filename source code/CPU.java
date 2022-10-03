/**
 * Project #1: Exploring Multiple Processes and IPC
 * CS 4348.501
 * @author Anthony Jaramillo
 */

import java.io.*;
import java.util.Scanner;
import java.util.Random;
import java.lang.Runtime;

public class CPU {
	public static void main(String[] args) {
		try {
			// Instantiating new process running Memory.java
			String[] memoryCommand = {"java", "Memory", args[0]};
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(memoryCommand);
			
			// Getting pipes
			InputStream is = proc.getInputStream();
			OutputStream os = proc.getOutputStream();
			
			// Instantiating read/write streams
			PrintWriter pw = new PrintWriter(os);
			Scanner scan = new Scanner(is);
			
			// Used for stepping through program in terminal debugging
			Scanner stall = new Scanner(System.in);
			
			// Registers
			int PC = 0, IR = 0, AC = 0, X = 0, Y = 0;
			int SP = 1000;
			int timer = Integer.parseInt(args[1]);
			// Mode flag
			boolean isKernel = false;
			
			// Program execution stage
			while(IR != 50) {
				// Fetch instruction
				pw.printf("%d\n", PC);
				pw.flush();
				IR = scan.nextInt();
				//System.out.println("Instruction fetched is " + IR);
				// Execute instruction
				switch(IR) {
					// Load value 
					case 1:
						// Get value in next line of code and store in AC
						PC++;
						AC = getOperand(PC, isKernel, pw, scan);
						//System.out.println("AC: " + AC);
						break;
						
					// Load addr
					case 2:
						// Get address in next line of code, then get the 
						// value in that address and store in AC
						PC++;
						AC = getOperand(PC, isKernel, pw, scan);
						AC = getOperand(AC, isKernel, pw, scan);
						//System.out.println("AC: " + AC);
						break;
					
					// LoadInd addr
					case 3:
						// Get address in next line of code, then get
						// address in that address and store it in AC
						PC++;
						AC = getOperand(PC, isKernel, pw, scan);
						AC = getOperand(AC, isKernel, pw, scan);
						AC = getOperand(AC, isKernel, pw, scan);
						//System.out.println("AC: " + AC);
						break;
						
					// LoadIdxX addr
					case 4:
						// Get address in next line of code, add
						// with X and get value from the new address
						PC++;
						AC = getOperand(PC, isKernel, pw, scan);
						AC = getOperand(AC + X, isKernel, pw, scan);
						break;
					
					// LoadIdxY addr
					case 5:
						// Get address in next line of code, add
						// with Y and get value from the new address
						PC++;
						AC = getOperand(PC, isKernel, pw, scan);
						AC = getOperand(AC + Y, isKernel, pw, scan);
						//System.out.println("AC: " + AC);
						break;
						
					// LoadSpX
					case 6:
						// Get value from address of SP + X 
						// then store in AC
						AC = getOperand(SP + X, isKernel, pw, scan);
						//System.out.println("AC: " + AC);
						break;
						
					// Store addr
					case 7:
						// Get address in next line of code
						PC++;
						int address = getOperand(PC, isKernel, pw, scan); // Can possibly get value from system memory
						
						// Write AC into the address
						writeToMemory(address, AC, pw);
						//System.out.println("Address " + address + " saved as AC: " + AC);
						break;
						
					// Get
					case 8:
						// Storing random number from 1-100 in AC
						Random rand = new Random();
						AC = rand.nextInt(100) + 1;
						//System.out.println("AC: " + AC);
						break;
						
					// Put port
					case 9:
						// Get value in next line of code
						PC++;
						int port = getOperand(PC, isKernel, pw, scan);
						
						// Print AC depending on the value of port
						if(port == 1) {
							System.out.print(AC);
						}
						else {
							System.out.print((char) AC);
						}
						break;
						
					// AddX
					case 10:
						AC += X;
						//System.out.println("AC: " + AC);
						break;
					
					// AddY
					case 11:
						AC += Y;
						//System.out.println("AC: " + AC);
						break;
						
					// SubX
					case 12:
						AC -= X;
						//System.out.println("AC: " + AC);
						break;
						
					// SubY
					case 13:
						AC -= Y;
						//System.out.println("AC: " + AC);
						break;
						
					// CopyToX
					case 14:
						X = AC;
						//System.out.println("X: " + X);
						break;
						
					// CopyFromX
					case 15:
						AC = X;
						//System.out.println("AC: " + AC);
						break;
						
					// CopyToY
					case 16:
						Y = AC;
						//System.out.println("Y: " + Y);
						break;
						
					// CopyFromY
					case 17:
						AC = Y;
						//System.out.println("AC: " + AC);
						break;
						
					// CopyToSp
					case 18:
						SP = AC;
						//System.out.println("SP: " + SP);
						break;
						
					// CopyFromSp
					case 19:
						AC = SP;
						//System.out.println("AC: " + AC);
						break;
						
					// Jump addr
					case 20:
						// Get value in next line of code
						PC++;
						PC = getOperand(PC, isKernel, pw, scan); // Can possibly get value from system memory
						PC--;
						break;
					
					// JumpIfEqual addr
					case 21:
						PC++;
						// If AC is 0, get value in next line of code 
						// and store in PC
						if(AC == 0) {
							PC = getOperand(PC, isKernel, pw, scan); // Can possibly get value from system memory
							PC--;
						}
						break;
						
					// JumpIfNotEqual addr
					case 22:
						PC++;
						// If AC is not 0, get value in next line of code
						// and store in PC
						if(AC != 0) {
							PC = getOperand(PC, isKernel, pw, scan); // Can possibly get value from system memory
							PC--;
						}
						break;
						
					// Call addr
					case 23:
						// Move to next index on stack
						SP--;
						//System.out.println("SP: " + SP);
						
						// Push PC + 2 so that PC can return to the proper 
						// index upon popping from stack
						writeToMemory(SP, PC + 2, pw);
						
						// Get value in next line of code
						PC++;
						PC = getOperand(PC, isKernel, pw, scan); // Can possibly get value from system memory
						// Decrement to prevent the PC from skipping
						// next line of code
						PC--;
						break;
						
					// Ret
					case 24:
						//System.out.println("SP: " + SP);
						// Get the top of stack value and load it to PC
						PC = getOperand(SP, isKernel, pw, scan);
						// Prevent PC from skipping next instruction
						PC--;
						
						// Reset top of stack value
						writeToMemory(SP, 0, pw);
						// Move to next value on stack
						SP++;
						break;
						
					// IncX
					case 25:
						X++;
						//System.out.println("X: " + X);
						break;
						
					// DecX
					case 26:
						X--;
						//System.out.println("X: " + X);
						break;
						
					// Push
					case 27:
						// Move to next index from stack
						SP--;
						// Push AC to top of stack
						writeToMemory(SP, AC, pw);
						//System.out.println("SP: " + SP);
						break;
						
					// Pop
					case 28:
						//System.out.println("SP: " + SP);
						
						// Get top of stack value and load to AC
						AC = getOperand(SP, isKernel, pw, scan);
						//System.out.println("AC: " + AC);
						
						// Reset top of stack value
						writeToMemory(SP, 0, pw);
						
						// Move to next value on stack
						SP++;
						break;
						
					// Int
					case 29:
						if(isKernel == false) {
							// Entering kernel mode
							isKernel = true;
							
							// Push SP and PC to system stack
							writeToMemory(1999, SP, pw);
							writeToMemory(1998, PC, pw);
							
							// Set registers to proper indices
							PC = 1499;
							SP = 1998;
						}
						break;
						
					// IRet
					case 30:
						// Pop SP and PC from system stack
						PC = getOperand(1998, isKernel, pw, scan);
						SP = getOperand(1999, isKernel, pw, scan);
						
						// Return to user mode
						isKernel = false;
						
						// Prevents infinite loop if interrupt handler causes
						// timer to timeout after final system program 
						// instruction execution
						if(timer == 1) {
							timer = Integer.parseInt(args[1]);
						}
						break;
						
					// End
					case 50:
						pw.printf("End\n");
						pw.flush();
						break;
				}
				PC++;
				timer--;
				
				// Check for timeout
				if(timer == 0) {
					timer = Integer.parseInt(args[1]);
					if(isKernel == false) {
						// Entering kernel mode
						isKernel = true;
						
						// Push SP and PC to system stack
						writeToMemory(1999, SP, pw);
						writeToMemory(1998, PC - 1, pw);
						
						// Set registers to proper indices
						PC = 1000;
						SP = 1998;
					}
				}
				//System.out.println("PC is " + PC);
				//System.out.println("Timer is " + timer);
				//System.out.println("Enter to continue program");
				//stall.nextLine();
			}
			proc.waitFor();
			stall.close();
			scan.close();
		}
		catch(Throwable t) {
			t.printStackTrace();
		}
	}
	
	/**
	 * getOperand fetches the value at the PC and returns.
	 * Throws error if in user mode and accessing system memory.
	 * @param address	The address in memory to receive data from
	 * @param isKernel	Flag for detecting if in kernel or user mode
	 * @param pw		Output stream to send read requests to memory
	 * @param scan		Input stream to receive data from memory
	 */
	public static int getOperand(int address, boolean isKernel, PrintWriter pw, Scanner scan) throws Exception {
		// Check if in user mode and violating system memory
		if(isKernel == false && address > 999) {
			System.out.println("Memory Violation: accessing system address " + address + " in user mode.");
			// Signal memory to end
			pw.printf("End\n");
			pw.flush();
			// Terminate CPU program
			System.exit(1);
		}
		// Fetch value at PC
		pw.printf("%d\n", address);
		pw.flush();
		// Return if no violation occurred
		return scan.nextInt();
	}
	
	/**
	 * writeToMemory takes data and stores it at the given address
	 * in memory.
	 * @param address	
	 * @param data		
	 * @param pw		
	 */
	public static void writeToMemory(int address, int data, PrintWriter pw) {
		pw.printf("%d %d\n", address, data);
		pw.flush();
	}
}
