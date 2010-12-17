package tptpaxsel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class ReadData {
	
	/**
	 * Each line in obligations.txt becomes an entry in the returned String[] 
	 * 
	 * @return The content of obligations.txt
	 */
	public static String[] getObligationsOrder(String FileName) {
		Vector<String> obOrder = new Vector<String>();
		String input;
		try {
			BufferedReader inputStream = new BufferedReader(new FileReader(FileName));
            while ((input = inputStream.readLine()) != null) {
            	obOrder.add(input);
            }
		} catch (IOException e) {
			System.err.println("Obligation file not found");
			e.printStackTrace();
		}		
		return obOrder.toArray(new String[obOrder.size()]);
	}
	
	/**
	 * Reads graph.dot in location and translates it into a DirectedWeightedGraph
	 * 
	 * @param location the location(folder) which contains graph.dot
	 * @return The Java representation of the graph.dot Graph as a DirectedWeightedGraph
	 */
	public static DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> readDot(String graphLocation){
		BufferedReader inputStream = null;
		String input;
        String[] splitInput;
        String Vertice1;
        String Vertice2;
        DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        File file = new File(graphLocation);
        
        // Make sure that the graph file exists
        if (!file.exists()) {
        	System.err.println("Could not read Graphprs.dot. Using empty graph instead.");
        	return graph;
        }
        
        try {
        	try {
        		inputStream = new BufferedReader(new FileReader(file));			
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
