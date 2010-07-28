package naproche.neural;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Vector;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * A class for all the proof obligations of an example.
 * 
 * @author rekzah
 *
 */
public class Obligations {
	/**
	 * Weight used in calculating the final relevance of a premise.
	 */
	double weightAPRILS;
	/**
	 * Weight used for edges in the obligations graph.
	 */
	double weightObligationGraph;
	/**
	 * Weight used in the used graph.
	 */
	double weightUsedGraph;
	/**
	 * The Naproche proof graph of the example.
	 */
	public DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph;
	/**
	 * The maximal time to be used per obligation. 
	 */
	public int maxTime;
	/**
	 * Counts how many obligations were proved.
	 */
	public int theoremCounter;
	/**
	 * Counts how many obligations exist.
	 */
	public int obligationCounter;
	/**
	 * Gives a measure on how good a checkObligation call was.
	 */
	double checkScore;	
	
	/**
	 * The obligations in the order in which they were created.
	 */
	private Vector<Obligation> obligations = new Vector<Obligation>();
	
	/**
	 * Creates a new obligations class.
	 * 
	 * @param obligationOrder	A String[] which contains the filenames of the proof obligations in the order in which they are created.
	 * @param location	The location of the files in the examples folder.
	 */
	public Obligations(String[] obligationOrder, String location) {
		// Set default values for the weights and proof checker settings
		weightAPRILS = 1;
		weightObligationGraph = 1;
		weightUsedGraph =1;
		// maxTime in milliseconds
		maxTime = 5 * 1000;
		obligationCounter = obligationOrder.length;
		theoremCounter = 0;
		checkScore = 0;
		// Read the problem files and create the obligations.
		// We expect Aprils relevance information in the files.
		for (int i = 0; i < obligationOrder.length; i++) {
			try {
				obligations.add(new Obligation("/home/rekzah/workspace/naproche/examples/"+location+"/"+obligationOrder[i]));
			} catch (IOException e) {
				System.out.println("File examples/"+location+"/"+obligationOrder[i]+" does not exist" );				
			}
		}		
	}

	/**
	 * Tries to discharge the obligations.
	 */
	public void checkObligations() {
		createObligationEdges();
		String fileName = new String();
		String line;
		DefaultWeightedEdge edge = new DefaultWeightedEdge();
		String conjecture;
		String axiom;
		int usedAxiomCounter ;
		boolean checkResult;
		double premiseRatio;
		double timePerObligation;
		double systemTime;
		checkScore = 0;
		
		for (Obligation obligation : obligations) {
			usedAxiomCounter = 0;
			checkResult = false;
			timePerObligation = maxTime;
			// Output
			System.out.print(obligation.file+" Result:");
			
			systemTime = System.currentTimeMillis();
			// ----------------- Preparations ---------------------------
			conjecture = obligation.conjecture.name;
			obligation.setNaprocheScore(graph);
			obligation.setFinalRelevance(weightAPRILS);
			Collections.sort(obligation.premises, new AxiomFinalComparator());
			try {
				fileName = createTPTPProblem(obligation);
			} catch (IOException e) {
				System.out.println("Could not create TPTP Problem File");
				e.printStackTrace();
			}
			
			// --------------- Start Running EP ------------------------						
			try {
				// Build the process
				Process runEP = new ProcessBuilder(
						"/home/rekzah/Programming/Naproche/Naproche-Due/naproche_core/www/cgi-bin/TPTP/Systems/EP---1.0/eproof",
						"-xAuto",
						"-tAuto",
						"--cpu-limit="+maxTime,
						"--proof-time-unlimited",
						"--memory-limit=Auto",
						"--tstp-in",
						"--tstp-out",
						fileName).start();
				// Set up the in and output streams
				InputStream is = runEP.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				FileWriter fstream = new FileWriter(fileName+".ep");
				BufferedWriter out = new BufferedWriter(fstream);
				// Parse the stream for results
				line = br.readLine();
				// If we have a proof we can analyze it				
				if ( line.equals("# Problem is unsatisfiable (or provable), constructing proof object") ) {
					checkResult = true;
					theoremCounter++;
					out.write(line);
		    		out.write("\r\n");
		    		// -------------------- Add the used graph ------------------------
		    		// formulaCounter counts the formulas. 
	    			int formulaCounter = 1;    			
		    		while ((line = br.readLine()) != null) {	    				    			   			
						out.write(line);
			    		out.write("\r\n");
			    		// Update proof graph
			    		if ( line.startsWith("fof(1, conjecture,")) {
			    			// When there are proofs by contradiction, we might not need the conjecture.
			    			formulaCounter++;
			    		}
			    		if ( line.startsWith("fof("+formulaCounter+", axiom,")) {
			    			String[] split = line.split("\'");
			    			axiom = split[split.length -2];
			    			edge = new DefaultWeightedEdge();
			    			graph.addEdge(conjecture, axiom, edge);
			    			graph.setEdgeWeight(edge, weightUsedGraph);
			    			formulaCounter++;
			    			usedAxiomCounter++;
			    		}		    		
		    	    }
					out.close();
					// Time update
					timePerObligation = timePerObligation - (System.currentTimeMillis() - systemTime);
					systemTime = System.currentTimeMillis();
				} else {
					out.close();				
				}
				//Output
				System.out.println(checkResult);
		} catch (IOException e) {
			System.out.println("Could not run prover");
			e.printStackTrace();
		}
		// ----------------------------- Calculate Score ---------------------------------
		if (checkResult == true) {
			checkScore = checkScore+50;			
		}
		if (obligation.premises.size() != 0) {
			premiseRatio = (double)usedAxiomCounter / (double)obligation.premises.size();
		} else {
			premiseRatio = 0;
		}		
		checkScore = checkScore+premiseRatio * 25;
		checkScore = checkScore+ timePerObligation / maxTime * 25;
		}
		System.out.println(theoremCounter+" out of "+obligationCounter+" obligations were discharged");
		System.out.println("The final score is "+checkScore);
	}
	

/**
 * Takes a proof obligation and creates the corresponding TPTP problem file.
 * Axioms are annotated with their relevance value.
 * 
 * @param obligation	The obligation which should be translated into a TPTP problem.
 * @return	The filename of the newly created problem file.
 * @throws IOException	If the file cannot be written.
 */
private String createTPTPProblem(Obligation obligation) throws IOException {
		String fileName = obligation.file+".naproche";
		FileWriter fstream = new FileWriter(fileName);
		BufferedWriter out = new BufferedWriter(fstream);
		BigDecimal bd;
		out.write("fof('"+obligation.conjecture.name+"'," +
				"conjecture," +
				obligation.conjecture.formula+","+
				"unknown,[relevance(1.0)]). \n");
		for (Axiom a : obligation.premises) {
			bd = new BigDecimal(a.scoreFinal);
			bd = bd.setScale(4, BigDecimal.ROUND_DOWN); 
			out.write("fof('"+a.name+"'," +
					"axiom," +
					a.formula+","+
					"unknown,[relevance("+bd+")]). \n");
		}
		out.close();
		return fileName;
	}

/**
 * Adds the obligation graph to the poof graph.
 * For each obligation, compares the premises to the premises of the previous obligation and creates edges with weight "weightAPRILS"
 * from all new premises to the obligation.
 */
private void createObligationEdges() {
		String Vertice1 = obligations.get(0).conjecture.name;
		Vector<Axiom> premises1 = obligations.get(0).premises;
		String Vertice2 = new String();
		Vector<Axiom> premises2;
		DefaultWeightedEdge edge = new DefaultWeightedEdge();		
		for (int i = 1; i < obligations.size(); i++) {
			Vertice2 = obligations.get(i).conjecture.name;
			edge = new DefaultWeightedEdge();
			graph.addEdge(Vertice2, Vertice1, edge);
			graph.setEdgeWeight(edge, weightObligationGraph);
			premises2 = obligations.get(i).premises;
			for (Axiom a : premises2) {
				if (!premises1.contains(a)) {
					edge = new DefaultWeightedEdge();
					graph.addEdge(Vertice2, a.name, edge);
					graph.setEdgeWeight(edge, weightObligationGraph);
				}
			}			
			premises1 = obligations.get(i).premises;
			Vertice1 = obligations.get(i).conjecture.name;
		}
	}
	
}
