/**
 * 
 */
package tptpaxsel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import tptpaxsel.PSA.PSADivvy;
import tptpaxsel.PSA.PSAExistential;
import tptpaxsel.PSA.PSAPremiseGrowth;
import tptpaxsel.PSA.PSARandomize;
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
		
//	AtpApi aaa = new AtpApi("test/javanaproche/","test/javanaproche/obligations.nap");    
//    aaa.setSimple(0.0, 0.0, 1.0, "E", 3, 1);
//    aaa.runSingleByFilename("1-apod(1)-holds(apod(1), 10, 0).input");
		
		Example example;
		Obligations obligations;
		PSASimple one;
		PSASimple two;
		PSAPremiseGrowth premiseGrowth;
		PSADivvy divvy;
		PSAExistential ex = new PSAExistential();
		PSARandomize random = new PSARandomize();

		/* Copy pasteable foobar */
		String[] provers = new String[2];
		provers[0] = "V";
		provers[1] = "E";
		String[] proverV = new String[1];
		proverV[0] = "V";		
		premiseGrowth = new PSAPremiseGrowth(5, "plus", 10, 5, 0, provers);
		divvy = new PSADivvy(2, 5, provers, true);
		one = new PSASimple("V",300);
		two = new PSASimple("E",5);
		/* End Copy pasteable foobar */

		/* Start tests */
		example = new Example("Euclid");
		//example.runAprils();
		obligations = example.obligations;		
		//ex.deleteExistentialPremises(obligations);
		//random.changeCheckSettings(obligations);
		//Default values are weightAPRILS = 1; weightObligationGraph=1; maxTime = 5 * 1000		
		obligations.weightAPRILS = 1.0;
		obligations.weightNaproche = 1.0;
		obligations.weightObligationGraph = 1.0;
		obligations.maxTime = maxTime;
		//PSA Settings
		premiseGrowth = new PSAPremiseGrowth(200, "plus", 10, 1, 0, provers);
		// Log
		System.out.println("Example: "+example.name);		
		premiseGrowth.changeCheckSettings(obligations);
		obligations.checkObligations();
		/* Write machine readable stats Start */
		Statistics.printMachineStats(obligations, "foo.stats");
		Statistics.printProcessTimeline(obligations, 600, "foo.timeline");
		/* Write machine readable stats End */

//		/* Start tests */
//        example = new Example("Euclid");
//		//example.runAprils();
//		obligations = example.obligations;		
//		//ex.deleteExistentialPremises(obligations);
//		//random.changeCheckSettings(obligations);
//		//Default values are weightAPRILS = 1; weightObligationGraph=1; maxTime = 5 * 1000		
//		obligations.weightAPRILS = 1.0;
//		obligations.weightNaproche = 1.0;
//		obligations.weightObligationGraph = 1.0;
//		obligations.maxTime = maxTime;
//		//PSA Settings
//		divvy = new PSADivvy(2, 5, proverV, true);		
//		// Log
//		System.out.println("Example: "+example.name);		
//		divvy.changeCheckSettings(obligations);
//		obligations.checkObligations();
//		/* Write machine readable stats Start */
//		Statistics.printMachineStats(obligations, "EuclidDivvyV2tries5secN1A1.stats");
//		Statistics.printProcessTimeline(obligations, 600, "EuclidDivvyV2tries5secN1A1.timeline");
//		/* Write machine readable stats End */
//		
//		
//		/* Start tests */
//        example = new Example("Euclid");
//		//example.runAprils();
//		obligations = example.obligations;		
//		//ex.deleteExistentialPremises(obligations);
//		//random.changeCheckSettings(obligations);
//		//Default values are weightAPRILS = 1; weightObligationGraph=1; maxTime = 5 * 1000		
//		obligations.weightAPRILS = 1.0;
//		obligations.weightNaproche = 1.0;
//		obligations.weightObligationGraph = 1.0;
//		obligations.maxTime = maxTime;
//		//PSA Settings
//		premiseGrowth = new PSAPremiseGrowth(10, "plus", 10, 5, 0, provers);		
//		// Log
//		System.out.println("Example: "+example.name);		
//		premiseGrowth.changeCheckSettings(obligations);
//		obligations.checkObligations();
//		/* Write machine readable stats Start */
//		Statistics.printMachineStats(obligations, "EuclidVE10plus10time5sN1A1.stats");
//		Statistics.printProcessTimeline(obligations, 600, "EuclidVE10plus10time5sN1A1.timeline");
//		/* Write machine readable stats End */
//		
//		/* Start tests */
//        example = new Example("Euclid");
//		//example.runAprils();
//		obligations = example.obligations;		
//		//ex.deleteExistentialPremises(obligations);
//		//random.changeCheckSettings(obligations);
//		//Default values are weightAPRILS = 1; weightObligationGraph=1; maxTime = 5 * 1000		
//		obligations.weightAPRILS = 1.0;
//		obligations.weightNaproche = 1.0;
//		obligations.weightObligationGraph = 1.0;
//		obligations.maxTime = maxTime;
//		//PSA Settings
//		divvy = new PSADivvy(2, 5, provers, true);		
//		// Log
//		System.out.println("Example: "+example.name);		
//		divvy.changeCheckSettings(obligations);
//		obligations.checkObligations();
//		/* Write machine readable stats Start */
//		Statistics.printMachineStats(obligations, "EuclidDivvyVE2tries5secN1A1.stats");
//		Statistics.printProcessTimeline(obligations, 600, "EuclidDivvyVE2tries5secN1A1.timeline");
//		/* Write machine readable stats End */
//	
//		/* Start tests */
//        example = new Example("Mizar");
//		//example.runAprils();
//		obligations = example.obligations;		
//		//ex.deleteExistentialPremises(obligations);
//		//random.changeCheckSettings(obligations);
//		//Default values are weightAPRILS = 1; weightObligationGraph=1; maxTime = 5 * 1000		
//		obligations.weightAPRILS = 0.0;
//		obligations.weightNaproche = 1.0;
//		obligations.weightObligationGraph = 1.0;
//		obligations.maxTime = maxTime;
//		//PSA Settings
//		premiseGrowth = new PSAPremiseGrowth(10, "plus", 10, 5, 0, proverV);		
//		// Log
//		System.out.println("Example: "+example.name);		
//		premiseGrowth.changeCheckSettings(obligations);
//		obligations.checkObligations();
//		/* Write machine readable stats Start */
//		Statistics.printMachineStats(obligations, "MizarV10plus10time5sN1A0.stats");
//		Statistics.printProcessTimeline(obligations, 600, "MizarV10plus10time5sN1A0.timeline");
//		/* Write machine readable stats End */
//	
//		/* Start tests */
//        example = new Example("Mizar");
//		//example.runAprils();
//		obligations = example.obligations;		
//		//ex.deleteExistentialPremises(obligations);
//		//random.changeCheckSettings(obligations);
//		//Default values are weightAPRILS = 1; weightObligationGraph=1; maxTime = 5 * 1000		
//		obligations.weightAPRILS = 1.0;
//		obligations.weightNaproche = 0.0;
//		obligations.weightObligationGraph = 1.0;
//		obligations.maxTime = maxTime;
//		//PSA Settings
//		premiseGrowth = new PSAPremiseGrowth(10, "plus", 10, 5, 0, proverV);		
//		// Log
//		System.out.println("Example: "+example.name);		
//		premiseGrowth.changeCheckSettings(obligations);
//		obligations.checkObligations();
//		/* Write machine readable stats Start */
//		Statistics.printMachineStats(obligations, "MizarV10plus10time5sN0A1.stats");
//		Statistics.printProcessTimeline(obligations, 600, "MizarV10plus10time5sN0A1.timeline");
//		/* Write machine readable stats End */
//	
//		/* Start tests */
//        example = new Example("MizarHard");
//		//example.runAprils();
//		obligations = example.obligations;		
//		//ex.deleteExistentialPremises(obligations);
//		//random.changeCheckSettings(obligations);
//		//Default values are weightAPRILS = 1; weightObligationGraph=1; maxTime = 5 * 1000		
//		obligations.weightAPRILS = 0.0;
//		obligations.weightNaproche = 1.0;
//		obligations.weightObligationGraph = 1.0;
//		obligations.maxTime = maxTime;
//		//PSA Settings
//		premiseGrowth = new PSAPremiseGrowth(10, "plus", 10, 5, 0, proverV);		
//		// Log
//		System.out.println("Example: "+example.name);		
//		premiseGrowth.changeCheckSettings(obligations);
//		obligations.checkObligations();
//		/* Write machine readable stats Start */
//		Statistics.printMachineStats(obligations, "MizarHardV10plus10time5sN1A0.stats");
//		Statistics.printProcessTimeline(obligations, 600, "MizarHardV10plus10time5sN1A0.timeline");
//		/* Write machine readable stats End */
//	
//		/* Start tests */
//        example = new Example("MizarHard");
//		//example.runAprils();
//		obligations = example.obligations;		
//		//ex.deleteExistentialPremises(obligations);
//		//random.changeCheckSettings(obligations);
//		//Default values are weightAPRILS = 1; weightObligationGraph=1; maxTime = 5 * 1000		
//		obligations.weightAPRILS = 1.0;
//		obligations.weightNaproche = 0.0;
//		obligations.weightObligationGraph = 1.0;
//		obligations.maxTime = maxTime;
//		//PSA Settings
//		premiseGrowth = new PSAPremiseGrowth(10, "plus", 10, 5, 0, proverV);		
//		// Log
//		System.out.println("Example: "+example.name);		
//		premiseGrowth.changeCheckSettings(obligations);
//		obligations.checkObligations();
//		/* Write machine readable stats Start */
//		Statistics.printMachineStats(obligations, "MizarHardV10plus10time5sN0A1.stats");
//		Statistics.printProcessTimeline(obligations, 600, "MizarHardV10plus10time5sN0A1.timeline");
//		/* Write machine readable stats End */
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
