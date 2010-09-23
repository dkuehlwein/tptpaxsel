/**
 * 
 */
package tptpaxsel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import tptpaxsel.PSA.PSAExistential;
import tptpaxsel.PSA.PSAPremiseGrowth;
import tptpaxsel.PSA.PSASimple;

import tptpaxsel.example.Example;


/**
 * @author Daniel Kühlwein
 *
 */
public class Main {
	
	static private PrintStream stdOut = System.out;
	static private double weightObligationEdges = 1.0;
	static private double weightAPRILS = 0.0;
	static private double weightNaproche = 1.0;
	static private int maxTime = 300; /* Currently not used!!! */
	
	/**
	 * Hack your tests here.
	 * Supported provers are E ("E") and Vampire ("V"). The Vampire binary must be in /lib/VAMPIRE
	 * 
	 * @param args
	 */	
	public static void main(String[] args) {
		Example example;
		Obligations obligations;
		PSASimple one;
		PSASimple two;
		PSAPremiseGrowth premiseGrowth;
		PSAExistential ex = new PSAExistential();
		
		/* Copy pasteable foobar */
		String[] provers = new String[2];
		provers[0] = "V";
		provers[1] = "E";
		premiseGrowth = new PSAPremiseGrowth(5, "plus", 10, 5, 0, provers);		
		two = new PSASimple("E",5);
		/* End Copy pasteable foobar */
		
		
		/* Start tests */
        example = new Example("Euclid");
		//example.runAprils();
		obligations = example.obligations;		
		ex.deleteExistentialPremises(obligations);
		//Default values are weightAPRILS = 1; weightObligationGraph=1; maxTime = 5 * 1000		
		obligations.weightAPRILS = 0.0;
		obligations.weightNaproche = 1.0;
		obligations.weightObligationGraph = 1.0;
		obligations.maxTime = maxTime;
		//PSA Settings
		one = new PSASimple("E",300);		
		// Log
		System.out.println("Example: "+example.name);
		one.changeCheckSettings(obligations);
		obligations.checkObligations();
		/* Write machine readable stats Start */
		Statistics.printMachineStats(obligations, "EuclidE300N1A0ExDel.stats");
		Statistics.printProcessTimeline(obligations, 300, "EuclidE300N1A0ExDel.timeline");
		/* Write machine readable stats End */
		
		/* Start tests */
        example = new Example("Euclid");
		//example.runAprils();
		obligations = example.obligations;		
		//Default values are weightAPRILS = 1; weightObligationGraph=1; maxTime = 5 * 1000		
		obligations.weightAPRILS = 1.0;
		obligations.weightNaproche = 1.0;
		obligations.weightObligationGraph = 1.0;
		obligations.maxTime = maxTime;
		//PSA Settings
		one = new PSASimple("E",300);		
		// Log
		System.out.println("Example: "+example.name);
		one.changeCheckSettings(obligations);
		obligations.checkObligations();
		/* Write machine readable stats Start */
		Statistics.printMachineStats(obligations, "EuclidE300N1A1.stats");
		Statistics.printProcessTimeline(obligations, 300, "EuclidE300N1A1.timeline");
		/* Write machine readable stats End */
		
		/* Start tests */
        example = new Example("Euclid");
		//example.runAprils();
		obligations = example.obligations;		
		ex.deleteExistentialPremises(obligations);
		//Default values are weightAPRILS = 1; weightObligationGraph=1; maxTime = 5 * 1000		
		obligations.weightAPRILS = 1.0;
		obligations.weightNaproche = 1.0;
		obligations.weightObligationGraph = 1.0;
		obligations.maxTime = maxTime;
		//PSA Settings
		one = new PSASimple("E",300);		
		// Log
		System.out.println("Example: "+example.name);
		one.changeCheckSettings(obligations);
		obligations.checkObligations();
		/* Write machine readable stats Start */
		Statistics.printMachineStats(obligations, "EuclidE300N1A1ExDel.stats");
		Statistics.printProcessTimeline(obligations, 300, "EuclidE300N1A1ExDel.timeline");
		/* Write machine readable stats End */

	}
	
	/* Redirect output stream to file */
	static public void setLog(String File) {		
        try {            
            System.setOut(new PrintStream(new FileOutputStream(File)));            
        } catch (FileNotFoundException ex) {
        	System.out.println("Cannot direct StdOut to file "+File);
            ex.printStackTrace();
            return;
        }
	}
	
	static public void stdOutReset() {
		System.setOut(stdOut);
	}

}
