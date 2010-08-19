package naproche.neural;


/**
 * Contains the settings for an ATP call. 
 * Namely the Time, the Prover(s) and the Number of Premises
 * Default values are Time = 5, Prover = E, NumOfPrem = -1.
 * 
 * @author rekzah
 *
 */
public class CheckSetting {	
	/**
	 * E or V
	 */
	public String prover;
	/**
	 * Time in seconds
	 */
	public int time;
	/**
	 * -1 for all Premises, else equals the number of premises.
	 */
	public double numOfPrem;
	
	// Constructor
	public CheckSetting() {
		time = 5;
		prover = "E";
		numOfPrem = -1;
	}
	public CheckSetting(String prover, int time) {
		this.time = time;
		this.prover = prover;
		numOfPrem = -1;
	}
	public CheckSetting(String prover, int time, double premises) {
		this.time = time;
		this.prover = prover;
		this.numOfPrem = premises;
	}	

}
