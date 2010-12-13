package tptpaxsel;

/*
 * Represents an abstraction-layer over a single directory in order to proof any tptp-files there.
 * @author Julian Schlöder
 */

import java.io.PrintStream;
import java.util.HashMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;


import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import tptpaxsel.PSA.PSAExistential;
import tptpaxsel.PSA.PSAPremiseGrowth;
import tptpaxsel.PSA.PSASimple;
import tptpaxsel.PSA.PremiseSelectionAlgorithm;

public class AtpApi {
	//initialise fields with "empty values"
	private PrintStream outStream=System.out;
	private double weightObligationEdges=0.0;
	private double weightAPRILS=0.0;
	private double weightNaproche=0.0;
	
	boolean changeExistential = false;
	boolean deleteExistential = false;

	private String technique="n/a";
	private String prover="n/a";
	private String growthMethod="n/a";
	private int growthStartValue=0;
	private int growthIncrValue=0;

	private int time=0;
	
	private int threads=1;

	private String location="";
	
	Obligations obligations;
	
/**
 * Sets basic default values for quick testing.
 */
	public void setFast(){
		outStream = System.out;
		setSimple(1.0,0.0,1.0,"V",3,2);
	}

	//Global Get/Set Methods
	//Every parameter besides outStream and location
/**
 * sets All parameters	
	 * @param weightObligationEdges
	 * @param weightAPRILS
	 * @param weightNaproche
	 * @param technique
	 * @param prover
	 * @param growthMethod
	 * @param growthStartValue
	 * @param growthIncrValue
	 * @param time
	 * @param threads
	 * @param changeExistential
	 * @param deleteExistential
 */
	public void setAll(	double weightObligationEdges,
						double weightAPRILS, double weightNaproche,
						String technique, String prover,
						String growthMethod, int growthStartValue,
						int growthIncrValue, int time, int threads,
						boolean changeExistential, boolean deleteExistential){

		this.weightObligationEdges=weightObligationEdges;
		this.weightAPRILS=weightAPRILS;
		this.weightNaproche=weightNaproche;
		this.technique=technique;
		this.prover=prover;
		this.growthMethod=growthMethod;
		this.growthStartValue=growthStartValue;
		this.growthIncrValue=growthIncrValue;
		this.time=time;
		this.time=threads;
		this.changeExistential = changeExistential;
		this.deleteExistential = deleteExistential;
	}

/**
 * Returns complete Configuration.
 * 
 * @return HashMap<String,String>
 */
	public HashMap<String,String> getConfig(){
		HashMap<String,String> retVal = new HashMap<String,String>();
		retVal.put("weightObligationEdges",Double.toString(weightObligationEdges));
		retVal.put("weightAPRILS",Double.toString(weightAPRILS));
		retVal.put("weightNaproche",Double.toString(weightNaproche));
		retVal.put("technique",technique);
		retVal.put("prover",prover);
		retVal.put("growthMethod",growthMethod);
		retVal.put("growthStartValue",Integer.toString(growthStartValue));
		retVal.put("growthIncrValue",Integer.toString(growthIncrValue));
		retVal.put("time",Integer.toString(time));
		retVal.put("threads",Integer.toString(threads));
		retVal.put("changeExistential",Boolean.toString(changeExistential));
		retVal.put("deleteExistential",Boolean.toString(deleteExistential));
		return retVal;
	}

	/** 
	 * Configures for use with Simple PSA
	 * 
	 * @param weightObligationEdges
	 * @param weightAPRILS
	 * @param weightNaproche
	 * @param prover
	 * @param time
	 * @param threads
	 */
	public void setSimple(	double weightObligationEdges,
							double weightAPRILS, double weightNaproche,
							String prover, int time, int threads){

		this.weightObligationEdges=weightObligationEdges;
		this.weightAPRILS=weightAPRILS;
		this.weightNaproche=weightNaproche;
		this.technique="simple";
		if (prover.equals("V") || prover.equals("E"))
			this.prover=prover;
		else
			outStream.println("Invalid Prover selected, must be 'V' or 'E'");
		this.time=time;
		this.threads=threads;
	}

	/**
	 * Configures for use with PremiseGrowth PSA
	 * 
	 * @param weightObligationEdges
	 * @param weightAPRILS
	 * @param weightNaproche
	 * @param prover
	 * @param growthMethod
	 * @param growthStartValue
	 * @param growthIncrValue
	 * @param time
	 * @param threads
	 */
	public void setPremiseGrowth(	double weightObligationEdges,
									double weightAPRILS, double weightNaproche,
									String prover,
									String growthMethod, int growthStartValue,
									int growthIncrValue, int time, int threads){

		this.weightObligationEdges=weightObligationEdges;
		this.weightAPRILS=weightAPRILS;
		this.weightNaproche=weightNaproche;
		this.technique="growth";
		if (prover.equals("V") || prover.equals("E") || prover.equals("VE") || prover.equals("EV"))
			this.prover=prover;
		else
			outStream.println("Invalid Prover selected, must be 'V', 'E', 'VE' or 'EV'");
		this.growthMethod=growthMethod;
		this.growthStartValue=growthStartValue;
		this.growthIncrValue=growthIncrValue;
		this.time=time;
		this.threads=threads;
	}
	
	//Get/Set Methods for fine-grained configuration
	public void setOutStream(PrintStream outStream){
		this.outStream=outStream;
	}
	public PrintStream getOutStream(){ return this.outStream;}

	public void setWeightObligationEdges(double weightObligationEdges){ this.weightObligationEdges=weightObligationEdges;}
	public double getWeightObligationEdges(){ return this.weightObligationEdges;}

	public void setWeightAPRILS(double weightAPRILS){ this.weightAPRILS=weightAPRILS;}
	public double getWeightAPRILS(){ return this.weightAPRILS;}
	
	public void setWeightNaproche(double weightNaproche){ this.weightNaproche=weightNaproche;}
	public double getWeightNaproche(){ return this.weightNaproche;}

	public void setTechnique(String technique){ this.technique=technique;}
	public String getTechnique(){ return this.technique;}

	public void setProver(String prover){ this.prover=prover;}
	public String getProver(){ return this.prover;}

	public void setGrowthMethod(String growthMethod){ this.growthMethod=growthMethod;}
	public String getGrowthMethod(){ return this.growthMethod;}

	public void setGrowthStartValue(int growthStartValue){ this.growthStartValue=growthStartValue;}
	public int getGrowthStartValue(){ return this.growthStartValue;}

	public void setGrowthIncrValue(int growthIncrValue){ this.growthIncrValue=growthIncrValue;}
	public int getGrowthIncrValue(){ return this.growthIncrValue;}

	public void setTime(int time){ this.time=time;}
	public int getTime(){ return this.time;}
	
	public void setThreads(int threads){ this.threads=threads;}
	public int getThreads(){ return this.threads;}
	
	public void setChangeExistential(boolean changeExistential){ this.changeExistential=changeExistential;}
	public boolean getChangeExistential(){ return this.changeExistential;}
	
	public void setDeleteExistential(boolean deleteExistential){ this.deleteExistential=deleteExistential;}
	public boolean getDeleteExistential(){ return this.deleteExistential;}

	public String getLocation(){ return this.location;}

	/**
	 * Simple Constructor, only sets location
	 * 
	 * @param location
	 */
	public AtpApi(String location){
		this.location=location;
		setup();
	}

	/**
	 * Complete Constructor. Sets location and all other parameters.
	 * 
	 * @param outStream
	 * @param weightObligationEdges
	 * @param weightAPRILS
	 * @param weightNaproche
	 * @param technique
	 * @param prover
	 * @param growthMethod
	 * @param growthStartValue
	 * @param growthIncrValue
	 * @param time
	 */
	public AtpApi(	PrintStream outStream, double weightObligationEdges,
					double weightAPRILS, double weightNaproche,
					String technique, String prover,
					String growthMethod, int growthStartValue,
					int growthIncrValue, int time, int threads,
					boolean changeExistential, boolean deleteExistential){
		this.outStream=outStream;
		setAll(weightObligationEdges,weightAPRILS,weightNaproche,technique,prover,growthMethod,growthStartValue,growthIncrValue,time,threads,changeExistential,deleteExistential);
		setup();
	}
/** 
 * 	Runs a single obligation in obligations, chosen by filename (in directory location)
 * 
 * @param filename
 * @return obligation Statistics
 */
	public ObligationStatistics runSingleByFilename(String filename){
		configureObligations();
		setupPSA();
		return obligations.checkSingleObligation(location+filename,"machine");
	}
/**
 * Runs a single obligation in obligations, chosen by absolute path (in the filesystem).
 * 	
 * @param path
 * @return obligation Statistics
 */
	public ObligationStatistics runSingleByAbsolutePath(String path){
		configureObligations();
		setupPSA();
		return obligations.checkSingleObligation(path,"machine");
	}
	
/**
 * Runs all obligations.
 */
	public void runAll(){
		configureObligations();
		setupPSA();
		obligations.checkObligations("machine");
		Statistics.printMachineStats(obligations, "testoutput");
		Statistics.printProcessTimeline(obligations,30,"timeline");
	};

/**
 * (re)sets all parameters in obligations.
 */
	private void configureObligations(){
		if (weightNaproche > 0)
			obligations.graph = readDot(location);
		if (weightAPRILS > 0)
			obligations.runAprils();
		obligations.weightAPRILS = this.weightAPRILS;
		obligations.weightNaproche = this.weightNaproche;
		obligations.weightObligationGraph = this.weightObligationEdges;
		obligations.outStream=this.outStream;
		obligations.threads=this.threads;
	}
	
/**
 * Constructs the absolute path to location and initialises obligations
 */
	private void setup(){
		String workingDir="user.dir";
		try{
			workingDir=new File(System.getProperty(workingDir)).getCanonicalPath();
		}
		catch (IOException e1) {
			outStream.println("Could not specify the working directory");
		}
		this.location=workingDir+this.location;
		this.obligations = new Obligations(getObligationsOrder(location),location,outStream);
	}

/**
 * Initalises the Premise Selection Algorithms and configures obligations accordingly
 */
	private void setupPSA(){
		PremiseSelectionAlgorithm psa=null;
		if (technique=="simple"){
			if (prover.equals("V") || prover.equals("E")){
				psa = new PSASimple(prover,time);
				psa.changeCheckSettings(obligations);
			}
			else{
				outStream.println("Prover "+prover+" not supported with Simple PSA.");
			}
		}
		else if (technique.equals("growth")){
			String[] provers;
			if (prover=="V" || prover=="E"){
				provers=new String[1];
				provers[0]=prover;
				psa = new PSAPremiseGrowth(time, growthMethod, growthStartValue, growthIncrValue, 0, provers);
			}
			else if (prover.equals("VE") || prover.equals("EV")){
				provers=new String[2];
				provers[0]="V";
				provers[1]="E";
				psa = new PSAPremiseGrowth(time, growthMethod, growthStartValue, growthIncrValue, 0, provers);
				psa.changeCheckSettings(obligations);
			}
			else{
				outStream.println("Prover "+prover+" not supported with Premise Growth PSA.");
			}
		}
		else{
			outStream.println("PSA "+technique+" not supported.");
		}
		if (changeExistential && deleteExistential)
			outStream.print("changeExistential and deleteExistential are mutually exclusive, set one to false.");
		
		if (changeExistential || deleteExistential){
			PSAExistential ex = new PSAExistential();
			if(changeExistential)
				ex.changeCheckSettings(obligations);
			else if(deleteExistential){
				ex.deleteExistentialPremises(obligations);
			}
		}

	}
// Everything below is copied from Example.java in tptpaxsel and slightly altered

	/**
	 * Each line in obligations.txt becomes an entry in the returned String[] 
	 * 
	 * @return The content of obligations.txt
	 */
	private String[] getObligationsOrder(String location) {
		Vector<String> obOrder = new Vector<String>();
		String input;
		try {
			BufferedReader inputStream = new BufferedReader(new FileReader(location+"obligation.txt"));
            while ((input = inputStream.readLine()) != null) {
            	obOrder.add(input);
            }
		} catch (IOException e) {
			outStream.println("Obligation file not found");
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
        
        try {
        	try {
        		inputStream = new BufferedReader(new FileReader(location+"Graphprs.dot"));			
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
