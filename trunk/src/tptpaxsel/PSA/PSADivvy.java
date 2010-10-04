package tptpaxsel.PSA;

import java.util.Vector;

import tptpaxsel.CheckSetting;
import tptpaxsel.Obligation;
import tptpaxsel.Obligations;

/**
 * PSADivvy tries a binary search on the premises. First proof try is with half of all available premises, second with 1/4, third with 3/4 etc. 
 * It's a simplified implementation of Geoff Sutcliffe's Divvy.
 * 
 * @author daniel
 *
 */
public class PSADivvy implements PremiseSelectionAlgorithm{

	private int divides;
	private int timePerTry;
	private String[] provers;
	private boolean initialCheck;

	/**
	 * Basic constructur.
	 * 
	 * @param divides Number of divides. There will be 2^divides proof tries. Must be > 0.
	 * @param timePerTry Time in seconds for each proof try.
	 * @param provers The provers which will be used in the proof tries. Must be E,V or both.
	 */	
	public PSADivvy(int divides, int timePerTry,String[] provers) {
		if (divides < 1) {
			System.err.println("Divides must be a positive integer > 0");			
		}
		this.divides = divides;
		this.timePerTry = timePerTry;
		this.provers = provers;
		initialCheck = false;
	}
	
	/**
	 * If initialCheck = true, each obligation is checked with all premises first.
	 * 
	 * @param divides Number of divides. There will be 2^divides proof tries. Must be > 0.
	 * @param timePerTry Time in seconds for each proof try.
	 * @param provers The provers which will be used in the proof tries. Must be E,V or both.
	 * @param initialCheck If initialCheck = true, each obligation is checked with all premises first.
	 */
	public PSADivvy(int divides, int timePerTry,String[] provers, boolean initialCheck) {
		if (divides < 1) {
			System.err.println("Divides must be a positive integer > 0");			
		}
		this.divides = divides;
		this.timePerTry = timePerTry;
		this.provers = provers;
		this.initialCheck = initialCheck;
	}
		
	@Override
	public void changeCheckSettings(Obligations obligations) {
		int localDivides;
		int startValue;
		int halfStartValue;
		if (divides < 1) {
			System.err.println("Divides must be a positive integer > 0. Did not change CheckSettings");
			return;			
		}		
		for (int i=0; i < obligations.obligations.size(); i++) {
			// Settings
			Vector<CheckSetting> CSs = new Vector<CheckSetting>();
			Obligation obligation = obligations.obligations.elementAt(i);
			if (initialCheck) {
				for (String prover : provers) {
					CheckSetting CS = new CheckSetting(prover, timePerTry);			
					CSs.add(CS);
				}
			}
			int[] premisesList = new int[1];
			localDivides = 0;
			premisesList[0] = (int)Math.floor(obligation.premises.size()/2);
			int[] premisesLocal;
			for (String prover : provers) {
				CheckSetting CS = new CheckSetting(prover, timePerTry, (int)Math.floor(obligation.premises.size()/2));						
				CSs.add(CS);
			}
			
			while (localDivides < divides) {
				premisesLocal = new int[premisesList.length *2];
				startValue = premisesList[0];
				halfStartValue = (int)Math.floor(startValue/2);
				for( int j = 0; j < premisesList.length; j++) {
					for (String prover : provers) {
						CheckSetting CS = new CheckSetting(prover, timePerTry, premisesList[j]-halfStartValue);						
						CSs.add(CS);
					}
					for (String prover : provers) {
						CheckSetting CS = new CheckSetting(prover, timePerTry, premisesList[j]+halfStartValue);						
						CSs.add(CS);
					}
					premisesLocal[2*j] = premisesList[j]-halfStartValue;	
					premisesLocal[2*j+1] = premisesList[j]+halfStartValue;
				}			
				premisesList = premisesLocal;
				localDivides++;				
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
		System.out.println("% Using PSADivvy with the following settings:"+
				" divides = "+divides+
				" timePerTry = "+timePerTry+
				" initialCheck = "+initialCheck+				
				" provers = "+proversString);
	}

}


