package naproche.neural;

/**
 * The Axiom class
 *  
 * Axioms are part of obligations. Most fields only make sense when seen as part of an 
 * obligation. E.g. scoreAprils denotes the score compared to the other premises of an 
 * obligation. 
 * 
 * @author rekzah
 *
 */

public class Axiom {
	/**
	 * The name of the Axiom.
	 */
	public String name;
	/**
	 * The formula as string in TPTP format.
	 */
	public String formula;
	/**
	 * The relevance score calculated by Aprils. A number between 0 (least relevant) 
	 * and 1 (most relevant)
	 */
	public double scoreAprilsDouble;
	/**
	 * According the Aprils, this number denotes how many axioms are considered 
	 * more relevant.
	 */
	public int scoreAprils;
	/**
	 * According the Naproche, this number denotes how many axioms are considered 
	 * more relevant.
	 */
	public int scoreNaproche;
	/**
	 * scoreFinal is a number between 0 and 1.
	 * This number denotes the final relevance rating of the axiom, calculated 
	 * by weighting the Naproche and APRILS relevance values. 
	 * 1 is most relevant, 0 is least.
	 */
	public double scoreFinal;
		
	public Axiom() {}
	
	/**
	 *	Creates a new axiom from a TPTP syntax string.
	 * 
	 * @param fofFormula	is a string of the form fofFormula fof('Name',type,Formula,unknown,[relevance(scoreAprilsDouble)]).
	 */
	public Axiom(String fofFormula) {
		String[] split = fofFormula.split("\'");
		name = split[1];
		if (split[2].startsWith(",conjecture,")) {
			split = split[2].split(",conjecture,");
		} else { 
			split = split[2].split(",axiom,");
		}
		split = split[1].split(",unknown,");
		formula = split[0];		
		split = split[1].split("[\\(\\)]"); 
		scoreAprilsDouble = Double.valueOf(split[1]);
	}
	
	/**
	 * Sets scoreFinal as 1/(weightAprils* scoreAprils + scoreNaproche).
	 * 
	 * @param weightAprils	A double > 0. The weight used for calculating the final score.
	 */
	public void setScoreFinal(double weightAprils) {
		scoreFinal = 1/(weightAprils* scoreAprils + scoreNaproche);				
	}
}
