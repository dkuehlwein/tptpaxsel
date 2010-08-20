package tptpaxsel.PSA;

import java.util.Vector;

import tptpaxsel.CheckSetting;
import tptpaxsel.Obligation;
import tptpaxsel.Obligations;


/**
 * PSAPremiseGrowth is a PSA that creates several proof tries with an increasing number of premises.
 * 
 * @author Daniel Kühlwein
 *
 */
public class PSAPremiseGrowth implements PremiseSelectionAlgorithm{
	
	int premisesStart;
	String premisesOperation;
	double premisesGrowth;
	int timeStart;
	int timeGrowth;
	String[] provers;	

	/**
	 * Standard Constructor
	 * 
	 * @param premisesStart			The number of premises for the first proof try.
	 * @param premisesOperation		plus or mul. Defines the growth operation: premises = premises premisesOperation premisesGrowth
	 * @param premisesGrowth		Determines how many premises will are added in each iteration. Must be > 1.
	 * @param timeStart				The time (in seconds) for the first proof try.
	 * @param timeGrowth			Determines how many seconds will are added in each iteration. 
	 * @param provers				The provers which will be used in the proof tries.
	 */
	public PSAPremiseGrowth(int premisesStart, String premisesOperation,
			double premisesGrowth, int timeStart, int timeGrowth,
			String[] provers) {		
		this.premisesStart = premisesStart;
		this.premisesOperation = premisesOperation;
		this.premisesGrowth = premisesGrowth;
		if (premisesGrowth < 1) {
			System.err.println("Tried to create PSAProofGraph with insufficied premises growth: "+premisesOperation+premisesGrowth+". Using default value 10 instead");
			this.premisesGrowth = 10;
		} 
		this.timeStart = timeStart;
		this.timeGrowth = timeGrowth;
		this.provers = provers;
		if (provers.equals(null)) {
			System.err.println("Empty prover array for PSAProofGraph. Using default value E instead");
			this.provers = new String[1];
			this.provers[0] = "E";
		}
	}
	
	@Override
	public void changeCheckSettings(Obligations obligations) {
		int time;
		double premises;				
		int premisesMax;
		for (int i=0; i < obligations.obligations.size(); i++) {
			// Settings
			time = timeStart;
			premises = premisesStart;
			Vector<CheckSetting> CSs = new Vector<CheckSetting>();						
			Obligation obligation = obligations.obligations.elementAt(i);
			premisesMax = obligation.premises.size();
			// premises must grow
			while (premises <= premisesMax) {
				for (String prover : provers) {
					CheckSetting CS = new CheckSetting(prover, time, premises);			
					CSs.add(CS);
				}			
				// Update the values
				if (premisesOperation.equals("plus")) {
					premises = premises + premisesGrowth;
				} else {
					premises = premises * premisesGrowth;
				}
				time = time + timeGrowth;
			}
			obligation.checkSettings = CSs;			
		}		
	}
	
	/**
	 *	Adds this checkSetting to all obligations.
	 * 
	 * @param obligations
	 */
	public void addCheckSettings(Obligations obligations) {
		int time;
		double premises;				
		int premisesMax;
		for (int i=0; i < obligations.obligations.size(); i++) {
			// Settings
			time = timeStart;
			premises = premisesStart;
			Obligation obligation = obligations.obligations.elementAt(i);
			Vector<CheckSetting> CSs = obligation.checkSettings;
			premisesMax = obligation.premises.size();
			// premises must grow
			while (premises < premisesMax) {
				for (String prover : provers) {
					CheckSetting CS = new CheckSetting(prover, time, premises);			
					CSs.add(CS);
				}			
				// Update the values
				if (premisesOperation.equals("plus")) {
					premises = premises + premisesGrowth;
				} else {
					premises = premises * premisesGrowth;
				}
				time = time + timeGrowth;
			}			
			obligation.checkSettings = CSs;			
		}
		
	}

	@Override
	public void logInfo() {
		String proversString = "";
		for (String s : provers) {
			proversString = proversString+s+" ";
		}
		System.out.println("% Using PSAProofGraph with the following settings:  premisesStart = "+premisesStart+
																			" premisesOperation = "+premisesOperation+
																			" premisesGrowth = "+premisesGrowth+
																			" timeStart = "+timeStart+
																			" timeGrowth = "+timeGrowth+
																			" provers = "+proversString);		
	}

}
