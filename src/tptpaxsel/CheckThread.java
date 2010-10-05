package tptpaxsel;

import java.util.Vector;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;

import tptpaxsel.Obligation;
import tptpaxsel.Obligations;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

class CheckThread extends Thread {
	
	static ArrayList<CheckThread> Threads = new ArrayList<CheckThread>();

	static Obligation obligation;
	static ObligationStatistics stats;
	
	static String conjecture;
	
	static PrintStream outStream;
	
	static DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph;
	static double weightUsedGraph;
	
	static int proofTries=0;
	
	static boolean checkResult=false;
	
	public static int gAxiomCounter=0;
	
	public static HashMap<String,Process> provers;
	

	public static boolean checkLoop(Obligation obl, int numThreads, double weightGraph, PrintStream oStream,
									DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> gr){
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
		
		for(int i=0;i<numThreads;i++){
			Threads.add(new CheckThread(checkSettings.get(i)));
			Threads.get(i).start();
		}
		
		try{
			Threads.get(0).join();
		}
		catch(InterruptedException e){}
		return checkResult;
	}
	static public void terminateAll(CheckThread caller){
		for(CheckThread t: Threads){
			if(t==caller) continue;
			synchronized(t.lock){
				if(!t.isInterrupted())
					try{
						t.lock.wait();
					}
					catch(InterruptedException e){
						e.printStackTrace();
					}
				t.interrupt();
				if(provers.containsKey(t.getName())){
					provers.get(t.getName()).destroy();
					provers.remove(t.getName());
				}
			}
		}
	}
	
	Vector<CheckSetting> checkSettings;
	
	public Object lock = new Object();
	
	
	CheckThread(Vector<CheckSetting> checkSettings){
		this.checkSettings = checkSettings;
	}

	public void run(){
		double checkTryTime;
		boolean cResult=false;
		for (CheckSetting checkSetting : checkSettings) {
			synchronized(lock){
				lock.notifyAll();
			}
			if(isInterrupted()) break;
			
			int givenAxiomCounter;
			checkTryTime = System.currentTimeMillis();
			/* Statistics Start */
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
				synchronized(lock){
					lock.notifyAll();
				}
				if(isInterrupted()) break;
				
				Process runProver = proverProcess.start();
				provers.put(this.getName(),runProver);
				// Set up the in and output streams				
				InputStream is = runProver.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				FileWriter fstream = new FileWriter(obligation.ATPOutput);
				BufferedWriter out = new BufferedWriter(fstream);
				ATPOutputParser ATPParser = new ATPOutputParser(br, out);
				
				// Parse the stream for results		
				
				if(isInterrupted()){
					is.close();
					out.close();
					break;
				}
				
				synchronized(lock){
					lock.notifyAll();
				}
				cResult = ATPParser.parse(graph, conjecture, weightUsedGraph);
				synchronized(lock){
					lock.notifyAll();
				}
				is.close();
				out.close();
				runProver.destroy();
				provers.remove(this.getName());
				
				if(isInterrupted()) break;
				
				outStream.print(" "+cResult+"("+checkSetting.prover+")");
				/* If we found a proof, we can stop */
				if (cResult) {
					synchronized(lock){
						lock.notifyAll();
					}
					if(isInterrupted()) break;
					CheckThread.gAxiomCounter = givenAxiomCounter;
					/* Statistics Start */
					CheckThread.terminateAll(this);
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
					synchronized(lock){
						lock.notifyAll();
					}
					this.interrupt();
					break;
				} 										
			} catch (IOException e) {
				outStream.println("Could not run prover");
				e.printStackTrace();
				synchronized(lock){
					lock.notifyAll();
				}
				this.interrupt();
			}
			synchronized(lock){
				lock.notifyAll();
			}
		}
	}
}
