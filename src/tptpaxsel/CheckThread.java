package tptpaxsel;

import java.util.Vector;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;

import tptpaxsel.Obligation;
import tptpaxsel.Obligations;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Thread-Class and Interface for multithreaded discharging of CheckSettings
 * Concept: A set of static Methods and Variables controls Thread-Objects.
 * 
 * Please note: There is no kind of synchronization or locking between the Threads;
 * they just run rampant until one of them decides he is done, kills off all the others
 * and just hopes every exception this may cause is caught.
 * Since termination of single Threads happens at chosen keypoints (see below) the only
 * dangerous act should be the killing of the external prover process. At the very least, this
 * causes an IOException in ATPOutputParser which is caught silently(!), so look there first if there
 * are any unexpected results...
 * @author Julian Schlöder
 *
 */
class CheckThread extends Thread {
	
	/**
	 * All current Threads are held here.
	 */
	static ArrayList<CheckThread> Threads = new ArrayList<CheckThread>();

	/**
	 * The current Obligation
	 */
	static Obligation obligation;
	
	/**
	 * The current ObligationStatistics
	 */
	static ObligationStatistics stats;
	
	/**
	 * The to-be-proven conjecture
	 */
	static String conjecture;
	
	/**
	 * outStream, carried over from Obligations.java (and AtpApi.java)
	 */
	static PrintStream outStream;
	
	/**
	 * naproche-graph and associated weight, carried over from Obligations.java
	 */
	static DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph;
	static double weightUsedGraph;
	
	/**
	 * Total number of tries until completion, relevant for statistics.
	 */
	static int proofTries=0;
	
	/**
	 * Result, starts at not-proven(i.e. false)
	 */
	static boolean checkResult=false;
	
	/**
	 * Number of given Axioms in the first successful run of the prover, is initalized at 0
	 * and is set by a Thread when the provers succeeds. May then be read external (e.g. Obligations.java)
	 */
	public static int gAxiomCounter=0;
	
	/**
	 * Whenever a external prover process (i.e. vampire or E) is started, its Process-Handler is
	 * saved here, so it can be terminated by other Threads (specifically by terminateAll).
	 */
	public static HashMap<String,Process> provers;
	

	/**
	 * Tries to discharge Obligation obl, using numThreads Threads, printing output to oStream
	 * and using graph gr with weight weightGraph.
	 * @param obl
	 * @param numThreads
	 * @param weightGraph
	 * @param oStream
	 * @param gr
	 * @return
	 */
	public static boolean checkLoop(Obligation obl, int numThreads, double weightGraph, PrintStream oStream,
									DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> gr){
		if (numThreads<1) return false; //no Threads cannot proof anything ;)
		
		/*
		 * Everything is initialized and default values are set.
		 * This is mportant, because otherwise settings/values from a previous obligation may be carried over.
		 */
		obligation = obl;
		stats = obligation.stats;
		conjecture = obligation.conjecture.name;
		outStream = oStream;
		graph = gr;
		weightUsedGraph = weightGraph;
		proofTries = 0;
		checkResult = false;
		
		gAxiomCounter = 0;
		
		Threads = new ArrayList<CheckThread>();
		
		provers = new HashMap<String,Process>();
		
		ArrayList<Vector<CheckSetting>> checkSettings = new ArrayList<Vector<CheckSetting>>();
		for(int i=0;i<numThreads;i++){
			checkSettings.add(new Vector<CheckSetting>());
		}
		
		/*
		 * All CheckSettings are distributed over the Vectors for each Thread
		 */
		int current = 0;
		for(CheckSetting checkSetting: obligation.checkSettings){
			if(current<numThreads-1){
				checkSettings.get(current).add(checkSetting);
				current++;
			}
			else if (current==numThreads-1){
				checkSettings.get(current).add(checkSetting);
				current=0;
			}
		}
		
		/*
		 * The Threads are created with one CheckSetting Vector each and subsequently started.
		 */
		for(int i=0;i<numThreads;i++){
			Threads.add(new CheckThread(checkSettings.get(i)));
			Threads.get(i).start();
		}
		
		/*
		 * Wait for the first thread to finish. 
		 * (It doesn't matter for which thread we wait, as all Threads are terminated once the first
		 * proof is found. But there is always a first thread so this is most simple).
		 */
		try{
			Threads.get(0).join();
		}
		catch(InterruptedException e){}
		return checkResult;
	}
	
	/**
	 * Utility method for terminating all Threads. Is always called by a thread, the caller.
	 * The caller isn't terminated in this method, but it is only called shortly before a
	 * caller is about to terminate itself.
	 * @param caller
	 */
	static public void terminateAll(CheckThread caller){
		for(CheckThread t: Threads){ //iterate all threads
			if(t==caller) continue; //on caller: do nothing
			if(!t.isInterrupted()){ //thread is not already terminated
				t.interrupt(); // interrupt thread to cause termination.
				if(provers.containsKey(t.getName())){ //if t has an active prover process, kill it.
					provers.get(t.getName()).destroy();
					provers.remove(t.getName());
				}
			}
		}
	}
	
	/**
	 * individual checkSettings-Vector
	 */
	Vector<CheckSetting> checkSettings;
	
	/**
	 * Constructor. Sets checkSettings.
	 * @param checkSettings
	 */
	CheckThread(Vector<CheckSetting> checkSettings){
		this.checkSettings = checkSettings;
	}

	/**
	 * Thread Execution. Thread terminates once run finishes.
	 */
	public void run(){
		/*
		 * Variables for statistics.
		 */
		double checkTryTime;
		boolean cResult=false;
		int givenAxiomCounter;
		
		/*
		 * Start Proof Loop
		 * When this Loop is through, run() finishes. So "break" serves as termination of the Thread.
		 */
		for (CheckSetting checkSetting : checkSettings) {
			if(isInterrupted()) break; //termination
			
			checkTryTime = System.currentTimeMillis();
			stats.setProofTries(stats.getProofTries()+1); //Note that stats is a static/global Object
			stats.addProver(checkSetting.prover);
			
			
			if (checkSetting.numOfPrem == -1) {
				givenAxiomCounter = obligation.premises.size(); 
			} else {
				givenAxiomCounter = (int)Math.floor(checkSetting.numOfPrem+0.5d);
			}
			
			/*
			 * Premise Selection
			 */				
			try {					
				Obligations.createTPTPProblem(obligation, givenAxiomCounter);
			} catch (IOException e) {
				outStream.println("Could not create TPTP Problem File");
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
						"--input_file",
						obligation.ATPInput.toString()); 								
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
						obligation.ATPInput.toString());						
			} else { 
				proverProcess = new ProcessBuilder(						
					"/usr/local/bin/eproof",
					"-xAuto",
					"-tAuto",
					"--cpu-limit="+checkSetting.time,
					"--proof-time-unlimited",
					"--memory-limit=Auto",
					"--tstp-in",
					"--tstp-out",
					obligation.ATPInput.toString());
			}
			
			/* Run the Prover */						
			try {					
				if(isInterrupted()) break; //termination
				
				Process runProver = proverProcess.start();
				provers.put(this.getName(),runProver);
				// Set up the in and output streams				
				InputStream is = runProver.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				FileWriter fstream = new FileWriter(obligation.ATPOutput);
				ATPOutputParser ATPParser = new ATPOutputParser(br);
				
				// Parse the stream for results		
				
				if(isInterrupted()){ //cleanup when terminated
					is.close();
					break;
				}
				
				cResult = ATPParser.parse(graph, conjecture, weightUsedGraph);
				
				is.close();
				runProver.destroy();
				provers.remove(this.getName());
				
				if(isInterrupted()) break; //termination, has to be after the lines doing cleanup above.
				
				outStream.print(" "+cResult+"("+checkSetting.prover+")");
				/* If we found a proof, we can stop */
				if (cResult) {
					if(isInterrupted()) break; //unless we already found a proof
					
					/* we're finished with this obligation, so put down all Threads
					 */
					CheckThread.terminateAll(this);
					
					/* and THEN write output */
					FileWriter fileWriter = new FileWriter(obligation.ATPOutput);
					BufferedWriter bWriter = new BufferedWriter(fstream);
					bWriter.write(ATPParser.out);
					bWriter.close();
					fileWriter.close();
					
					CheckThread.gAxiomCounter = givenAxiomCounter;
					/* Statistics Start */
					checkResult = cResult;
					obligation.checkResult = checkResult;
					stats.setUsedAxiomsNumber(ATPParser.usedAxioms.size());
					stats.setUsedAxioms(ATPParser.usedAxioms);
					stats.setProofTime((System.currentTimeMillis() - checkTryTime)/1000);
					stats.setProver(checkSetting.prover);
					stats.setInconsistencyWarning(ATPParser.inconsistencyWarning);
					obligation.inconsistencyWarning = ATPParser.inconsistencyWarning;
					for (int i = 0; i < stats.getUsedAxiomsNumber(); i++) {
						Axiom axiom = ATPParser.usedAxioms.elementAt(i);
						int localRelevance = obligation.premises.indexOf(axiom)+1;							
						stats.setMaxDistance(Math.max(localRelevance, stats.getMaxDistance()));							
					}
					/* Statistics End */
					this.interrupt(); //terminate the successful Thread.
					break;
				} 										
			} catch (IOException e) {
				outStream.println("Could not run prover");
				e.printStackTrace();
				this.interrupt(); //somethings broken, terminate.
			}
		}
	}
}
