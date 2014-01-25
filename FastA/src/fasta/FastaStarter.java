package fasta;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import alignments.Alignment;
import alignments.LocalAlignment;
import alignments.NoGapsAlignment;
import fasta.Query.QTarget;


public class FastaStarter {
	public static Logger logger;
	public Observer observer;
	public static int query_num, target_num;
	public static boolean stopAll = false;
	public static int ktup;
	public static int numOfTopResults;
	public static double percentageFilteration;
	public static boolean hashDB;
	public static ArrayList<String> queries;
	public static ArrayList<String> queriesNames;
	public static ArrayList<Query> queryArr;
	public static ArrayList<String> targets;
	public static ArrayList<String> targetsNames;
	public HotSpots hotspots;
	public LocalAlignment localAlignment;



	/**
	 * FastaStarter constructor.
	 */
	FastaStarter() {
		this.observer = new Observer(this);
		this.queries = new ArrayList<>();
		this.queriesNames = new ArrayList<>();
		this.queryArr = new ArrayList<>();
		this.targets = new ArrayList<>();
		this.targetsNames = new ArrayList<>();
		this.hotspots = new HotSpots();
	}


	/**
	 * Main method.
	 * @param args	the given arguments (file names).
	 * @throws IOException an exception if input file is missing.
	 */
    public static void main(String[] args) {
    	long timer = System.currentTimeMillis();
    	
    	setDefaultParams();
    	
    	//for logger
    	String logH = "";
		String logS = "";
		String logD = "";
		String logO = "";
    	
    	//program parameters
		boolean runFasta= false, fullLocal = false, cutTarget=true;
		if (args.length < 3  || args[0] == "--help"){
			usage();
			return;
		}
		try {
			for (int i = 3; i < args.length; i++) {
				if (args[i].startsWith("-")) {
					switch (args[i]) {
					case "-h" : runFasta = true; logH="Huristic";   break;
					case "-l" : fullLocal = true; logS="SW";  break; 
					case "-k" : ktup = Integer.parseInt(args[++i]);	break;
					case "-o" : cutTarget = false; logO="Optimized"; break;
					case "-m" : numOfTopResults = Integer.parseInt(args[++i]); break;
					case "-t" : percentageFilteration = Double.parseDouble(args[++i])/100.0; break;
					case "-d" : hashDB = false; logD = "NoHash"; break; 
					default : System.out.println("wrong flag"); break;
					}
				}
			}	
		} catch (Exception e) {
			usage();
			System.err.println("Error: " + e.getMessage());
			return;
		}
    	setupLogger(logS+logH+logO+logD);
    	
    	//run
		FastaStarter fs = new FastaStarter();
    	fs.logVariables(args,runFasta,fullLocal,cutTarget);
    	FastaStarter.logger.info("Main started");
//    	Thread observerThread = new Thread(fs.observer);
//    	observerThread.start();
    	
    	fs.readFiles(args[2], args[1]);	// args[2] = query		args[1] = db 
//    	fs.runLocalAlignments();
    	fs.createAlignment(args[0]);
    	if (fullLocal)
    		fs.runAllLocalQueries();
    	if (runFasta) { 
    		if (hashDB) 
    			fs.hashDB();
    		fs.runAllFastaLocalQueries(cutTarget);
    	}
    	timer =  System.currentTimeMillis()-timer;
		FastaStarter.logger.info("\n\t\t\t\t\tMain Exits\n\t\t\t*****\tMain running time: " + timer + " ms\t*****\n"); 	
    }



	private static void setDefaultParams() {
    	// default values
    	ktup = 6;
    	numOfTopResults = 5;
    	hashDB = true;
    	percentageFilteration = 0.7;
    	numOfTopResults = 30;
	}


	private static void usage() {
		System.out.println("USAGE: java Alignments.jar Score.matrix database.fasta query.fasta -h -l -o -d -k [ktup] -m [topResults] -t [ percentage threshold] \r\n -h = heuristic align \r\n -l = full SW local alignment \r\n -o = Optimize \r\n -d = disable Hash \r\n -k [ktup] = define ktup size \r\n -m [topResults] = define number of results to align \r\n -t [percentage threshold] only display results that are bigger than (max_score)*(percentage threshold)");
	}


	private static void setupLogger(String loggerName){
		FastaStarter.logger = Logger.getLogger(loggerName);
		FastaStarter.logger.setLevel(Level.INFO);
		try{
		Handler fh = new FileHandler("output/" + loggerName + ".log");
	 	FastaStarter.logger.addHandler(fh);
	 	fh.setFormatter(new SimpleFormatter());
		}
	 	catch (IOException e){
	 	System.out.println(e.getMessage()+"\r\n"+"No log file will be created");	
	 	}
	}


	protected void createAlignment(String matFile) {
		if (matFile==null)
			matFile="resources/Score5.matrix";
		this.localAlignment = new LocalAlignment(matFile);
		this.localAlignment.init();
	}


	private void readFiles(String queryPath, String dbPath) {
		long timer = System.currentTimeMillis();
		try{
			  // Open the file that is the first 
			  // command line parameter
			  FileInputStream fstream = new FileInputStream(queryPath);		//"resources/query.fasta" originally
			  // Get the object of DataInputStream
			  DataInputStream in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  String strLine;
			  //Read File Line By Line
			  while ((strLine = br.readLine()) != null)   {
			  // Print the content on the console
				  if (strLine.startsWith(">")) {
					  FastaStarter.queriesNames.add(strLine.substring(1));
				  } else {
					  FastaStarter.queries.add(strLine);
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
			FileInputStream fstream = new FileInputStream(dbPath);	// "resources/virus_DB.fasta" originally
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				// Print the content on the console
				if (strLine.startsWith(">")) {
					FastaStarter.targetsNames.add(strLine.substring(1));
				} else {
					FastaStarter.targets.add(strLine);
				}
			}
			
			//Close the input stream
			in.close();
		}
		catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		timer = System.currentTimeMillis() - timer;
    	FastaStarter.logger.info("\n\t\t\t*****\tFiles reading time: " + timer + " ms\t*****\n");
	}


	private void hashDB() {
		long timer = System.currentTimeMillis();
		hotspots.hashDB();
		timer =  System.currentTimeMillis()-timer;
		FastaStarter.logger.info("\n\t\t\t*****\tDataBase hashing running time: " + timer + " ms\t*****\n");
	}
	
	
	public void runLocalAlignments() {
		System.out.println("run Local Alignments");
		int counter = 0;
		long timer = System.currentTimeMillis();
		for (String query: FastaStarter.queries) {
			for (String target : FastaStarter.targets) {
				counter++;
				Alignment alignedStrings = new NoGapsAlignment(query,target,"resources/Score5.matrix",true);
				alignedStrings.align();
				alignedStrings.printResults();		
			}
			return;
		}
		timer =  System.currentTimeMillis()-timer;
		FastaStarter.logger.info("\n\t\t\t*****\tLocal alignments running time: " + timer + " ms, for " + counter + " alignments\t*****\n");
	}
	

	
	public void runAllLocalQueries() {
		long timer = System.currentTimeMillis();
		for (int i = 0; i < FastaStarter.queries.size(); i++) {
			runLocalQuery(i);
		}
		timer =  System.currentTimeMillis()-timer;
		FastaStarter.logger.info("\n\t\t\t*****\tTotal Local running time: " + timer + " ms\t*****\n"); 	
	}
	
	
	public void runLocalQuery(int ind) {
		if (ind >= FastaStarter.queries.size()) {
			FastaStarter.logger.info("wrong query index");
		} else {
			long timer = System.currentTimeMillis();
			String query = FastaStarter.queries.get(ind);
			Query currQuery = new Query(ind, FastaStarter.queriesNames.get(ind), numOfTopResults);
			for (int i = 0; i < FastaStarter.targets.size(); i++) {
				String target = FastaStarter.targets.get(i);
				this.localAlignment.alignStrings(target, query);
//				this.localAlignment.printResults();
				QTarget currQT = currQuery.updateTopTargets(i, localAlignment.getFinalScore(), localAlignment.getTargetOutput(), localAlignment.getQueryOutput());
				if (currQT != null) {	// added to top targets
					currQT.setAlignmentPositionResults(localAlignment.getMinRow(), localAlignment.getMaxRow(),
							localAlignment.getMinCol(), localAlignment.getMaxCol());
				}
			}
			String logStr = "\nTop alignments for query #" + ind + " " + FastaStarter.queriesNames.get(ind) + "\n";
//			FastaStarter.logger.info("Top alignments for query #" + ind);
			// Filter top results
			double maxResult = 0.0;
			for (int j = 0; j < currQuery.topTargets.size(); j++) {
				QTarget qt = currQuery.topTargets.get(j);
				maxResult = Math.max(maxResult, qt.score);
			}
			double threshholdResult = FastaStarter.percentageFilteration * maxResult;
			for (int j = 0; j < currQuery.topTargets.size(); j++) {
				QTarget qt = currQuery.topTargets.get(j);
				if (qt.score > threshholdResult)
					logStr += qt.toString();
			}
			FastaStarter.logger.info(logStr);
			timer =  System.currentTimeMillis()-timer;
			FastaStarter.logger.info("\n\t\t\t*****\tQuery #" + ind + " " + FastaStarter.queriesNames.get(ind) + " local alignment running time: " + timer + " ms\t*****\n");
		}
	}
    
	
	public void runAllFastaLocalQueries(boolean runOnSubMat) {
		long timer = System.currentTimeMillis();
		for (int i = 0; i < FastaStarter.queries.size(); i++) {
			runFastaLocalQuery(i,runOnSubMat);
		}
		timer =  System.currentTimeMillis()-timer;
		FastaStarter.logger.info("\n\t\t\t*****\tTotal Fasta running time: " + timer + " ms\t*****\n"); 	
	}
	
	
	public void runFastaLocalQuery(int ind, boolean runOnSubMat) {
		ArrayList<DiagonalRun> diagArr;
		ArrayList<Integer>[] hotspotsArr;
		if (ind >= FastaStarter.queries.size()) {
			FastaStarter.logger.info("wrong query index");
		} else {
			long timer = System.currentTimeMillis();
			String query = FastaStarter.queries.get(ind);
			Query currQuery = new Query(ind, FastaStarter.queriesNames.get(ind), numOfTopResults);
			for (int targetInd = 0; targetInd < FastaStarter.targets.size(); targetInd++) {
				hotspotsArr = hotspots.discover(ind, targetInd, hashDB);
				DiagonalBuilder diagB = new DiagonalBuilder(hotspotsArr, query.length());
				diagArr = diagB.extendDiagonals();
				DiagonalRunsChainer drc = new DiagonalRunsChainer(diagArr);
				double score = drc.connectDiagonals();
//				System.out.println("query #" + ind + " target #" + i + " Score:" + score);
//				this.localAlignment.alignStrings(target, query);
				currQuery.updateTopFastaTargets(targetInd, score, drc.getFrom(), drc.getTo());
			}
			// aligning max results
			for (int i = 0; i<currQuery.topTargets.size(); i++) {
				QTarget qt = currQuery.topTargets.get(i);
				String target = targets.get(qt.targetInd);
				
				if (runOnSubMat) {
					int extra = 100;
					int from = Math.max(0, qt.from-extra);
					int to = Math.min(qt.to+query.length()+extra , target.length());
					// align on subtarget
					this.localAlignment.alignStrings(target.substring(from, to), query);
					qt.setAlignmentPositionResults(localAlignment.getMinRow(), localAlignment.getMaxRow(),
							from+localAlignment.getMinCol(), from+localAlignment.getMaxCol());
				} else {
					this.localAlignment.alignStrings(target, query);
					qt.setAlignmentPositionResults(localAlignment.getMinRow(), localAlignment.getMaxRow(),
							localAlignment.getMinCol(), localAlignment.getMaxCol());
				}
				
				qt.score = this.localAlignment.getFinalScore();
				qt.targetOutput = this.localAlignment.getTargetOutput();
				qt.queryOutput = this.localAlignment.getQueryOutput();
			}
			// logging
			String logStr = "\nTop Fasta alignments for query #" + ind + " " + FastaStarter.queriesNames.get(ind) + "\n";
			if (runOnSubMat) {
				logStr += "Running on subtarget\n";
			} else {
				logStr += "Running on entire target\n";
			}
			// Filtering
			double maxResult = 0.0;
			for (int j = 0; j < currQuery.topTargets.size(); j++) {
				QTarget qt = currQuery.topTargets.get(j);
				maxResult = Math.max(maxResult, qt.score);
			}
			double threshholdResult = FastaStarter.percentageFilteration * maxResult;
			for (int j = 0; j < currQuery.topTargets.size(); j++) {
				QTarget qt = currQuery.topTargets.get(j);
				if (qt.score > threshholdResult)
					logStr += qt.toString();
			}
			FastaStarter.logger.info(logStr);
			timer =  System.currentTimeMillis()-timer;
			FastaStarter.logger.info("\n\t\t\t*****\tQuery #" + ind + " " + FastaStarter.queriesNames.get(ind) + " Fasta local alignment running time: " + timer + " ms\t*****\n");
		
		}
	}
    

    // prints to file all the variables used by the program
	private void logVariables(String[] args, boolean runFasta, boolean fullLocal, boolean cutTarget) {
		String str = "\nVariables:";
		str += "\nScore mat file: " + args[0];
		str += "\nDatabase file : " + args[1];
		str += "\nQuery file    : " + args[2];
		str += "\nHash DB       : " + hashDB;
		str += "\nTop Results   : " + numOfTopResults; 
		str += "\nthreshold     : " + percentageFilteration;
		str += "\nKtup          : " + ktup + "\n";
		if (runFasta) str += "Running Fasta\n";
		if (fullLocal) str += "Running full local alignments\n";
		if (cutTarget) str += "Running faster Fasta\n";
		FastaStarter.logger.info(str);
	}




}
