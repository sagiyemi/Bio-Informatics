package alignments;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public abstract class Alignment {

	final int minInf = -99999;
	static final byte end = 7;
	static final byte diagM = 0;
	static final byte diagD = 1;
	static final byte diagI = 2;
	static final byte insM = 3;
	static final byte insI = 4;
	static final byte delM = 5;
	static final byte delD = 6;
	final byte A = 0;
	final byte T = 1;
	final byte G = 2;
	final byte C = 3;
	final byte U = 4;
	final byte N = 5;
	final byte Space = 6;
	final String scoreMatFile;
	int gapA;
	int gapB;
	int score[][];
	double w[];
	String target, query;
	int s2[], s1[];
	int n, m;
	int M[][];
	double gapM[][];
	int gapPointers[][];
	int D[][];
	int I[][];
	int pointers[][];
	int pointersIns[][];
	int pointersDel[][];
	protected String targetOutput = "";
	protected String queryOutput = "";
	protected double finalScore;
	//if true run local alignment, else run global
	boolean local;
	protected int maxRow;
	protected int maxCol;
	protected int minCol;
	protected int minRow;
		

	public Alignment(String scoreMatFile) {
		this.scoreMatFile = scoreMatFile;
	}
	
	public Alignment(String str2, String str1, String scoreMatFile, boolean local) {
		this.target = str2.toUpperCase();
		this.query = str1.toUpperCase();
		this.scoreMatFile = scoreMatFile;
		this.local = local;
		initAll();
	}
	
	////////////////////////////////////
	// Initializing
	////////////////////////////////////
	
	protected void initAll() {
		initStrings();
		initScoreMatrix();
	}
	
	protected void initScoreMatrix() {
		this.score = readScoreMat(scoreMatFile);
	}
	
	
	
	protected int[][] readScoreMat(String scoreMatFile) {
		int[][] score = new int[7][7];
		try{
			  // Open the file that is the first 
			  // command line parameter
			  FileInputStream fstream = new FileInputStream(this.scoreMatFile);
			  // Get the object of DataInputStream
			  DataInputStream in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  String strLine;
			  //Read File Line By Line
			  while ((strLine = br.readLine()) != null)   {
			  // Print the content on the console
				  if (!strLine.startsWith("#"))
					  break;
			  }
			  for (int i = 0; i < 7; i++) {
				  if ((strLine = br.readLine()) != null) {
					  for (int j = 0; j < 6; j++) {
						  strLine = (strLine.substring(strLine.indexOf(" "))).trim();  
						  score[i][j] = Integer.parseInt(strLine.substring(0, strLine.indexOf(" ")));
					  }
					  //For I/D
					  strLine = (strLine.substring(strLine.indexOf(" "))).trim();  
					  score[i][6] = Integer.parseInt(strLine);
				  }
			  }
			  while ((strLine = br.readLine()) != null)   {
				  if (strLine.startsWith("A")) {
					  this.gapA = Integer.parseInt(strLine.substring(1).trim());
				  } else if (strLine.startsWith("B")) {
					  this.gapB = Integer.parseInt(strLine.substring(1).trim());
				  }
			  }
			  
			  //Close the input stream
			  in.close();
		}
		catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
		}
		return score;
	}


	protected void initStrings() {
		this.n = this.target.length();
		this.m = this.query.length();
		this.s2 = new int[n + 1];
		this.s1 = new int[m + 1];
		this.s2[0] = -1;
		this.s1[0] = -1;
		char c;
		for (int i = 0; i < n; i++) {
			c = this.target.charAt(i);
			switch (c) {
	        	case 'A' : this.s2[i+1] = A;   break;
	        	case 'T' : this.s2[i+1] = T;   break;
	        	case 'G' : this.s2[i+1] = G;   break;
	        	case 'C' : this.s2[i+1] = C;   break;
	        	case 'U' : this.s2[i+1] = U;   break;
	        	case 'N' : this.s2[i+1] = N;   break;
			}
		}
		for (int i = 0; i < m; i++) {
			c = this.query.charAt(i);
			switch (c) {
			case 'A' : this.s1[i+1] = A;   break;
			case 'T' : this.s1[i+1] = T;   break;
			case 'G' : this.s1[i+1] = G;   break;
			case 'C' : this.s1[i+1] = C;   break;
			case 'U' : this.s1[i+1] = U;   break;
			case 'N' : this.s1[i+1] = N;   break;
			}
		}

	}
	

	public void align() {
		this.M = new int[m + 1][n + 1]; // creating matrix
		this.pointers = new int[m + 1][n + 1];
		this.M[0][0] = 0;	
		this.finalScore = 0;	
	}
		
	public void printResults() {
		System.out.println("Output:");
		System.out.println(this.targetOutput);
		System.out.println(this.queryOutput);
		System.out.println("Score: " + this.finalScore);
	}
	


	public abstract void traceBack(int row, int col, int pointer,  StringBuilder SB_Target, StringBuilder SB_Query);
	

	public void traceBack(int targetSize, int querySize){
		traceBack(maxRow, maxCol, 0, new StringBuilder(targetSize), new StringBuilder(querySize));
	}

	public void printLocalResults() {
		System.out.println("Output:");
		System.out.println(this.targetOutput);
		System.out.println(this.queryOutput);
		System.out.println("Score: " + this.finalScore);
	}
	

	////////////////////////////////////
	// Fun Stuff
	////////////////////////////////////
	
	public void printMat() { 
		if (this.M==null) System.out.println("no matrix to print");
		for (int i = 0; i <= m; i++) {
			for (int j = 0; j <= n; j++) {
				System.out.print(this.M[i][j] +"("+this.pointers[i][j]+")"+ " , ");
			}
			System.out.println();
		}
	}
	
	public void printAffineMat() { 
		if (this.M==null) System.out.println("no matrix to print");
		for (int i = 0; i <= m; i++) {
			for (int j = 0; j <= n; j++) {
				System.out.print("{"+this.M[i][j] +"," + this.D[i][j] + "," + this.I[i][j]+ "} ");
			}
			System.out.println();
		}
	}

	public void printScoreMat() {
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 7; j++) {
				System.out.print(this.score[i][j] + " | ");
			}
			System.out.println("");
		}
	}


	public static void main(String[] args) {
		System.out.println("Test started");
		String scoreFile = "";
		boolean local =false;
		boolean affine = false;
		boolean gap = false;
		boolean file = false;
		String seq1 = null,seq2 = null;
		long profileStart;
		long profileElpased;
		Alignment alignedStrings;
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")) {
				switch (args[i]) {
				case "-g" : local = false; break;
				case "-l" : local = true; break;
				case "-a" : affine = true; break;
				case "-p" : gap = true; break;
				case "-f" : file = true; break;
				case "-h" : Usage(); return;
				default : System.out.println("wrong flag"); break;
				}
			} else if (args[i].endsWith("matrix")) {
				scoreFile = args[i];
			}
		}
		if (scoreFile.length()==0) scoreFile = args[args.length-3];
		if (file){
			try{
				FileInputStream fis = new FileInputStream(args[args.length-2]);
				Scanner sc = new Scanner(fis,"UTF-8");
				seq1 = sc.nextLine();
				fis.close();
				sc.close();
				fis =  new FileInputStream(args[args.length-1]);
				sc = new Scanner(fis,"UTF-8");
				seq2 = sc.nextLine();
				sc.close();
			}
			catch (Exception e){
				e.printStackTrace();
				return;
			}
		}
		else{
			seq1 = args[args.length-2];
			seq2 = args[args.length-1];
		}
		profileStart=System.currentTimeMillis();
		if (affine) 
			alignedStrings = new AffineAlignment(seq1, seq2, scoreFile, local);
		else if (gap) 
			alignedStrings = new GapAlignment(seq1, seq2, scoreFile, local);
		else 
			alignedStrings = new NoGapsAlignment(seq1, seq2, scoreFile, local);
		alignedStrings.align();
		alignedStrings.printResults();
		
		profileElpased =  System.currentTimeMillis()-profileStart;
		System.out.println("RunTime is: " + profileElpased + " milliseconds");
//		alignedStrings.printScoreMat();
	}
	
	public double getFinalScore() {
		return finalScore;
	}
	
	public String getTargetOutput() {
		return targetOutput;
	}

	public String getQueryOutput() {
		return queryOutput;
	}
	
	public int getMinCol() {
		return this.minCol;
	}
	
	public int getMinRow() {
		return this.minRow;
	}
	
	public int getMaxCol() {
		return this.maxCol;
	}
	
	public int getMaxRow() {
		return this.maxRow;
	}
	

	private static void Usage() {
		System.out.println("java allignments -g/-l -a/-p [Score*.matrix] [-f TargetFile QueryFile]/TargetString QueryString");
	}
}