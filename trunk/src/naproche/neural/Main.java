/**
 * 
 */
package naproche.neural;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import naproche.PSA.PSAExistential;
import naproche.PSA.PSAPremiseGrowth;
import naproche.PSA.PSASimple;
import naproche.neural.example.Example;


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
		String[] provers = new String[2];
		provers[0] = "V";
		provers[1] = "E";
		PSAPremiseGrowth premiseGrowth = new PSAPremiseGrowth(5, "plus", 10, 5, 0, provers);
		PSASimple one = new PSASimple("V",5);
//		PSAExistential ex = new PSAExistential();
//		PSASimple two = new PSASimple("E",30);
		one.changeCheckSettings(obligations);
//		premiseGrowth.addCheckSettings(obligations);
//		setLog("landauPremiseGrowthEx5.log");
		one.logInfo();
//		premiseGrowth.logInfo();		
		obligations.checkObligations();
				
		/*
		example = new Example("Euclid");
		//example.runAprils();
		obligations = example.obligations;
		ex.changeCheckSettings(obligations);	
		
		setLog("euclidEx5.log");
		System.out.println("Example: "+example.name);
		System.out.println("----------------------------Test Start-----------------------------------");
        one = new PSASimple("E",5);
        ex.logInfo();
        one.logInfo();
		one.changeCheckSettings(obligations);
		obligations.checkObligations();
		System.out.println("----------------------------Test End-----------------------------------");
		System.out.println();
		System.out.println("----------------------------Test Start-----------------------------------");
		one = new PSASimple("V",5);
		ex.logInfo();
        one.logInfo();
		one.changeCheckSettings(obligations);
		obligations.checkObligations();
		System.out.println("----------------------------Test End-----------------------------------");
		System.out.println();
		System.out.println("----------------------------Test Start-----------------------------------");
		two = new PSASimple("E",5);
		ex.logInfo();
		one.logInfo();
		two.logInfo();
		one.changeCheckSettings(obligations);		
		two.addCheckSettings(obligations);
		obligations.checkObligations();
		System.out.println("----------------------------Test End-----------------------------------");
		
		setLog("euclidEx10.log");
		System.out.println("Example: "+example.name);
		System.out.println("----------------------------Test Start-----------------------------------");
        one = new PSASimple("E",10);
        ex.logInfo();
        one.logInfo();       
		one.changeCheckSettings(obligations);
		obligations.checkObligations();
		System.out.println("----------------------------Test End-----------------------------------");
		System.out.println();
		System.out.println("----------------------------Test Start-----------------------------------");
		one = new PSASimple("V",10);
		ex.logInfo();
        one.logInfo();
		one.changeCheckSettings(obligations);
		obligations.checkObligations();
		System.out.println("----------------------------Test End-----------------------------------");
		System.out.println();
		System.out.println("----------------------------Test Start-----------------------------------");
		two = new PSASimple("E",10);
		ex.logInfo();
		one.logInfo();
		two.logInfo();
		one.changeCheckSettings(obligations);		
		two.addCheckSettings(obligations);
		obligations.checkObligations();
		System.out.println("----------------------------Test End-----------------------------------");
		
		setLog("euclidEx30.log");
		System.out.println("Example: "+example.name);
		System.out.println("----------------------------Test Start-----------------------------------");
        one = new PSASimple("E",30);
        ex.logInfo();
        one.logInfo();       
		one.changeCheckSettings(obligations);
		obligations.checkObligations();
		System.out.println("----------------------------Test End-----------------------------------");
		System.out.println();
		System.out.println("----------------------------Test Start-----------------------------------");
		one = new PSASimple("V",30);
		ex.logInfo();
        one.logInfo();
		one.changeCheckSettings(obligations);
		obligations.checkObligations();
		System.out.println("----------------------------Test End-----------------------------------");
		System.out.println();
		System.out.println("----------------------------Test Start-----------------------------------");
		two = new PSASimple("E",30);
		ex.logInfo();
		one.logInfo();
		two.logInfo();
		one.changeCheckSettings(obligations);		
		two.addCheckSettings(obligations);
		obligations.checkObligations();
		System.out.println("----------------------------Test End-----------------------------------");

		setLog("euclidEx60.log");
		System.out.println("Example: "+example.name);
		System.out.println("----------------------------Test Start-----------------------------------");
        one = new PSASimple("E",60);
        ex.logInfo();
        one.logInfo();       
		one.changeCheckSettings(obligations);
		obligations.checkObligations();
		System.out.println("----------------------------Test End-----------------------------------");
		System.out.println();
		System.out.println("----------------------------Test Start-----------------------------------");
		one = new PSASimple("V",60);
		ex.logInfo();
        one.logInfo();
		one.changeCheckSettings(obligations);
		obligations.checkObligations();
		System.out.println("----------------------------Test End-----------------------------------");
		System.out.println();
		System.out.println("----------------------------Test Start-----------------------------------");
		two = new PSASimple("E",60);
		ex.logInfo();
		one.logInfo();
		two.logInfo();
		one.changeCheckSettings(obligations);		
		two.addCheckSettings(obligations);
		obligations.checkObligations();
		System.out.println("----------------------------Test End-----------------------------------");
	
		setLog("euclidEx120.log");
		System.out.println("Example: "+example.name);
		System.out.println("----------------------------Test Start-----------------------------------");
        one = new PSASimple("E",120);
        ex.logInfo();
        one.logInfo();       
		one.changeCheckSettings(obligations);
		obligations.checkObligations();
		System.out.println("----------------------------Test End-----------------------------------");
		System.out.println();
		System.out.println("----------------------------Test Start-----------------------------------");
		one = new PSASimple("V",120);
		ex.logInfo();
        one.logInfo();
		one.changeCheckSettings(obligations);
		obligations.checkObligations();
		System.out.println("----------------------------Test End-----------------------------------");
		System.out.println();
		System.out.println("----------------------------Test Start-----------------------------------");
		two = new PSASimple("E",120);
		ex.logInfo();
		one.logInfo();
		two.logInfo();
		one.changeCheckSettings(obligations);		
		two.addCheckSettings(obligations);
		obligations.checkObligations();
		System.out.println("----------------------------Test End-----------------------------------");
		
		setLog("euclidEx300.log");
		System.out.println("Example: "+example.name);
		System.out.println("----------------------------Test Start-----------------------------------");
        one = new PSASimple("E",300);
        ex.logInfo();
        one.logInfo();       
		one.changeCheckSettings(obligations);
		obligations.checkObligations();
		System.out.println("----------------------------Test End-----------------------------------");
		System.out.println();
		System.out.println("----------------------------Test Start-----------------------------------");
		one = new PSASimple("V",300);
		ex.logInfo();
        one.logInfo();
		one.changeCheckSettings(obligations);
		obligations.checkObligations();
		System.out.println("----------------------------Test End-----------------------------------");
		System.out.println();
		System.out.println("----------------------------Test Start-----------------------------------");
		two = new PSASimple("E",300);
		ex.logInfo();
		one.logInfo();
		two.logInfo();
		one.changeCheckSettings(obligations);		
		two.addCheckSettings(obligations);
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
