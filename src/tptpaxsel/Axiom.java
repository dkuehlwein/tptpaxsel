package tptpaxsel;

import tptp_parser.SimpleTptpParserOutput;
import tptp_parser.SimpleTptpParserOutput.AnnotatedFormula;
import tptp_parser.SimpleTptpParserOutput.TopLevelItem;

/**
 * The Axiom class
 *  
 * Axioms are part of obligations. Most fields only make sense when seen as part of an 
 * obligation. E.g. scoreAprils denotes the score compared to the other premises of an 
 * obligation. 
 * 
 * @author Daniel Kühlwein
 *
 */

public class Axiom {
	/**
	 * The name of the Axiom.
	 */
	public String name;
	/**
	 * The formula in TPTP format.
	 */
	public SimpleTptpParserOutput.Formula formula;
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
	 * Creates a new axiom from a TPTPParser item.
	 * 
	 * @param fofFormula
	 */
	public Axiom(TopLevelItem fofFormula) {
		name = ((SimpleTptpParserOutput.AnnotatedFormula)fofFormula).getName();
		// Remove quotations
		if (name.startsWith("\'")) {
			name = name.substring(1, name.length()-1);
		}
		    	
    	formula = ((SimpleTptpParserOutput.AnnotatedFormula)fofFormula).getFormula();
    	// Requires useful info to be [relevance(double)] !   
    	if ( ((SimpleTptpParserOutput.AnnotatedFormula)fofFormula).getAnnotations() != null ) {
    		String[] split = (((SimpleTptpParserOutput.AnnotatedFormula)fofFormula).getAnnotations().usefulInfo().toString()).split("[\\(\\)]");
    		scoreAprilsDouble = Double.valueOf(split[1]);
    	} else {
    		scoreAprilsDouble = 0;
    	}
    		
	}

	/**
	 * Basic constructor that only initializes the name and the formula.
	 * 
	 * @param axiomName
	 * @param formula
	 */
	public Axiom(String axiomName, AnnotatedFormula formula) {
		this.name = axiomName;
		this.formula = formula.getFormula(); 
	}

	/**
	 * Sets scoreFinal as 1/(weightAprils* scoreAprils + weightNaproche*scoreNaproche).
	 * 
	 * @param weightAprils		The APRILS weight used for calculating the final score.
	 * @param weightNaproche	The Naproche weight used for calculating the final score.
	 */
	public void setScoreFinal(double weightAprils, double weightNaproche) {
		if (weightAprils* scoreAprils + weightNaproche*scoreNaproche > 0) {
			scoreFinal = 1/(weightAprils* scoreAprils + weightNaproche*scoreNaproche);
		} else {
			System.err.println("Cannot set final score: Division through zero");
			scoreFinal = 0;
		}
						
	}
	
	
	@Override
	public boolean equals(Object o) {
	  if ((o == null) || (o.getClass() != Axiom.class)) {
	    return false;
	  }
	  Axiom other = (Axiom) o;
//	  return other.name.equals(this.name) && other.formula.equals(this.formula);
	  return other.name.equals(this.name);
	}
	
	@Override
	public int hashCode() {
//	  return name.hashCode() * 37 + formula.hashCode();
	  return name.hashCode();
	}

}
