package tptpaxsel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Vector;

import java.io.PrintStream;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * A class for all the proof obligations of an example.
 * 
 * @author Daniel Kühlwein
 * @author Julian Schlöder
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
	 * The location of the files in the examples folder.
	 */
	public String location;
	/**
	 * The obligations in the order in which they were created.
	 */
	public Vector<Obligation> obligations = new Vector<Obligation>();
	/**
	 * The output string
	 */
	public PrintStream outStream = System.out;	
	/**
	 * Number of threads for multithreaded discharging
	 */
	public int threads=1;

	/**
	 * Creates a new obligations class.
	 * 
	 * @param obligationOrder	A String[] which contains the filenames of the proof obligations in the order in which they are created.
	 * @param location	The location of the files in the examples folder.
	 */
	public Obligations(String[] obligationOrder, String location) {
		this(obligationOrder, location, System.out);
	}
	
	/**
	 * Creates a new obligations class.
	 * 
	 * @param obligationOrder A String[] which contains the filenames of the proof obligations in the order in which they are created.
	 * @param location 	The location of the files in the examples folder.
	 * @param outStream	The output is written onto this stream
	 */
	public Obligations(String[] obligationOrder, String location, PrintStream outStream) {
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
		this.location = location;
		// Read the problem files and create the obligations.			
		for (int i = 0; i < obligationOrder.length; i++) {	
			File file = new File(location+obligationOrder[i]);
			File fileAprils = new File(location+obligationOrder[i]+".aprils");
			/* If possible, we try to get relevance information from APRILS */
			if (fileAprils.exists()) {
				try {
					obligations.add(new Obligation(fileAprils,outStream));
				} catch (IOException e) {
					outStream.println("Cannot find .aprils file. Run APRILS if you want to use the APRILS selection");
				}
			} else if (file.exists()) {
				try {
					obligations.add(new Obligation(file,outStream));
				} catch (IOException e) {
					outStream.println("Cannot find input file. Check the path variables");
				}
			}			
		}		
	}
	
	public ObligationStatistics checkSingleObligation(String filename, String outputType) {
		createObligationEdges();		
		ObligationStatistics stats;		
		boolean checkResult;
		
		Obligation obligation=null;
		for (Obligation ob : obligations)
			if (ob.problemFile.toString().equals(filename))
				obligation = ob;
		
		if (obligation==null){
			outStream.print("Obligation "+filename+" not found.");
			return null;
		}

		// Output
		outStream.print(obligation.problemFile+" Result:");
		
		/* Preparations */
		stats = obligation.stats;			
		obligation.setNaprocheScore(graph);
		obligation.setFinalRelevance(weightAPRILS,weightNaproche);
		Collections.sort(obligation.premises, new AxiomFinalComparator());
		
		checkResult = CheckThread.checkLoop(obligation, threads, weightUsedGraph,outStream,graph);
		
		// Basic Stats
		stats.setResult(checkResult);
		
		/* Output*/
		if (outputType.equals("human"))
			stats.print();
		else if (outputType.equals("both")){
			stats.print();
			stats.printMachine(outStream);
		}
		else stats.printMachine(outStream);
		return stats;
	}

	/**
	 * Tries to discharge the obligations.
	 */
	public void checkObligations() {
		this.checkObligations("human");
	}
	
	/**
	 * Tries to discharge the obligations.
	 * 
	 * @param outputType can be "human", "machine" or "both"
	 */
	public void checkObligations(String outputType) {
		createObligationEdges();		
		ObligationStatistics stats;		
		int usedAxiomCounterSum = 0;
		int givenAxiomCounterSum = 0;
		int premisesSum = 0;
		boolean checkResult;
		double premiseRatio;
		double systemTime;		
		double totalTime = System.currentTimeMillis();
		int maxRelevance = 0;		
		theoremCounter = 0;
		checkScore = 0;

		for (Obligation obligation : obligations) {			
			// Output
			outStream.print(obligation.problemFile+" Result:");
			
			/* Preparations */
			stats = obligation.stats;			
			checkResult = false;
			systemTime = System.currentTimeMillis();
			obligation.setNaprocheScore(graph);
			obligation.setFinalRelevance(weightAPRILS,weightNaproche);
			Collections.sort(obligation.premises, new AxiomFinalComparator());
			
			checkResult = CheckThread.checkLoop(obligation, threads, weightUsedGraph,outStream,graph);

			/* Statistics Start */
			stats.setResult(checkResult);
			stats.setGivenAxioms(CheckThread.gAxiomCounter);
			stats.setTotalTime((System.currentTimeMillis() - systemTime)/1000);
			maxRelevance = Math.max(maxRelevance, stats.getMaxDistance());
			usedAxiomCounterSum = usedAxiomCounterSum+stats.getUsedAxiomsNumber();
			givenAxiomCounterSum = givenAxiomCounterSum + CheckThread.gAxiomCounter;
			premisesSum = premisesSum + obligation.premises.size();
			/* Statistics End */
			
			// ----------------------------- Calculate Score ---------------------------------
			if (checkResult == true) {
				theoremCounter++;
				checkScore = checkScore+75;			
			}
			if (stats.getGivenAxioms()==0)
					stats.setGivenAxioms( obligation.premises.size());
			if (obligation.premises.size() != 0) {
				premiseRatio = (double)stats.getUsedAxiomsNumber() / stats.getGivenAxioms();
			} else {
				premiseRatio = 0;
			}
			// Score update			
			checkScore = checkScore + premiseRatio * 25;
			checkScore = checkScore - stats.getTotalTime();
			
			/* Output*/
			if (outputType.equals("human"))
				stats.print();
			else if (outputType.equals("both")){
				stats.print();
				stats.printMachine(outStream);
			}
			else stats.printMachine(outStream);
			/* Output End */
		}
		/* Round the final score to two digits */
		BigDecimal checkScoreRounded = new BigDecimal(checkScore);
		checkScoreRounded = checkScoreRounded.setScale(2, BigDecimal.ROUND_HALF_UP);	 
		
		totalTime = (System.currentTimeMillis() - totalTime)/1000;
		outStream.println(
				"% Final PSA Stats: " +
				"Used Time: "+totalTime+
				". maxRelevance: "+maxRelevance+
				". Used Axioms Sum: "+usedAxiomCounterSum+
				". Given Axioms Sum: "+givenAxiomCounterSum+
				". Total Axioms Sum: "+premisesSum);
		outStream.println("% Final Result: "+theoremCounter+" out of "+obligationCounter+" obligations were discharged");
		outStream.println("% Final score: "+checkScoreRounded);
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
	public static void createTPTPProblem(Obligation obligation, double numberOfPremises) throws IOException {
		int i = 0;		
		FileWriter fstream = new FileWriter(obligation.ATPInput);
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
		return;
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

	public void runAprils() {
		for(Obligation obligation : obligations)
			obligation.runAprils();
	}

}
