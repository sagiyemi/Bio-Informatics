
package fasta;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import alignments.Alignment;
import alignments.NoGapsAlignment;


public class FastaRunner extends FastaStarter{
/*inherited from fasta starter */	
//	static ArrayList<String> queries;
//	static ArrayList<String> queriesNames;
//	static ArrayList<String> targets;
//	static ArrayList<String> targetsNames;
//	//parameters
//	static int ktup, 
	
	//public static Logger logger;
	
	public FastaRunner() {
		FastaRunner.queries = new ArrayList<>();
		FastaRunner.queriesNames = new ArrayList<>();
		FastaRunner.targets = new ArrayList<>();
		FastaRunner.targetsNames = new ArrayList<>();
	}


	public static void main(String[] args) {
		//default values
		query_num= 2;
		target_num = 5;
		ktup=6;
		int band = 10;
		int topK=20;
		//program parameters
		boolean runFasta= false, fullLocal = false, chooseQuery=false, chooseTarget = false;
		
		setUpLogger();
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")) {
				switch (args[i]) {
				case "-f" : runFasta = true;  break;
				case "-l" : fullLocal = true;  break;
				case "-h" : Usage(); return;
				default : System.out.println("wrong flag"); break;
				}
			}
			try{
			 if (args[i].startsWith("query=")){
				 query_num = Integer.parseInt(args[i].split("=")[1]);
				 chooseQuery= true;
			 }
			 if (args[i].startsWith("target=")){
				 target_num = Integer.parseInt(args[i].split("=")[1]);
			 	 chooseTarget = true;
			 }
			 if (args[i].startsWith("ktup="))
				 ktup = Integer.parseInt(args[i].split("=")[1]);
			 if (args[i].startsWith("band="))
				 band = Integer.parseInt(args[i].split("=")[1]);
			 if (args[i].startsWith("topK="))
				 topK = Integer.parseInt(args[i].split("=")[1]);
			}
		
			catch(NumberFormatException e){
				System.out.println("Error! Check arguements\n" + e.getMessage());			
				return;
				}
		}
		
		FastaRunner fr = new FastaRunner();
		
		fr.readFiles();
		fr.createAlignment("resources/Score5.matrix");
		if (runFasta)
			fr.testFasta(chooseQuery, chooseTarget, topK);
		if (fullLocal){
			if (chooseQuery && chooseTarget)
			fr.runFullAlignment();
			else if (chooseQuery == true)
				fr.runLocalQuery(query_num);
			else
				fr.runAllLocalQueries();
		}
//		fr.runFullAlignment();
//	//	fr.runLocalAlignments();
//		System.out.println("Main Ended");
//		totalTime =  System.currentTimeMillis()-totalTime;
//		System.out.println("\nMain RunTime is: " + totalTime + " milliseconds");			
		
	}


	
	 protected static void setUpLogger() {
			Handler logFile = null;	
	    	try {
	    		logger = Logger.getLogger("myLog");
	    		logFile = new FileHandler("log.txt");
			 	logFile.setFormatter(new SimpleFormatter());
			 	logger.addHandler(logFile);
				logger.setLevel(Level.INFO);
		    	logger.info("Main started");
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			catch (SecurityException e) {
				System.out.println(e.getMessage());
			}
	    	catch (NullPointerException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}

		
	
	// question 1.. running local alignments in order to check running time
	public void runSWTest() {
		System.out.println("Running Local Alignments");
		FastaRunner fr = new FastaRunner();
		fr.readFiles();
		fr.runLocalAlignments();
	}
	
	private void readFiles() {
		try{
			  // Open the file that is the first 
			  // command line parameter
			  FileInputStream fstream = new FileInputStream("resources/query.fasta");
			  // Get the object of DataInputStream
			  DataInputStream in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  String strLine;
			  //Read File Line By Line
			  while ((strLine = br.readLine()) != null)   {
			  // Print the content on the console
				  if (strLine.startsWith(">")) {
					  FastaRunner.queriesNames.add(strLine.substring(1));
				  } else {
					  FastaRunner.queries.add(strLine);
				  }
			  }
		  
			  //Close the input stream
			  in.close();
		}
		catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
		}
		try{
			// Open the file that is the first 
			// command line parameter
			FileInputStream fstream = new FileInputStream("resources/virus_DB.fasta");
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				// Print the content on the console
				if (strLine.startsWith(">")) {
					FastaRunner.targetsNames.add(strLine.substring(1));
				} else {
					FastaRunner.targets.add(strLine);
				}
			}
			
			//Close the input stream
			in.close();
		}
		catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	
	public void runLocalAlignments() {
		int counter = 0;
		long totalTime = System.currentTimeMillis();
		for (String query: FastaRunner.queries) {
			for (String target : FastaRunner.targets) {
				counter++;
				long runningTime = System.currentTimeMillis(); 
				Alignment alignedStrings = new NoGapsAlignment(query,target,"resources/Score5.matrix",true);
				alignedStrings.align();
				alignedStrings.printResults();
				
				runningTime =  System.currentTimeMillis()-runningTime;
				System.out.println("RunTime is: " + runningTime + " milliseconds");				
			}
		}
		totalTime =  System.currentTimeMillis()-totalTime;
		System.out.println("TOTAL number of alignments is: " + counter);			
		System.out.println("\nTOTAL RunTime is: " + totalTime + " milliseconds");			


		
	}

//	public void trollTest() {
//		long totalTime = System.currentTimeMillis();
//		HotSpots hs = new HotSpots();
//		for (int i = 0; i < 1000000000; i++) {
////			hs.setNum(i);
//			hs.num2 = i;
//		}
//		totalTime =  System.currentTimeMillis()-totalTime;
//		System.out.println("\nTOTAL RunTime is: " + totalTime + " milliseconds");		
//	}
	
	/**
	 * 
	 * @param chooseQuery - true run fasta over the query specified by <code> query_num </code>
	 * @param chooseTarget - true run fasta over the query specified by <code> target_num </code>
	 * @param k - only up to k results will be calculated by SmithWaterMan
	 */
	public void testFasta(boolean chooseQuery, boolean chooseTarget, int k) {
		long startTime = System.currentTimeMillis();
		logger.info("running fasta\n");
		SortedSet<ChainedString> subtargets = new TreeSet<>();
		
//		query =  "AAACGTTGGGTTGGAAGTTGAAGGCCCGTCTATAGACCAAAGTCGGC";
//		target = "AAACGTTGGGTTCCTAGTTGAAGGCCCGTCTACCTACCAAAGTCGGC";
		
		HotSpots hs = new HotSpots();
		hs.hashDB();
	    logger.info("DB Hash time: " + (System.currentTimeMillis() - startTime) + " ms");
	   
	    if (chooseQuery && chooseTarget)
    		//run one query one target
    		subtargets.add(runFasta(hs));
    	else if (chooseQuery)
    		//run query against entire DB
    		for (target_num = 0; target_num<targets.size(); target_num++) {
    			subtargets.add(runFasta(hs));
			    if (subtargets.size()>k)
					subtargets.remove(subtargets.first());
    		}
    	else if (chooseTarget)
    		//run all queries against a target
    		for (query_num = 0; query_num<queries.size(); query_num++) {
    			subtargets.add(runFasta(hs));
			    if (subtargets.size()>k)
					subtargets.remove(subtargets.first());
    		}
	    else
	    	//run all queries on DB
	    	for (query_num = 0; query_num<queries.size(); query_num++) 
	    		for (target_num = 0; target_num<targets.size(); target_num++) {
	    			subtargets.add(runFasta(hs));
	    			if (subtargets.size()>k)
	    				subtargets.remove(subtargets.first());
	    		}
	    
	    for (ChainedString chainedString : subtargets)
			chainedString.align();
		
	    
	    logger.info("Final Total Time with Db Hash: " + (System.currentTimeMillis()-startTime) +"ms");
	}

	/**
	 * Run Fasta on one query and one target specified by static parameters
	 * @param hs - Hotspots with hashed DB
	 */
	protected ChainedString runFasta(HotSpots hs) {
		long startTime = System.currentTimeMillis();
		ArrayList<DiagonalRun> diagArr;
		ArrayList<Integer>[] hotspots;
		String query = FastaRunner.queries.get(query_num);
		String target = FastaRunner.targets.get(target_num);
		
		long hotspotstime = System.currentTimeMillis();
		hotspots = hs.discover(query_num, target_num, true);
		logger.fine("hotspot dicovery time: " + (System.currentTimeMillis() - hotspotstime) + "ms");

		long diagbuildertime = System.currentTimeMillis();
		DiagonalBuilder diagB = new DiagonalBuilder(hotspots, query.length());
		diagArr = diagB.extendDiagonals();
		logger.fine("DiagBuilder time: " + (System.currentTimeMillis() - diagbuildertime) + "ms");
		
		long chainertime = System.currentTimeMillis();
		DiagonalRunsChainer drc = new DiagonalRunsChainer(diagArr);
		double score = drc.connectDiagonals();
		logger.fine("Chainer time: " + (System.currentTimeMillis() - chainertime) + "ms");
		
		int from = drc.getFrom();
		int to = drc.getTo();
		int extra = 100;
		from = Math.max(0, from-extra);
		to = Math.min(to+query.length()+extra , target.length());
		return new ChainedString(target, query, score, target_num, query_num);
//		return new ChainedString(target.substring(from, to), query, score);
//
//		logger.fine("Need to align target from " + from + " to " + to);
//		
//		long subalignment = System.currentTimeMillis(); 
//		this.localAlignment.alignStrings(query, subTarget);
//		localAlignment.printResults();
//		logger.fine("subalign time: " + (System.currentTimeMillis() - subalignment) + "ms");
//
//		logger.info("total time (not including DB hashing): " + (System.currentTimeMillis() - startTime) + "ms");
		
//		System.out.println("hotspots time is: " + hotspotstime + " milliseconds");	
//		System.out.println("diagbuilder time is: " + diagbuildertime + " milliseconds");	
//		System.out.println("chainer time is: " + chainertime + " milliseconds");	
//		System.out.println("subalignment time is: " + subalignment + " milliseconds");
//		System.out.println("newRunningTime is: " + newRunningTime + " milliseconds");	
	}
	
	private void runFullAlignment() {
		long newRunningTime = System.currentTimeMillis(); 
		String query = FastaRunner.queries.get(query_num);
		String target = FastaRunner.targets.get(target_num);
//		query =  "AAACGTTGGGTTGGAAGTTGAAGGCCCGTCTATAGACCAAAGTCGGC";
//		target = "AAACGTTGGGTTCCTAGTTGAAGGCCCGTCTACCTACCAAAGTCGGC";

		
		Alignment alignedStrings = new NoGapsAlignment(query,target,"resources/Score5.matrix",true);
		alignedStrings.align();
		alignedStrings.printResults();
		
		newRunningTime =  System.currentTimeMillis()-newRunningTime;
		System.out.println("oldRunningTime is: " + newRunningTime + " milliseconds");
		
	}
	
	
	private static void Usage() {
		System.out.println("java allignments -f -l [query=? target =?]\n -f - run fasta\n -l - run slow alignment");
	}	
	
	private class ChainedString implements Comparable<ChainedString>{
		private String subtarget;
		private String query;
		private double score;
		private int targetInd;
		private int queryInd;
		
		public ChainedString(String target, String query, double score, int targetInd, int queryInd){
			this.subtarget = target;
			this.query = query;
			this.score = score;
			this.targetInd = targetInd;
			this.queryInd = queryInd;
		}
		
		public void align(){
			long start = System.currentTimeMillis();
			FastaRunner.this.localAlignment.alignStrings(subtarget, query);
			logger.info("alignment took : "+ (System.currentTimeMillis()-start) +"ms");
			logger.info("query: " + queryInd+ "against target: " + targetInd);
			FastaRunner.this.localAlignment.printResults();
			}
		
		public String getSubtarget() {
			return subtarget;
		}
		public void setSubtarget(String subtarget) {
			this.subtarget = subtarget;
		}
		public double getScore() {
			return score;
		}
		public void setScore(double score) {
			this.score = score;
		}
		
		@Override
		public int compareTo(ChainedString o) {
		
			return (int) (score-o.getScore());
		}
		
		
	}
}
