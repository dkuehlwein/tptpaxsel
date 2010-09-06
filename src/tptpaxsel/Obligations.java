package tptpaxsel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
 * @author Daniel Kühlwein
 *
 */
public class Obligations {
	/**
	 * Weight used for weighting the APRILS score in the final relevance of a premise.
	 */
	double weightAPRILS;
	/**
	 * Weight used for weighting the Naproche Score in the final relevance of a premise.
	 */
	double weightNaproche;
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
	 * Specifies the working directory
	 */
	public String workingDir;	

	/**
	 * The obligations in the order in which they were created.
	 */
	public Vector<Obligation> obligations = new Vector<Obligation>();

	/**
	 * Creates a new obligations class.
	 * 
	 * @param obligationOrder	A String[] which contains the filenames of the proof obligations in the order in which they are created.
	 * @param location	The location of the files in the examples folder.
	 */
	public Obligations(String[] obligationOrder, String location) {
		// Set default values for the weights and proof checker settings
		weightAPRILS = 1;
		weightNaproche = 1;
		weightObligationGraph = 1;
		weightUsedGraph =1;
		// maxTime in seconds
		maxTime = 5;
		obligationCounter = obligationOrder.length;
		theoremCounter = 0;
		checkScore = 0;
		// Read the problem files and create the obligations.			
		for (int i = 0; i < obligationOrder.length; i++) {	
			File file = new File(location+obligationOrder[i]);
			File fileAprils = new File(location+obligationOrder[i]);
			/* If possible, we try to get relevance information from APRILS */
			if (fileAprils.exists()) {
				try {
					obligations.add(new Obligation(fileAprils));
				} catch (IOException e) {
					System.out.println("Cannot find .aprils file. Run APRILS if you want to use the APRILS selection");
				}
			} else if (file.exists()) {
				try {
					obligations.add(new Obligation(file));
				} catch (IOException e) {
					System.out.println("Cannot find input file. Check the path variables");
				}
			}			
		}		
	}

	/**
	 * Tries to discharge the obligations.
	 */
	public void checkObligations() {
		createObligationEdges();
		String fileName = new String();
		String conjecture;
		ObligationStatistics stats;		
		int usedAxiomCounterSum = 0;
		int givenAxiomCounter;
		int givenAxiomCounterSum = 0;
		int premisesSum = 0;
		boolean checkResult;
		double premiseRatio;
		double checkTryTime;
		double systemTime;		
		double totalTime = System.currentTimeMillis();
		int maxRelevance = 0;		
		int localRelevance;		
		theoremCounter = 0;
		checkScore = 0;

		for (Obligation obligation : obligations) {			
			// Output
			System.out.print(obligation.file+" Result:");
			
			/* Preparations */
			stats = obligation.stats;			
			givenAxiomCounter = 0;			
			checkResult = false;
			systemTime = System.currentTimeMillis();
			conjecture = obligation.conjecture.name;
			obligation.setNaprocheScore(graph);
			obligation.setFinalRelevance(weightAPRILS,weightNaproche);
			Collections.sort(obligation.premises, new AxiomFinalComparator());
			
			/* 	Proof Loop	*/
			for (CheckSetting checkSetting : obligation.checkSettings) {
				/* Statistics Start */
				checkTryTime = System.currentTimeMillis();
				stats.setProofTries(stats.getProofTries()+1);
				stats.addProver(checkSetting.prover);
				/* Statistics End */
				if (checkSetting.numOfPrem == -1) {
					givenAxiomCounter = obligation.premises.size(); 
				} else {
					givenAxiomCounter = (int)Math.floor(checkSetting.numOfPrem+0.5d);
				}
				/* Select the right premises */				
				try {
					fileName = createTPTPProblem(obligation, givenAxiomCounter);
				} catch (IOException e) {
					System.out.println("Could not create TPTP Problem File");
					e.printStackTrace();
				}				
				
				/* Create the proof process */
				ProcessBuilder proverProcess;
				if (checkSetting.prover.equals("V")) {
					proverProcess = new ProcessBuilder(							
							"lib/VAMPIRE/vampire_lin32",
							"-t",Integer.toString(checkSetting.time), 							
							"--proof","tptp", 
							"--memory_limit","1500", 
							"--output_axiom_names","on",
							"--mode","casc", 
							"--input_file",fileName); 								
				} else if (checkSetting.prover.equals("E10")) { 
					proverProcess = new ProcessBuilder(
							"/home/rekzah/Programming/Naproche/Naproche-Due/naproche_core/www/cgi-bin/TPTP/Systems/EP---1.0/eproof",							
							"-xAuto",
							"-tAuto",
							"--cpu-limit="+checkSetting.time,
							"--proof-time-unlimited",
							"--memory-limit=Auto",
							"--tstp-in",
							"--tstp-out",
							fileName);						
				} else { 
					proverProcess = new ProcessBuilder(						
						"eproof",
						"-xAuto",
						"-tAuto",
						"--cpu-limit="+checkSetting.time,
						"--proof-time-unlimited",
						"--memory-limit=Auto",
						"--tstp-in",
						"--tstp-out",
						fileName);
				}
				
				/* Run the Prover */						
				try {					
					Process runProver = proverProcess.start();
					// Set up the in and output streams				
					InputStream is = runProver.getInputStream();
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					FileWriter fstream = new FileWriter(fileName+".proof");
					BufferedWriter out = new BufferedWriter(fstream);
					ATPOutputParser ATPParser = new ATPOutputParser(br, out);
					// Parse the stream for results		
					checkResult = ATPParser.parse(graph, conjecture, weightUsedGraph);
					is.close();
					out.close();
					runProver.destroy();
					System.out.print(" "+checkResult+"("+checkSetting.prover+")");
					/* If we found a proof, we can stop */
					if (checkResult) {
						/* Statistics Start */
						obligation.checkResult = checkResult;
						stats.setUsedAxiomsNumber(ATPParser.usedAxioms.size());
						stats.setUsedAxioms(ATPParser.usedAxioms);
						stats.setProofTime((System.currentTimeMillis() - checkTryTime)/1000);
						stats.setProver(checkSetting.prover);
						for (int i = 0; i < stats.getUsedAxiomsNumber(); i++) {
							Axiom axiom = ATPParser.usedAxioms.elementAt(i);
							localRelevance = obligation.premises.indexOf(axiom)+1;							
							stats.setMaxDistance(Math.max(localRelevance, stats.getMaxDistance()));							
						}
						/* Statistics End */
						break;
					} 										
				} catch (IOException e) {
					System.out.println("Could not run prover");
					e.printStackTrace();
				}
			}
			/* Statistics Start */
			stats.setResult(checkResult);
			stats.setGivenAxioms(givenAxiomCounter);
			stats.setTotalTime((System.currentTimeMillis() - systemTime)/1000);
			maxRelevance = Math.max(maxRelevance, stats.getMaxDistance());
			usedAxiomCounterSum = usedAxiomCounterSum+stats.getUsedAxiomsNumber();
			givenAxiomCounterSum = givenAxiomCounterSum + givenAxiomCounter;
			premisesSum = premisesSum + obligation.premises.size();
			/* Statistics End */
			
			// ----------------------------- Calculate Score ---------------------------------
			if (checkResult == true) {
				theoremCounter++;
				checkScore = checkScore+75;			
			}
			if (obligation.premises.size() != 0) {
				premiseRatio = (double)stats.getUsedAxiomsNumber() / stats.getGivenAxioms();
			} else {
				premiseRatio = 0;
			}
			// Score update			
			checkScore = checkScore + premiseRatio * 25;
			checkScore = checkScore - stats.getTotalTime();
			
			/* Output Start */
			stats.print();
			/* Output End */
		}
		/* Round the final score to two digits */
		BigDecimal checkScoreRounded = new BigDecimal(checkScore);
		checkScoreRounded = checkScoreRounded.setScale(2, BigDecimal.ROUND_HALF_UP);	 
		
		totalTime = (System.currentTimeMillis() - totalTime)/1000;
		System.out.println(
				"% Final PSA Stats: " +
				"Used Time: "+totalTime+
				". maxRelevance: "+maxRelevance+
				". Used Axioms Sum: "+usedAxiomCounterSum+
				". Given Axioms Sum: "+givenAxiomCounterSum+
				". Total Axioms Sum: "+premisesSum);
		System.out.println("% Final Result: "+theoremCounter+" out of "+obligationCounter+" obligations were discharged");
		System.out.println("% Final score: "+checkScoreRounded);
	}


	/**
	 * Takes a proof obligation and creates the corresponding TPTP problem file.
	 * Axioms are annotated with their relevance value.
	 * 
	 * @param obligation	The obligation which should be translated into a TPTP problem.
	 * @param numberOfPremises Only take the first numberOfPremises Premises 
	 * @return	The filename of the newly created problem file.
	 * @throws IOException	If the file cannot be written.
	 */
	private String createTPTPProblem(Obligation obligation, double numberOfPremises) throws IOException {
		int i = 0;
		String fileName = obligation.file+".naproche";
		FileWriter fstream = new FileWriter(fileName);
		BufferedWriter out = new BufferedWriter(fstream);
		BigDecimal bd;
		out.write("fof('"+obligation.conjecture.name+"'," +
				"conjecture," +
				obligation.conjecture.formula.toString().replaceAll("\n", "")+"). \n");
		for (Axiom a : obligation.premises) {
			if (i >= numberOfPremises) { break; }
			i++;
			bd = new BigDecimal(a.scoreFinal);
			bd = bd.setScale(4, BigDecimal.ROUND_DOWN); 
			out.write("fof('"+a.name+"'," +
					"axiom," +
					a.formula.toString().replaceAll("\n", "")+
//					",[relevance("+a.scoreFinal+")]" +
					"). \n");
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
		DefaultWeightedEdge edge = new DefaultWeightedEdge();
		Vector<Axiom> premises1 = obligations.get(0).premises;
		/**
		 * Initial Graph Setup
		 */
		if (!graph.containsVertex(Vertice1)) {
			graph.addVertex(Vertice1);
		}		
		for (Axiom a : premises1) {
			edge = new DefaultWeightedEdge();
			if (!graph.containsVertex(a.name)) {
				graph.addVertex(a.name);
			}
			graph.addEdge(Vertice1, a.name, edge);
			graph.setEdgeWeight(edge, weightObligationGraph);			
		}
		String Vertice2 = new String();
		Vector<Axiom> premises2;
		/**
		 * Iterative Graph Setup
		 */
		for (int i = 1; i < obligations.size(); i++) {
			Vertice2 = obligations.get(i).conjecture.name;
			if (!graph.containsVertex(Vertice2)) {
				graph.addVertex(Vertice2);
			}
			graph.addEdge(Vertice2, Vertice1, edge);
			graph.setEdgeWeight(edge, weightObligationGraph);
			premises2 = obligations.get(i).premises;
			for (Axiom a : premises2) {
				if (!premises1.contains(a)) {
					edge = new DefaultWeightedEdge();
					if (!graph.containsVertex(a.name)) {
						graph.addVertex(a.name);
					}
					graph.addEdge(Vertice2, a.name, edge);
					graph.setEdgeWeight(edge, weightObligationGraph);
				}
			}			
			premises1 = obligations.get(i).premises;
			Vertice1 = obligations.get(i).conjecture.name;
		}
	}

}
