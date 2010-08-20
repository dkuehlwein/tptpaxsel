package naproche.neural;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Vector;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import tptp_parser.SimpleTptpParserOutput;
import tptp_parser.TptpLexer;
import tptp_parser.TptpParser;

/**
 * A obligation consists of the conjecture and its premises.
 * We want to prove that the premises imply the conjecture.
 * 
 * @author Daniel Kühlwein
 *
 */
public class Obligation {
	/**
	 * The location of the original problem file in TPTP format.
	 */
	public File file;
	/**
	 * The conjecture of the obligation.
	 */
	public Axiom conjecture;
	/**
	 * The premises of the conjecture.
	 */
	public Vector<Axiom> premises; 
	/**
	 * When the Obligation gets discharged, these settings will be used. 
	 * Contains the number of ATP tries as well as the time, prover and premise settings for each try. 
	 */
	public Vector<CheckSetting> checkSettings;
	/**
	 * If the obligation was checked successfully, checkResult = true
	 */
	public boolean checkResult;
	
	/**
	 * Creates a new obligation from a problem file.
	 * 
	 * @param fileLocation 	The location of the file. 
	 * @throws IOException
	 */
	public Obligation(File file) throws IOException {
		this.file = file;
		premises = new Vector<Axiom>();
		checkSettings = new Vector<CheckSetting>();
		checkSettings.add(new CheckSetting());
		checkResult = false;
		
		/* Open input stream */
		DataInputStream inputStream;
		try {
			inputStream = new DataInputStream(new FileInputStream(file));
		TptpLexer lexer = new TptpLexer(inputStream);
		TptpParser parser = new TptpParser(lexer);
		SimpleTptpParserOutput parserOutput = new SimpleTptpParserOutput();
		String role;
		try {
		    for (SimpleTptpParserOutput.TopLevelItem formula = 
		           (SimpleTptpParserOutput.TopLevelItem)parser.topLevelItem(parserOutput);
		         formula != null;
		         formula = (SimpleTptpParserOutput.TopLevelItem)parser.topLevelItem(parserOutput))
		    {
		    
		    role = ((SimpleTptpParserOutput.AnnotatedFormula)formula).getRole().toString(); 
		    if ( role.equalsIgnoreCase("conjecture")) {
		    	conjecture = new Axiom(formula);
		    } else {
		    	premises.add(new Axiom(formula));
		    }
	 
		    };
		    setAPRILSScore();
		}
		// General ANTLR exception, provides diagnostics.
		catch (antlr.ANTLRException e) {
		    System.err.println("Syntax error in " + "test" + ": " + e);
		}
		} catch (FileNotFoundException e1) {
			System.err.println("Trying to create a proof obligation from nonexisting file!");
			e1.printStackTrace();
		}

	}

	/**
	 * Sets the scoreFinal value of all axioms.
	 * 
	 * @param weight A double > 0. Used for the weighting the two different relevance scores.
	 * 
	 */
	public void setFinalRelevance(double weightAPRILS, double weightNaproche) {
		for (Axiom a : premises) {
			a.setScoreFinal(weightAPRILS, weightNaproche);
		}
	}
	
	/**
	 * The axioms get sorted by their APRILS score (the double between 0 and 1)
	 * The axiom with the highest score get an APRILS score (the integer) of 1, 
	 * the second highest of 2 etc. 
	 */
	private void setAPRILSScore() {
		conjecture.scoreAprils = 0;
		Collections.sort(premises, new AxiomAPRILSComparator());
		for (int i = 0; i < premises.size(); i++) {
			premises.elementAt(i).scoreAprils = i+1;
		}
	}

	// Declaration for setNaprocheScore
	private Vector<String> foundNodes;
	private int depth;
	private Vector<String> newNodes;
	private Vector<Axiom> missingPremises;
	
	/**
	 * Calculates the geodesic distance between the conjecture and the premises.
	 * 
	 * @param graph 	The current proof graph.
	 */
	public void setNaprocheScore(DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph) {
		// initialize the values
		depth = 1;
		foundNodes = new Vector<String>();
		foundNodes.add(conjecture.name);
		newNodes = new Vector<String>(foundNodes);
		missingPremises = new Vector<Axiom>(premises);
		
		while (!missingPremises.isEmpty()) {
			newNodes = getNewNodes(graph);
			if (newNodes.size() == 0) {
				System.out.println("No new nodes for "+ conjecture.name +" at depth" + depth);
				System.out.println("Missing Premises are:");
				for (Axiom a : missingPremises) {
					System.out.println(a.name);
				}
				break;
			}
			depth++;
		}		
	}

		// Finds all nodes that are connected to newNodes
	private Vector<String> getNewNodes(DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph) {
		Vector<String> newNodesTmp = new Vector<String>();
		for (String node : newNodes) {
			for (String succ : Graphs.successorListOf(graph, node)) {
				if (!foundNodes.contains(succ)) {
					foundNodes.add(succ);
					newNodesTmp.add(succ);
					removeNodeFromPremises(succ);
				}
			}
		}
		return newNodesTmp;
	}

	private void removeNodeFromPremises(String newNode) {
		int size = missingPremises.size();
		for (int j = 0; j < size; j++) {
			Axiom a = missingPremises.get(j);
			if (a.name.equals(newNode)) {
				missingPremises.remove(a);	
				premises.get(premises.indexOf(a)).scoreNaproche = depth;
				// Each axiom can be at most once in premises, therefore we can break
				// after finding it
				break;
			}
		}
		
	}
}
