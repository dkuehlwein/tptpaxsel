package tptpaxsel.PSA;

import java.util.Vector;

import tptpaxsel.CheckSetting;
import tptpaxsel.Obligations;


/**
 * Simple Premise Selection Algorithm (PSA):
 * One Prover, running for Time seconds.
 * 
 * @author Daniel Kühlwein
 *
 */
public class PSASimple implements PremiseSelectionAlgorithm{

	String prover;
	int time;
	
	public PSASimple(String prover, int time) {
		this.prover = prover;
		this.time = time;
	}
	
	/**
	 * 	Changes the checkSettings of all obligations to this checkSetting.
	 * 
	 */
	@Override
	public void changeCheckSettings(Obligations obligations) {
		CheckSetting CS = new CheckSetting(prover, time);		
		for (int i=0; i < obligations.obligations.size(); i++) {
			Vector<CheckSetting> CSs = new Vector<CheckSetting>();
			CSs.add(CS);
			obligations.obligations.elementAt(i).checkSettings = CSs;			
		}
		
	}

	/**
	 *	Adds this checkSetting to all obligations.
	 * 
	 * @param obligations
	 */
	public void addCheckSettings(Obligations obligations) {
		CheckSetting CS = new CheckSetting(prover, time);		
		for (int i=0; i < obligations.obligations.size(); i++) {
			obligations.obligations.elementAt(i).checkSettings.add(CS);			
		}
		
	}

	@Override
	public void logInfo() {
		System.out.println("% Using PSASimple with prover "+prover+" and "+Integer.toString(time)+" seconds.");		
	}

}
