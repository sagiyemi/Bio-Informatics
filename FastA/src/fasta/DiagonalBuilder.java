package fasta;

import java.util.ArrayList;

public class DiagonalBuilder {
	private ArrayList<DiagonalRun> topDiagonals;
	private ArrayList<DiagonalRun> allDiagonals;
	private ArrayList<Integer>[] hotspots;
	private int queryN; // length-1 (for array pos)
	public final int amoutOfTopDiagonals = 10;
	
	

	public DiagonalBuilder(ArrayList<Integer>[] hotspots, int queryN) {
		this.topDiagonals= new ArrayList<DiagonalRun>();
		this.allDiagonals= new ArrayList<DiagonalRun>();
		this.hotspots = hotspots;
		this.queryN = queryN-1;
	}
	
	public void buildDiagonals() {
		int x,y;
		DiagonalRun dr;
		for (int i = 0; i < this.hotspots.length; i++) {
			while ((this.hotspots[i]!=null) && (!(this.hotspots[i].isEmpty()))) {			// create new diagonalRun
				x = this.hotspots[i].remove(0);
				y = i+x-this.queryN;
//				System.out.println("i="+i+" ** x=" +x);
				dr = new DiagonalRun(x, y);
				this.allDiagonals.add(dr);
				while (!(this.hotspots[i].isEmpty())) {		// extend current diagonalRun to max
					x = this.hotspots[i].get(0);
//					System.out.println("i="+i+" ** x=" +x);
					if (dr.extend(x)) {
						this.hotspots[i].remove(0);
					} else {
						break;
					}
				}
			}
		}
	}
	
	public void pickTopDiagonals() {
		int i = 0;
		int max;
		DiagonalRun dr;
//		System.out.println("******");
//		System.out.println(this.allDiagonals);
//		System.out.println("******");
		while ((i<amoutOfTopDiagonals) && (!this.allDiagonals.isEmpty())) {
			dr = this.allDiagonals.get(0); 
			max = dr.getScore();
			for (DiagonalRun currDr : this.allDiagonals) {
				if (currDr.getScore()>max){
					dr = currDr;
					max = currDr.getScore();
				}
			}
			dr.updateEndingCoords();
			this.topDiagonals.add(dr);
			this.allDiagonals.remove(dr);
			i++;
		}
	}
	
	
	public ArrayList<DiagonalRun> extendDiagonals() {
		buildDiagonals();
		pickTopDiagonals();
		return this.topDiagonals;
	}
	
	
	
	
}
