package naproche.neural.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import naproche.neural.Obligations;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Class used for loading an example set of problems.
 * 
 * @author rekzah
 *
 */
public class Example {
	public String name;
	public String location;
	public Obligations obligations;
	private String[] obligationsOrderString;

	/**
	 * Creates a new example from the testName files.
	 * 
	 * @param testName must be either Landau, Euclid or Mizar	 
	 */
	public Example(String testName) {
		if (testName == "Landau") {
			name = "Landau";
			location = "landau";
			obligationsOrderString = getObligationsOrder();
			obligations = new Obligations(obligationsOrderString,location);	
			obligations.graph = readDot(location);
		}
		else if (testName == "Euclid") {
			name = "Euclid";
			location = "euclid";
			obligationsOrderString = getObligationsOrder();
			obligations = new Obligations(obligationsOrderString,location);
			obligations.graph = readDot(location);
		}
		else if (testName == "Mizar") {
			name = "Mizar";
			location = "mizar";
			obligationsOrderString = getObligationsOrder();
			obligations = new Obligations(obligationsOrderString,location);
			obligations.graph = new DefaultDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);			
		}
		else {			
			System.out.println("No test defined for "+ name);
		}
	}

	private String[] getObligationsOrder() {
		Vector<String> obOrder = new Vector<String>();
		String input;
		try {
			BufferedReader inputStream = new BufferedReader(new FileReader("examples//"+location+"//obligation.txt"));
            while ((input = inputStream.readLine()) != null) {
            	obOrder.add(input);
            }
		} catch (IOException e) {
			System.out.println("Obligation file not found");
			e.printStackTrace();
		}		
		return obOrder.toArray(new String[obOrder.size()]);
	}
	
	private static DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> readDot(String location){
		BufferedReader inputStream = null;
		String input;
        String[] splitInput;
        String Vertice1;
        String Vertice2;
        DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        
        try {
        	try {
        		inputStream = new BufferedReader(new FileReader("examples//"+location+"//Graphprs.dot"));			
        		while ((input = inputStream.readLine()) != null) {
        			splitInput = input.split("  ->  ");
        			if (splitInput.length == 2) { 
        				// We need to delete the " around the Node names
        				Vertice1 = splitInput[0].substring(1, splitInput[0].length()-1);
        				Vertice2 = splitInput[1].substring(1, splitInput[1].length()-2);
        				graph.addVertex(Vertice1);
        				graph.addVertex(Vertice2);
        				graph.addEdge(Vertice1, Vertice2);
        			}
        		}
        	} finally {
        		if (inputStream != null) {
        			inputStream.close();
        		}
        	}
        } catch (Exception e) {
        	System.out.println("Graph file not found");
			e.printStackTrace();
		}			
		return graph;
	}

	
}
