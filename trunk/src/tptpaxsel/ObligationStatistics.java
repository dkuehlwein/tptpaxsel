package tptpaxsel;

import java.io.PrintStream;
import java.util.Vector;

/**
 * Contains all the info we would like to know about an obligation.
 * 
 * @author Daniel Kühlwein
 *
 */
public class ObligationStatistics {
	/**
	 * True if we found a proof, false if not.
	 */
	private boolean result;
	/**
	 * The time the ATP used to find a proof. If there were several runs, this only 
	 * denotes the time needed for the successful run.
	 */
	private double proofTime;
	/**
	 * The total Time needed for the obligation.
	 */
	private double totalTime;
	/**
	 * The number of axioms available for this problem.
	 */
	private int totalAxioms;	
	/**
	 * The number of axioms given to the ATP in the successful proof. If the ATP could 
	 * not find a proof, then this is the number of given axioms in the last proof attempt. 
	 */
	private int givenAxioms;
	/**
	 * If the ATP found a proof, this is the highest index of the used axioms, 
	 * else maxDistanceAxiom = 0. 
	 */
	private int maxDistance;
	/**
	 * Contains the names of all the axioms used in the proof. Is empty if there is no proof. 
	 */
	private Vector<Axiom> usedAxioms;
	/**
	 * If the ATP found a proof, this is the total number of axioms used in the proof, 
	 * else it is 0.
	 */
	private int usedAxiomsNumber;	
	/**
	 * The name of the prover that found the proof. Null, if we didn't find a proof.
	 */
	private String prover;
	/**
	 * Contains the names of all provers that were used in the proof attempt.  
	 */
	private String[] provers;
	/**
	 * Counts the number of proof tries.
	 * E.g. PSAPremiseGrowth uses several proof tries.
	 */
	private int proofTries;
	/**
	 * True if the conjecture was not used in the proof
	 */
	private boolean inconsistencyWarning;
	
	/* Constructors */
	public ObligationStatistics() {		
		result = false;		
		inconsistencyWarning = false;
		maxDistance = 0;
		proofTime = 0;
		proofTries = 0;		
	}
	
	/* Getters and Setters */
	public boolean getResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public double getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(double totalTime) {
		this.totalTime = totalTime;
	}
	public double getProofTime() {
		return proofTime;
	}
	public void setProofTime(double proofTime) {
		this.proofTime = proofTime;
	}
	public int getTotalAxioms() {
		return totalAxioms;
	}
	public void setTotalAxioms(int totalAxioms) {
		this.totalAxioms = totalAxioms;
	}
	public int getGivenAxioms() {
		return givenAxioms;
	}
	public void setGivenAxioms(int givenAxioms) {
		this.givenAxioms = givenAxioms;
	}
	public int getMaxDistance() {
		return maxDistance;
	}
	public void setMaxDistance(int maxDistance) {
		this.maxDistance = maxDistance;
	}
	public Vector<Axiom> getUsedAxioms() {
		return usedAxioms;
	}
	public void setUsedAxioms(Vector<Axiom> usedAxioms) {
		this.usedAxioms = usedAxioms;
	}
	public int getUsedAxiomsNumber() {
		return usedAxiomsNumber;
	}
	public void setUsedAxiomsNumber(int usedAxiomsNumber) {
		this.usedAxiomsNumber = usedAxiomsNumber;
	}
	public String getProver() {
		return prover;
	}
	public void setProver(String prover) {
		this.prover = prover;
	}
	public String[] getProvers() {
		return provers;
	}
	public void setProvers(String[] provers) {
		this.provers = provers;
	}
	public int getProofTries() {
		return proofTries;
	}
	public void setProofTries(int proofTries) {
		this.proofTries = proofTries;
	}
	public boolean isInconsistencyWarning() {
		return inconsistencyWarning;
	}
	public void setInconsistencyWarning(boolean inconsistencyWarning) {
		this.inconsistencyWarning = inconsistencyWarning;
	}

	/**
	 * If prover2 is not an element of provers, adds prover2 to the provers array
	 * 
	 * @param prover2
	 */
	public void addProver(String prover2) {
		if (provers == null) {
			provers = new String[] {prover2};
			return;
		}
		
		for (String s : provers) {
			if (s.equals(prover2)) {
				return;
			}
		}
		String[] newProvers = new String[provers.length+1];
		System.arraycopy(provers,0,newProvers,0,provers.length);
		newProvers[provers.length]=prover2;
		provers = newProvers;
		
		return;
	}	

	/**
	 * Display the information in human readable format on StdOut.
	 */
	public void print() {
		String sprovers = new String();
		for (String s : provers) {
			sprovers = s+"\t"+sprovers;
		}
		
		System.out.println();
		System.out.println(
				"% PSA Stats: \n% " +
				"Result: "+result+"\t"+
				"InconsistencyWarning: "+inconsistencyWarning+"\t"+
				"Prover: "+prover+"\n% "+
				"TotalAxioms: "+totalAxioms+"\t"+
				"GivenAxioms: "+givenAxioms+"\t"+
				"UsedAxioms: "+usedAxiomsNumber+"\t"+
				"maxDistance: "+maxDistance+"\n% "+
				"ProofTime: "+proofTime+"\t"+
				"TotalTime: "+totalTime+"\t"+
				"ProofTries: "+proofTries+"\n% "+
				"Provers: "+sprovers+"\n"
		);		
	}
	
	/**
	 * Print the header of a machine readable output.
	 * 
	 * @param out
	 */
	public void printHeader(PrintStream out) {
		out.println(
				"Result\t" +
				"InconsistencyWarning\t"+
				"Prover\t"+
				"TotalAxioms\t"+
				"GivenAxioms\t"+
				"UsedAxioms\t"+
				"maxDistance\t"+
				"ProofTime\t"+
				"TotalTime\t"+
				"ProofTries\t"+
				"Provers"
		);		
	}
	
	/**
	 * Print the statistics in machine readable output that can be copy pasted in 
	 * excel-like programs.
	 * 
	 * @param out
	 */
	public void printMachine(PrintStream out) {
		String sprovers = new String();
		for (String s : provers) {
			sprovers = s+"\t"+sprovers;
		}		
		
		out.println(
				result+"\t"+
				inconsistencyWarning+"\t"+
				prover+"\t"+
				totalAxioms+"\t"+
				givenAxioms+"\t"+
				usedAxiomsNumber+"\t"+
				maxDistance+"\t"+
				proofTime+"\t"+
				totalTime+"\t"+
				proofTries+"\t"+
				sprovers
		);	
		
	}
}
