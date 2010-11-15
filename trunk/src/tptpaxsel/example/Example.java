package tptpaxsel.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;


import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import tptpaxsel.Obligations;

/**
 * Class used for loading an example set of problems.
 * 
 * @author Daniel Kühlwein
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
		String workingDir="user.dir"; // set to current directory
		try {
			workingDir=new File(System.getProperty(workingDir)).getCanonicalPath();			 
			if (testName == "Landau") {
				name = "Landau";
				location = workingDir+"/examples/landau/";
				obligationsOrderString = getObligationsOrder();
				obligations = new Obligations(obligationsOrderString,location);	
				obligations.graph = readDot(location);
			}
			else if (testName == "Euclid") {
				name = "Euclid";
				location = workingDir+"/examples/euclid/";
				obligationsOrderString = getObligationsOrder();
				obligations = new Obligations(obligationsOrderString,location);
				obligations.graph = readDot(location);
			}
			else if (testName == "MizarHard") {
				name = "MizarHard";
				location = workingDir+"/examples/mizarHard/";
				obligationsOrderString = getObligationsOrder();
				obligations = new Obligations(obligationsOrderString,location);
				obligations.graph = readDot(location);
			}
			else if (testName == "Mizar") {
				name = "Mizar";
				location = workingDir+"/examples/mizar/";
				obligationsOrderString = getObligationsOrder();
				obligations = new Obligations(obligationsOrderString,location);
				obligations.graph = new DefaultDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);			
			}
			else {			
				System.out.println("No test defined for "+ name);
			}
		}
		catch (IOException e1) { System.out.println("Could not specify the working directory"); }

	}
	
	/**
	 * Runs Aprils on all .tptp and .input files in the examples location.
	 */
	public void runAprils() {
		File directory = new File(location);
		String[] fileNames = directory.list();
		String line;
		String workingDir="user.dir"; // set to current directory
		try {
			workingDir=new File(System.getProperty(workingDir)).getCanonicalPath();			 

			try {
				for (String fileName : fileNames) {
					if (fileName.endsWith(".tptp") || fileName.endsWith(".input")) {					
						Process aprils = new ProcessBuilder(
								workingDir+"/lib/APRILS/aprils",
								location+fileName).start();
						InputStream is = aprils.getInputStream();
						InputStreamReader isr = new InputStreamReader(is);
						BufferedReader br = new BufferedReader(isr);
						FileWriter fstream = new FileWriter(location+fileName+".aprils");
						BufferedWriter out = new BufferedWriter(fstream);

						while ((line = br.readLine()) != null) {
							if (line.startsWith("%")) {}
							else if (line.startsWith("fof")) {
								out.write("\r\n");
								out.write(line.trim());
							} else {
								out.write(line.trim());
							}        	    	
						}
						out.close();
					}
				}            
			}
			catch (IOException e) {
				System.out.println("Could not run APRILS.");            
			}
		}
		catch (IOException e1) { System.out.println("Could not specify the working directory"); }

	}
	
	/**
	 * Each line in obligations.txt becomes an entry in the returned String[] 
	 * 
	 * @return The content of obligations.txt
	 */
	private String[] getObligationsOrder() {
		Vector<String> obOrder = new Vector<String>();
		String input;
		try {
			BufferedReader inputStream = new BufferedReader(new FileReader(location+"obligation.txt"));
            while ((input = inputStream.readLine()) != null) {
            	obOrder.add(input);
            }
		} catch (IOException e) {
			System.out.println("Obligation file not found");
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
	private static DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> readDot(String location){
		BufferedReader inputStream = null;
		String input;
        String[] splitInput;
        String Vertice1;
        String Vertice2;
        DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        File file = new File(location+"Graphprs.dot");
        
        // Make sure that the graph file exists
        if (!file.exists()) {
        	System.err.println("Could not read Graphprs.dot");
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
