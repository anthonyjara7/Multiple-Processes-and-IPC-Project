/**
 * Project #1: Exploring Multiple Processes and IPC
 * CS 4348.501
 * @author Anthony Jaramillo
 */

import java.io.*;
import java.util.Scanner;

public class Memory {
	// User memory 0-999
	// System memory 1000-1999
	private static int[] memory = new int[2000];
	
	public static void main(String[] args) {
			// Setup memory from passed argument
			setMemory(args[0]);
			
			// Scanner reads input from CPU
			Scanner input = new Scanner(System.in);
			String line;
			
			// Loop for I/O requests
			while((line = input.nextLine()).equals("End\n") == false) {
				String[] command = line.split(" ");
				// One argument was read, output data at address
				// given in argument
				if(command.length == 1) {
					read(Integer.parseInt(command[0]));
				}
				// Two arguments were read, write data to memory array
				else {
					write(Integer.parseInt(command[0]), 
						  Integer.parseInt(command[1]));
				}
			}
			input.close();
	}
	
	/**
	 * Reads the address and returns the value in memory
	 * @param address	Index for memory output
	 */
	public static void read(int address) {
		System.out.println(memory[address]);
	}
	
	/**
	 * Write the data into the address in memory
	 * @param address	Index to write data to
	 * @param data		Data being written into memory
	 */
	public static void write(int address, int data) {
		memory[address] = data;
	}
	
	/**
	 * Reads the input file and stores it in memory array
	 * @param fileName Name of file located within same package
	 */
	public static void setMemory(String fileName) {
		try {
		File file = new File(fileName);
		Scanner scan = new Scanner(file);
		
		// Step through each line in file
		for(int i = 0; scan.hasNext(); i++) {
			String buffer = scan.next();
			// If '.' is detected, set memory index to the following number
			if(buffer.charAt(0) == '.') {
				i = Integer.parseInt(buffer.substring(1)) - 1;
			}
			// If comments or blank line detected, skip to next line
			else if(buffer.charAt(0) == '/' || buffer.charAt(0) == '\n' 
					|| !(Character.isDigit(buffer.charAt(0)))) {
				scan.nextLine();
				i--;
			}
			else {
				memory[i] = Integer.parseInt(buffer);
			}
		}
		scan.close();
		}
		catch(Throwable t){
			t.printStackTrace();
		}
	}
	
}