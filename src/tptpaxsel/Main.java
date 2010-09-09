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
		/* Start tests */
        Example example = new Example("Landau");
		//example.runAprils();
		Obligations obligations = example.obligations;		
		//Default values are weightAPRILS = 1; weightObligationGraph=1; maxTime = 5 * 1000		
		obligations.weightAPRILS = weightAPRILS;
		obligations.weightNaproche = weightNaproche;
		obligations.weightObligationGraph = weightObligationEdges;
		obligations.maxTime = maxTime;
		//PSA Settings
		String[] provers = new String[2];
		provers[0] = "V";
		provers[1] = "E";
		PSAPremiseGrowth premiseGrowth = new PSAPremiseGrowth(5, "plus", 10, 5, 0, provers);
		PSASimple one = new PSASimple("V",5);
		PSASimple two = new PSASimple("E",5);
		PSAExistential ex = new PSAExistential();
		// Log
		System.out.println("Example: "+example.name);
		one.changeCheckSettings(obligations);
		obligations.checkObligations();
		/* Write machine readable stats Start */
		Statistics.printMachineStats(obligations, "testoutput");
		Statistics.printProcessTimeline(obligations, 30, "timeline");
		/* Write machine readable stats End */
		
		//Set CheckSettings
		//ex.changeCheckSettings(obligations);
		//ex.deleteExistentialPremises(obligations);
		/*
		one.changeCheckSettings(obligations);
		two.addCheckSettings(obligations);
		//premiseGrowth.addCheckSettings(obligations);
		setLog("landau55ExEV5.log");
		System.out.println("----------------------------Test Start-----------------------------------");
		ex.logInfo();
		one.logInfo();
		two.logInfo();
		//premiseGrowth.logInfo();		
		obligations.checkObligations();
		System.out.println("----------------------------Test End-----------------------------------");
		
		one = new PSASimple("V",10);
		two = new PSASimple("E", 10);
		//premiseGrowth = new PSAPremiseGrowth(5, "plus", 10, 10, 0, provers);
		//Set CheckSettings
		one.changeCheckSettings(obligations);
		two.addCheckSettings(obligations);
		//premiseGrowth.addCheckSettings(obligations);
		setLog("landau55ExEV10.log");
		System.out.println("----------------------------Test Start-----------------------------------");
		ex.logInfo();
		one.logInfo();
		two.logInfo();
		//premiseGrowth.logInfo();		
		obligations.checkObligations();		
		System.out.println("----------------------------Test End-----------------------------------");
		
		one = new PSASimple("V",30);
		two = new PSASimple("E", 30);
		//premiseGrowth = new PSAPremiseGrowth(5, "plus", 10, 30, 0, provers);
		//Set CheckSettings
		one.changeCheckSettings(obligations);
		two.addCheckSettings(obligations);
		//premiseGrowth.addCheckSettings(obligations);
		setLog("landau55ExEV30.log");
		System.out.println("----------------------------Test Start-----------------------------------");
		ex.logInfo();
		one.logInfo();
		two.logInfo();
		//premiseGrowth.logInfo();		
		obligations.checkObligations();		
		System.out.println("----------------------------Test End-----------------------------------");
		
		one = new PSASimple("V",60);
		two = new PSASimple("E", 60);
		//premiseGrowth = new PSAPremiseGrowth(5, "plus", 10, 60, 0, provers);
		//Set CheckSettings
		one.changeCheckSettings(obligations);
		//two.addCheckSettings(obligations);
		//premiseGrowth.addCheckSettings(obligations);
		setLog("test.log");
		System.out.println("----------------------------Test Start-----------------------------------");
		ex.logInfo();
		one.logInfo();
		two.logInfo();
		//premiseGrowth.logInfo();		
		obligations.checkObligations();		
		System.out.println("----------------------------Test End-----------------------------------");
		
		one = new PSASimple("V",120);
		two = new PSASimple("E", 120);
		//premiseGrowth = new PSAPremiseGrowth(5, "plus", 10, 120, 0, provers);
		//Set CheckSettings
		one.changeCheckSettings(obligations);
		two.addCheckSettings(obligations);
		//premiseGrowth.addCheckSettings(obligations);
		setLog("landau55ExEV120.log");
		System.out.println("----------------------------Test Start-----------------------------------");
		ex.logInfo();
		one.logInfo();
		two.logInfo();
		//premiseGrowth.logInfo();		
		obligations.checkObligations();		
		System.out.println("----------------------------Test End-----------------------------------");
				
		one = new PSASimple("V",300);
		two = new PSASimple("E", 300);
		//premiseGrowth = new PSAPremiseGrowth(5, "plus", 10, 300, 0, provers);
		//Set CheckSettings
		one.changeCheckSettings(obligations);
		two.addCheckSettings(obligations);
		//premiseGrowth.addCheckSettings(obligations);
		setLog("landau55ExEV300.log");
		System.out.println("----------------------------Test Start-----------------------------------");
		ex.logInfo();
		one.logInfo();
		two.logInfo();
		//premiseGrowth.logInfo();		
		obligations.checkObligations();		
		System.out.println("----------------------------Test End-----------------------------------");
		*/
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
