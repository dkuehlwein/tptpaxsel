package tptpaxsel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * 
 * Provides general statistics creating methods.
 * 
 * @author Daniel Kühlwein
 *
 */
public class Statistics {

	/**
	 * Calculates how many obligations were proved at each second, and prints the results on the Printstream.
	 * The line n contains the number of axioms that were proven at time n. 
	 * 
	 * @param obligations 
	 * @param time Time in seconds
	 * @param out Where to print the output
	 */
	static public void printProcessTimeline(Obligations obligations, int time, PrintStream out) {
		int[] timeStats = new int[time];
		for (int i : timeStats) {
			i = 0;			
		}									
		for (Obligation o : obligations.obligations) {
			for (int i=0; i < timeStats.length; i++) {					
				if (o.stats.getProofTime() < i && o.stats.getResult()) {	
					timeStats[i] = timeStats[i]+1;						
				}
			}				
		}
		for (int i : timeStats) {
			out.println(i);
		}		
	}
	
	/**
	 * Calculates how many obligations were proved at each second, and prints the results into the file fileName.
	 * The line n contains the number of axioms that were proven at time n. 
	 * 
	 * @param obligations 
	 * @param time Time in seconds
	 * @param fileName The name of the output File
	 */
	static public void printProcessTimeline(Obligations obligations, int time, String fileName) {
		PrintStream out;
		try {
			out = new PrintStream(new FileOutputStream(fileName));
			printProcessTimeline(obligations, time, out);
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		}		
	}	
	
	/**
	 * Prints all the statistics information of obligations in machine readable format (columns, separated by tabs) on the Printstream.
	 * 
	 * @param obligations
	 * @param out
	 */
	static public void printMachineStats(Obligations obligations, PrintStream out) {
		ObligationStatistics stats = new ObligationStatistics();
		stats.printHeader(out);
		for (Obligation o : obligations.obligations) {
			o.stats.printMachine(out);
		}
	}
	
	/**
	 * Prints all the statistics information of obligations in machine readable format (columns, separated by tabs) in the file fileName.
	 * 
	 * @param obligations
	 * @param fileName
	 */
	static public void printMachineStats(Obligations obligations, String fileName) {
		try {
			PrintStream out = new PrintStream(new FileOutputStream(fileName));
			printMachineStats(obligations, out);		
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		}
	}
}
