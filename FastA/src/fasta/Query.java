package fasta;

import java.util.ArrayList;

public class Query {
	public int amountOfTargets;
	public int index;
	public String name;
	public ArrayList<QTarget> topTargets;
	
	
	public Query(int index, String name) {
		//default amount of targets is 5
		this(index, name, 5);
	}
	
	public Query(int index, String name, int topResults){
		this.index = index;
		this.name = name;
		this.amountOfTargets = topResults;
		this.topTargets = new ArrayList<>();
	}
	
	public QTarget updateTopTargets(int targetInd, double score, String targetOutput, String queryOutput) {
		if (topTargets.size() < amountOfTargets) {
			QTarget qt = new QTarget(targetInd,score,targetOutput,queryOutput);
			this.topTargets.add(qt);
			return qt;
		} else {
			QTarget minTarget = topTargets.get(0);
			for (int i = 1; i < topTargets.size(); i++) {
				if (topTargets.get(i).score < minTarget.score) {
					minTarget = topTargets.get(i);
				}
			}
			if (minTarget.score < score) {
				this.topTargets.remove(minTarget);
				QTarget qt = new QTarget(targetInd,score,targetOutput,queryOutput);
				this.topTargets.add(qt);
				return qt;
			}
		}
		return null; // wasn't added to the top targets
	}
	
	public void updateTopFastaTargets(int targetInd, double score, int from, int to) {
		if (topTargets.size() < amountOfTargets) {
			this.topTargets.add(new QTarget(targetInd,score,from,to));
		} else {
			QTarget minTarget = topTargets.get(0);
			for (int i = 1; i < topTargets.size(); i++) {
				if (topTargets.get(i).score < minTarget.score) {
					minTarget = topTargets.get(i);
				}
			}
			if (minTarget.score < score) {
				this.topTargets.remove(minTarget);
				this.topTargets.add(new QTarget(targetInd,score,from,to));
			}
		}
	}
	
	
	class QTarget {
		public int targetInd;
		public double score;
		public String targetOutput;
		public String queryOutput;
		public int from;
		public int to;
		public int queryStartingPos;
		public int queryEndingPos;
		public int targetEndingPos;
		public int targetStartingPos;
		
		public QTarget (int targetInd, double score, String targetOutput, String queryOutput) {
			this.targetInd = targetInd;
			this.score = score;
			this.targetOutput = targetOutput;
			this.queryOutput = queryOutput;
		}
		
		public QTarget (int targetInd, double score, int from, int to) {
			this.targetInd = targetInd;
			this.score = score;
			this.from = from;
			this.to = to;
		}

		public void setAlignmentPositionResults(int minRow, int maxRow,
				int minCol, int maxCol) {
			this.queryStartingPos = minRow;
			this.queryEndingPos = maxRow;
			this.targetStartingPos = minCol;
			this.targetEndingPos = maxCol;
//			if (FastaStarter.queries.get(index).substring(minRow,maxRow).compareTo(queryOutput.replace("_", "")) != 0) {
//				System.out.println("WRONG INDEXES");
//			}
//			if (FastaStarter.targets.get(targetInd).substring(minCol,maxCol).compareTo(targetOutput.replace("_", "")) != 0) {
//				System.out.println("WRONG INDEXES");				
//			}
//			System.out.println("Setting Alignment Positions");
//			System.out.println(minRow + "*" + maxRow + "*" + minCol + "*" + maxCol);
//			System.out.println(FastaStarter.queries.get(index).substring(minRow,maxRow).compareTo(queryOutput.replace("_", "")));
//			System.out.println(FastaStarter.targets.get(targetInd).substring(minCol,maxCol).compareTo(targetOutput.replace("_", "")));
//			System.out.println("Test  Query:" + FastaStarter.queries.get(index).substring(minRow,maxRow));
//			System.out.println("Clean Query:" + queryOutput.replace("_", ""));
//			System.out.println("Other Query:" + queryOutput);
			
		}
		
		@Override
		public String toString() {
			String logStr = "\nQuery  #" + index + " " + FastaStarter.queriesNames.get(index);
			logStr += "\tPosition: " + this.queryStartingPos  + "..." + this.queryEndingPos + "\n";
			logStr += "Target #" + this.targetInd + " " + FastaStarter.targetsNames.get(this.targetInd);
			logStr += "\tPosition: " + this.targetStartingPos  + "..." + this.targetEndingPos + "\n";
			logStr += "Score: " + (int)this.score + "\n";
			logStr += "Query  Output: " + this.queryOutput + "\n";
			logStr += "Target Output: " + this.targetOutput + "\n";
			return logStr;
		}
	}
}
