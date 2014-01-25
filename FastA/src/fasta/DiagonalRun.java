package fasta;

public class DiagonalRun {
	private int score;
	private int StartingX;
	private int StartingY;
	private int EndingX;
	private int EndingY;
	public final int maxGapLength = FastaRunner.ktup-1;
	public double d = Double.MIN_VALUE;
	public DiagonalRun pi = null;
	

	public DiagonalRun(int x, int y) {
		this.score = FastaRunner.ktup;
		this.StartingX = x;
		this.StartingY = y;
		this.EndingX = x;
		this.EndingY = y;
	}
	
	public boolean extend(int newX) {
//		System.out.println("preX:"+EndingX + " ** newX:"+newX);
		int gap = (newX-this.EndingX);
		if (gap == 1) {
			this.score++;
			this.EndingX++;
			this.EndingY++;
			return true;
		}
		if ( gap <= FastaRunner.ktup + maxGapLength ) {
			this.score += ((2*FastaRunner.ktup) - gap);
			this.EndingX += gap;
			this.EndingY += gap;
			return true;
		} 
		return false;
	}
	
	public int getLength() {
		return ((this.EndingX-this.StartingX)+FastaRunner.ktup);
	}
	
	public int getScore() {
		return this.score;
	}
	
	public int getStartingX() { 
		return this.StartingX;
	}
	
	public int getStartingY() { 
		return this.StartingY;
	}
	
	public int getEndingX() { 
		return this.EndingX;
	}
	
	public int getEndingY() { 
		return this.EndingY;
	}

	
	
	@Override
	public String toString() {
		String str = "<(" + this.StartingX + "," + this.StartingY + ")-("
				+ this.EndingX + "," + this.EndingY + ")>";
		return str;
	}
//	@Override
//	public String toString() {
//		String str = "(" + this.StartingX + "," + this.StartingY + ") - ("
//				+ this.EndingX + "," + this.EndingY + ") : length="
//				+ this.getLength() + " ; score=" + this.getScore() + "\n";
//		return str;
//	}

	public void updateEndingCoords() {
		this.EndingX += FastaRunner.ktup-1;
		this.EndingY += FastaRunner.ktup-1;
	}

}
