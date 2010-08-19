package naproche.PSA;

import java.util.Vector;
import tptp_parser.SimpleTptpParserOutput;

import naproche.neural.Axiom;
import naproche.neural.Obligations;


/**
 * Premise selection algorithm that implements existential selection.
 * Does not change the prover or time settings. Should be used in addition with other PSAs
 * 
 * If the conjecture starts with an existential quantifier, do nothing,
 * else set the final score to 0 for all premises that are existential 
 *	
 * Prolog Code:
 * is_existential(type~quantifier..name~'?').
 * is_existential(type~quantifier..name~'!'..args~[_Vars,SubFormula]) :-
 *   is_existential(SubFormula).
 * is_existential(type~logical_symbol..name~'=>'..args~[_Ante,Succ]) :-
 *  is_existential(Succ).
 *  
 * @author rekzah
 *
 */
public class PSAExistential implements PremiseSelectionAlgorithm{
	String logInfo;
	
	@Override
	public void changeCheckSettings(Obligations obligations) {	
		logInfo = "% Using PSAExistential";
		for (int i=0; i < obligations.obligations.size(); i++) {
			/* Only sort if the conjecture does not start with an ex quantifier */
			if (!startsWithExistentialQuantifer(obligations.obligations.elementAt(i).conjecture)) {				
				/* Select the non-existential premises */
				for(int j=0; j < obligations.obligations.elementAt(i).premises.size(); j++){
					if (!isExistential(obligations.obligations.elementAt(i).premises.elementAt(j).formula)) {
						obligations.obligations.elementAt(i).premises.elementAt(j).scoreFinal = 0;
					}
				}	
			}						
		}		
	}
	
	/**
	 * For each obligation: If the conjecture doesn't start with an existential quantifier, delete all existential premises
	 * 
	 * @param obligations
	 */
	public void deleteExistentialPremises(Obligations obligations) {
		logInfo = "% Using PSAExistential with deletion";
		Vector<Axiom> premises;
		for (int i=0; i < obligations.obligations.size(); i++) {
			/* Only sort if the conjecture does not start with an ex quantifier */
			if (!startsWithExistentialQuantifer(obligations.obligations.elementAt(i).conjecture)) {
				premises = new Vector<Axiom>();
				/* Select the non-existential premises */
				for(int j=0; j < obligations.obligations.elementAt(i).premises.size(); j++){
					if (!isExistential(obligations.obligations.elementAt(i).premises.elementAt(j).formula)) {
						premises.add(obligations.obligations.elementAt(i).premises.elementAt(j));
					}
				}				
				/* Update the premises */
				obligations.obligations.elementAt(i).premises = premises;
			}						
		}		
	}
	
	/**
	 * Succeeds if conjecture is a first order formula that starts with an existential quantifier.
	 * 
	 * @param conjecture 
	 * @return
	 */
	private boolean startsWithExistentialQuantifer(Axiom conjecture) {				
		if(conjecture.formula.getKind().toString().equals("Quantified") ) {
			SimpleTptpParserOutput.Formula.Quantified qFormula = (SimpleTptpParserOutput.Formula.Quantified)conjecture.formula;			
			if (qFormula.getQuantifier().toString().equals("?")) {
				return true;
			}
		}		
		return false;
	}

	/**
	 * Prolog Code:
	 * is_existential(type~quantifier..name~'?').
	 * is_existential(type~quantifier..name~'!'..args~[_Vars,SubFormula]) :-
	 *   is_existential(SubFormula).
	 * is_existential(type~logical_symbol..name~'=>'..args~[_Ante,Succ]) :-
	 *  is_existential(Succ).
	 * 
	 * @param A
	 * @return
	 */
	private boolean isExistential(SimpleTptpParserOutput.Formula axiom) {
		if (axiom.getKind().toString().equals("Quantified")) {
			SimpleTptpParserOutput.Formula.Quantified qFormula = (SimpleTptpParserOutput.Formula.Quantified)axiom;			
			if (qFormula.getQuantifier().toString().equals("?")) {
				return true;
			} else if (qFormula.getQuantifier().toString().equals("!")) {
				SimpleTptpParserOutput.Formula subFormula = qFormula.getMatrix();
				return isExistential(subFormula);
			}
		} else if (axiom.getKind().toString().equals("Binary")) {
			SimpleTptpParserOutput.Formula.Binary bFormula = (SimpleTptpParserOutput.Formula.Binary)axiom;
			if (bFormula.getConnective().toString().equals("=>")) {
				return isExistential(bFormula.getRhs());
			}
		}
		
		return false;
	}

	@Override
	public void logInfo() {
		System.out.println(logInfo);
		
	}
	
	
}
