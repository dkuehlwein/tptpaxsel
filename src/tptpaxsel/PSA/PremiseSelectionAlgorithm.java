/**
 * 
 */
package tptpaxsel.PSA;

import tptpaxsel.Obligations;

/**
 * Each Premise Selection Algorithm must implement this interface.
 * 
 * @author Daniel Kühlwein
 *
 */
public interface PremiseSelectionAlgorithm {
	/**
	 * Changes the checkSettings of all obligations in obligations.
	 * 
	 * @param obligations
	 */
	public void changeCheckSettings(Obligations obligations);
	/**
	 * Provides information about the kind of PSA used.
	 */
	public void logInfo();
}
