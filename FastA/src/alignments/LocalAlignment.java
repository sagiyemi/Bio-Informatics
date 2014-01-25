
package alignments;


public class LocalAlignment extends Alignment {


	public LocalAlignment(String scoreMatFile) {
		super(scoreMatFile);
	}

	
	
	public void init() {
		this.score = readScoreMat(scoreMatFile);
	}
	
	
	
	public void alignStrings(String target, String query) {
		this.target = target.toUpperCase();
		this.query = query.toUpperCase();
		initStrings();
		align();
	}
	
	
	
	
	@Override
	public void align() {
		this.M = new int[m + 1][n + 1]; // creating matrix
		this.pointers = new int[m + 1][n + 1];
		this.finalScore = 0;
		int maxC = n;
		int maxR = m;
		
		// Base
		this.M[0][0] = 0;	
		for (int i = 1; i <= n; i++) {	// base row
			this.M[0][i] = 0;
			this.pointers[0][i] = Alignment.insI;
		}
		for (int i = 1; i <= m; i++) {	// base col
			this.M[i][0] = 0;
			this.pointers[i][0] = Alignment.delD;
		}
		
		// filling matrix
		int res1,res2,res3;
		for (int i = 1; i <= m; i++) {		// rows S2[i]
			for (int j = 1; j <= n; j++) {	// cols S1[j]
				res1 = this.M[i-1][j-1] + this.score[this.s1[i]][this.s2[j]];	// M\R
				res2 = this.M[i-1][j] + this.score[this.s1[i]][6];				// D
				res3 = this.M[i][j-1] + this.score[6][this.s2[j]];				// I
				if (res1>res2) {
					this.M[i][j] = res1;
					this.pointers[i][j] = Alignment.diagM;
				} else {
					this.M[i][j] = res2;
					this.pointers[i][j] = Alignment.delD;
				}
				if (res3 > this.M[i][j]) {
					this.M[i][j] = res3;
					this.pointers[i][j] = Alignment.insI;
				}
				if (0 > this.M[i][j]) {
					this.M[i][j] = 0;
				}
				if (this.M[i][j] > this.finalScore) {
					this.finalScore = this.M[i][j];
					maxC = j;
					maxR = i;
				}
			}
		}
		
		// trace back
		this.maxCol = maxC;
		this.maxRow = maxR;
		traceBack();	
	}
	
	

	public void traceBack() {
		StringBuilder SB_Target = new StringBuilder();
		StringBuilder SB_Query = new StringBuilder();
		int row = this.maxRow;
		int col = this.maxCol;
		//stop condition	
		while (!((this.M[row][col]==0) || (row==0 && col==0))){
			switch (pointers[row][col]){
			case diagM:		// Match / Replace
				row--;
				col--;
				SB_Target.append(this.target.charAt(col));
				SB_Query.append(this.query.charAt(row));
				break;
			case insI :		// Insertion
				col--;
				SB_Target.append(this.target.charAt(col));
				SB_Query.append("_");
				break;
			case delD :		// Deletion
				row--;
				SB_Target.append("_");
				SB_Query.append(this.query.charAt(row));
				break;
			}
			
		}
		this.targetOutput = SB_Target.reverse().toString();
		this.queryOutput = SB_Query.reverse().toString();
		this.minCol = col;
		this.minRow = row;
		return;
	}
	
	
	

	@Override
	public void traceBack(int row, int col, int pointer,
			StringBuilder SB_Target, StringBuilder SB_Query) {
		// TODO Auto-generated method stub
	}
	
}
