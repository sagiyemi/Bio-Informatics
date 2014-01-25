
package alignments;


public class NoGapsAlignment extends Alignment {


	public NoGapsAlignment(String str2, String str1, String scoreMatFile, boolean local) {
		super(str2, str1, scoreMatFile, local);
		// TODO Auto-generated constructor stub
	}
	
	public NoGapsAlignment(String scoreMatFile){
		super(scoreMatFile);
	}

	@Override
	public void align() {
		super.align();
		System.out.println(local ? "Running local alignment" : "Running global alignment" );
		//will change only on local alignment
		int maxC = n;
		int maxR = m;
		this.finalScore = 0;
		this.M = new int[m + 1][n + 1]; // creating matrix
		this.pointers = new int[m + 1][n + 1];
		// initializing matrix
		this.M[0][0] = 0;
		if (this.local){
			for (int i = 1; i <= n; i++) {	// base row
				this.M[0][i] = 0;
				this.pointers[0][i] = Alignment.insI;
			}
			for (int i = 1; i <= m; i++) {	// base col
				this.M[i][0] = 0;
				this.pointers[i][0] = Alignment.delD;
			}
		}
		else{
			for (int i = 1; i <= n; i++) {	// base row
				this.M[0][i] = this.M[0][i-1] + this.score[this.Space][this.s2[i]];
				this.pointers[0][i] = Alignment.insI;
			}
			for (int i = 1; i <= m; i++) {	// base col
				this.M[i][0] = this.M[i-1][0] + this.score[this.s1[i]][this.Space];
				this.pointers[i][0] = Alignment.delD;
			}
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
				if (this.local){ 
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
		}
		// trace back
		this.maxCol = maxC;
		this.maxRow = maxR;
		traceBack();
//		traceBack(maxR, maxC, 0, new StringBuilder(), new StringBuilder());
		if (!this.local) 
			this.finalScore = this.M[m][n];
	}
	
	// Previous recursive stackoverflow traceback
//	@Override
	public void traceBackRecur(int row, int col, int pointer, StringBuilder SB_Target, StringBuilder SB_Query) {
	//stop condition	
	if (this.M[row][col]==0 && this.local || (row==0 && col==0)){
			this.targetOutput = SB_Target.reverse().toString();
			this.queryOutput = SB_Query.reverse().toString();
			return;
	}
	//case implementation
	switch (pointer){
	case diagM:
		// Match / Replace
		SB_Target.append(this.target.charAt(col-1));
		SB_Query.append(this.query.charAt(row-1));
		traceBack(row-1, col-1, pointers[row-1][col-1] ,SB_Target, SB_Query);
		break;
	case insI :		// Insertion
		SB_Target.append(this.target.charAt(col-1));
		SB_Query.append("_");
		traceBack(row, col-1, pointers[row][col-1], SB_Target, SB_Query);
		break;
	case delD :		// Deletion
		SB_Target.append("_");
		SB_Query.append(this.query.charAt(row-1));
		traceBack(row-1, col, pointers[row-1][col], SB_Target, SB_Query);
		break;
	}
	}
	
	@Override
	public void traceBack(int row, int col, int pointer, StringBuilder SB_Target, StringBuilder SB_Query) {
	//stop condition	
		while (!((this.M[row][col]==0 && this.local) || (row==0 && col==0))){
			switch (pointer){
			case diagM:
				// Match / Replace
				SB_Target.append(this.target.charAt(col-1));
				SB_Query.append(this.query.charAt(row-1));
				row--;
				col--;
				pointer = pointers[row][col];
				break;
			case insI :		// Insertion
				SB_Target.append(this.target.charAt(col-1));
				SB_Query.append("_");
				col--;
				pointer = pointers[row][col];
				break;
			case delD :		// Deletion
				SB_Target.append("_");
				SB_Query.append(this.query.charAt(row-1));
				row--;
				pointer = pointers[row][col];
				break;
			}
			
		}
		this.targetOutput = SB_Target.reverse().toString();
		this.queryOutput = SB_Query.reverse().toString();
		return;
	}
	
	//No need to use
	public void traceBack() {
		StringBuilder SB_Target = new StringBuilder();
		StringBuilder SB_Query = new StringBuilder();
		int row = this.maxRow;
		int col = this.maxCol;
		//stop condition	
		while (!((this.local && this.M[row][col]==0) || (row==0 && col==0))){
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
		return;
	}
	
}
