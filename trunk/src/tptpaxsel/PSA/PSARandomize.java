package tptpaxsel.PSA;

import java.util.Collections;

import tptpaxsel.Obligation;
import tptpaxsel.Obligations;

/**
 * Randomly changes the order of the premises of each obligation.
 * 
 * @author Daniel Kühlwein
 *
 */
public class PSARandomize implements PremiseSelectionAlgorithm {

	@Override
	public void changeCheckSettings(Obligations obligations) {
		for (int i=0; i < obligations.obligations.size(); i++) {
			Obligation obligation = obligations.obligations.elementAt(i);
			Collections.shuffle(obligation.premises);
		}
		
	}

	@Override
	public void logInfo() {
		System.out.println("% Using PSARandomize");		
	}
	

}
