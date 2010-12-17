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

import tptpaxsel.Obligations;
import tptpaxsel.ReadData;

/**
 * Class used for loading an example set of problems.
 * 
 * @author Daniel Kühlwein
 *
 */
public class Example {
	public String name;
	public String location;
	public String locationOb;
	public String locationGraph;
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
				location = workingDir+"/examples/landau";				
				locationGraph = workingDir+"/examples/landau/Graphprs.dot";
				obligationsOrderString = getObligationsOrder(location);
				obligations = new Obligations(obligationsOrderString);	
				obligations.graph = ReadData.readDot(locationGraph);
			}
			else if (testName == "Euclid") {
				name = "Euclid";
				location = workingDir+"/examples/euclid";				
				locationGraph = workingDir+"/examples/euclid/Graphprs.dot";
				obligationsOrderString = getObligationsOrder(location);
				obligations = new Obligations(obligationsOrderString);
				obligations.graph = ReadData.readDot(locationGraph);
			}
			else if (testName == "MizarHard") {
				name = "MizarHard";
				location = workingDir+"/examples/mizarHard";				
				locationGraph = workingDir+"/examples/mizarHard/Graphprs.dot";
				obligationsOrderString = getObligationsOrder(location);
				obligations = new Obligations(obligationsOrderString);	
				obligations.graph = ReadData.readDot(locationGraph);
			}
			else if (testName == "Mizar") {
				name = "Mizar";
				location = workingDir+"/examples/mizar";				
				locationGraph = workingDir+"/examples/mizarHard/Graphprs.dot";
				obligationsOrderString = getObligationsOrder(location);
				obligations = new Obligations(obligationsOrderString);	
				obligations.graph = ReadData.readDot(locationGraph);	
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
	public static String[] getObligationsOrder(String location) {
		Vector<String> obOrder = new Vector<String>();
		String input;
		try {
			BufferedReader inputStream = new BufferedReader(new FileReader(location+"/obligation.txt"));
			while ((input = inputStream.readLine()) != null) {
				obOrder.add(location+"/"+input);
			}
		} catch (IOException e) {
			System.err.println("Obligation file not found");
			e.printStackTrace();
		}		
		return obOrder.toArray(new String[obOrder.size()]);
	}

}