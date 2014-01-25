
package alignments;


public class GapAlignment extends Alignment {


	public GapAlignment(String str2, String str1, String scoreMatFile, boolean local) {
		super(str2, str1, scoreMatFile, local);
		initWeightFunc();
	}
	
	@Override
	public void align() {
		System.out.println(local ? "Running local allignment with gaps": "Running global allignment with gaps");
		// if local than block the minimal value in the table by 0
		double minTableVal = local ? 0 : Double.NEGATIVE_INFINITY;
		this.gapM = new double[m + 1][n + 1]; // creating matrix
		this.gapPointers = new int[m + 1][n + 1];
		// initializing matrix
		this.gapM[0][0] = 0;
		double tmpScore = 0.0;
		double bestScore = 0.0;
		int pointer = 0;
		for (int j = 1; j <= n; j++) {	// base row
			bestScore = Math.max(this.gapM[0][j-1] - w[1], minTableVal);
			pointer = 1;
			for (int k=2; k <= j; k++) {
				tmpScore = this.gapM[0][j-k] - w[k]; 
				if (tmpScore > bestScore) {
					bestScore = tmpScore;
					pointer = k;
				}
			}
			this.gapM[0][j] = bestScore;
			this.gapPointers[0][j] = pointer;	// positive pointer value for Insertion
		}
		for (int i = 1; i <= m; i++) {	// base col
			bestScore = Math.max(this.gapM[i-1][0] - w[1], minTableVal);
			pointer = 1;
			for (int k=2; k <= i; k++) {
				tmpScore = this.gapM[i-k][0] - w[k]; 
				if (tmpScore > bestScore) {
					bestScore = tmpScore;
					pointer = k;
				}
			}
			this.gapM[i][0] = bestScore;
			this.gapPointers[i][0] = -pointer;	// negative pointer value for Deletion
		}
		// filling matrix
		double maxScore= 0;
		int maxRow=m;
		int maxColumn=n;
		for (int i = 1; i <= m; i++) {		// rows S2[i]
			for (int j = 1; j <= n; j++) {	// cols S1[j]
				bestScore = Math.max(this.gapM[i-1][j-1] + this.score[this.s1[i]][this.s2[j]],minTableVal);	// M\R
				pointer = 0;
				for (int k = 1; k <= i; k++) {	// getting max from column
					tmpScore = this.gapM[i-k][j] - w[k];
					if (tmpScore > bestScore) {
						bestScore = tmpScore;
						pointer = -k;
					}
				}
				for (int k = 1; k <= j; k++) {	// getting max from row
					tmpScore = this.gapM[i][j-k] - w[k];
					if (tmpScore > bestScore) {
						bestScore = tmpScore;
						pointer = k;
					}
				}
				this.gapM[i][j] = bestScore;
				this.gapPointers[i][j] = pointer;
				if (local && maxScore<bestScore){
					maxScore=bestScore;
					maxRow = i;
					maxColumn = j;
				}
			}

		}
		this.finalScore = this.gapM[maxRow][maxColumn];

		//trace back
		traceBack(maxRow,maxColumn,0, new StringBuilder(maxColumn), new StringBuilder(maxRow));
	}

	// Previous recursive stackoverflow traceback
//	@Override
	public void traceBackRec(int row, int col, int pointer,
			StringBuilder SB_Target, StringBuilder SB_Query) {
		//Stop recursion if local or global condition is met
				if ((this.gapM[row][col]==0 && local) || (row==0 && col==0) ) {
					this.targetOutput = SB_Target.reverse().toString();
					this.queryOutput = SB_Query.reverse().toString();
					return;
				}
				if (this.gapPointers[row][col]==0) {
						// Match / Replace
						SB_Target.append(this.target.charAt(col-1));
						SB_Query.append(this.query.charAt(row-1));
						traceBack(row-1, col-1, 0, SB_Target, SB_Query);
				}
				if (this.gapPointers[row][col]>0){
					// Insertion
						for (int i = 1; i<=this.gapPointers[row][col]; i++) {
							SB_Target.append(this.target.charAt(col-i)) ;
							SB_Query.append('_') ;
						}
						traceBack(row, col-this.gapPointers[row][col], 0, SB_Target, SB_Query);
				}
				if (this.gapPointers[row][col]<0) {
					// Deletion
					for (int i = 1; i<=-this.gapPointers[row][col]; i++) {
						SB_Target.append('_') ;
						SB_Query.append(this.query.charAt(row-i)) ;
					}
					traceBack(row+this.gapPointers[row][col], col, 0, SB_Target, SB_Query);
				}
				
	}

	/* (non-Javadoc)
	 * @see Alignments.Alignment#traceBack(int, int, int, java.lang.StringBuilder, java.lang.StringBuilder)
	 */
	@Override
	public void traceBack(int row, int col, int pointer, StringBuilder SB_Target, StringBuilder SB_Query) {
		//Stop recursion if local or global condition is met
		while (!((this.gapM[row][col]==0 && local) || (row==0 && col==0) )) {
			if (this.gapPointers[row][col]==0) {
				// Match / Replace
				SB_Target.append(this.target.charAt(col-1));
				SB_Query.append(this.query.charAt(row-1));
				row--;
				col--;
			}
			else if (this.gapPointers[row][col]>0){
				// Insertion
				for (int i = 1; i<=this.gapPointers[row][col]; i++) {
					SB_Target.append(this.target.charAt(col-i)) ;
					SB_Query.append('_') ;
				}
				col = col - this.gapPointers[row][col];
			}
			else if (this.gapPointers[row][col]<0) {
				// Deletion
				for (int i = 1; i<=-this.gapPointers[row][col]; i++) {
					SB_Target.append('_') ;
					SB_Query.append(this.query.charAt(row-i)) ;
				}
				row = row + this.gapPointers[row][col];
			}
		}
		this.targetOutput = SB_Target.reverse().toString();
		this.queryOutput = SB_Query.reverse().toString();
		return;	
	}
	
	public void traceBack() {
		
	}
	
	protected void initWeightFunc() {
		int size = Math.max(this.n, this.m);
		this.w = new double[size+1];
		this.w[0]=0;
		for (int i = 1; i <= size; i++) {
			this.w[i] = 10 + Math.log(i);
		}
		
	}

}
