
package alignments;


public class AffineAlignment extends Alignment {


	public AffineAlignment(String str2, String str1, String scoreMatFile, boolean local) {
		super(str2, str1, scoreMatFile, local);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void align() {
		//initializing basic fields
		super.align();
		System.out.println(local ? "Running local affine alignment" : "Running global affine alignment" );

		
		//farther initialization
		this.D = new int[m + 1][n + 1];
		this.I = new int[m + 1][n + 1];
		this.pointersDel = new int[m + 1][n + 1];
		this.pointersIns = new int[m + 1][n + 1];
		
		int maxC = n;
		int maxR = m;
		
		initializeAffineMatrix();
		
		// filling matrix
				for (int i = 1; i <= m; i++) {		// rows S2[i]
					for (int j = 1; j <= n; j++) {	// cols S1[j]
						// Calculating M[i][j]
						if (this.M[i-1][j-1] > this.D[i-1][j-1]) {									// M > D
							if (this.M[i-1][j-1] > this.I[i-1][j-1]) {								// M > D I
								//if Global or local with positive score
								if (!local || this.M[i-1][j-1] + this.score[this.s1[i]][this.s2[j]] > 0) {											// M > D I 0
									this.M[i][j] = this.M[i-1][j-1] + this.score[this.s1[i]][this.s2[j]];
									this.pointers[i][j] = Alignment.diagM;							
								} else {															// 0 >
									this.M[i][j] = 0;
									this.pointers[i][j] = Alignment.end;
								}
							} else if (!local || this.I[i-1][j-1] + this.score[this.s1[i]][this.s2[j]] > 0){										// I > M > D > 0
									this.M[i][j] = this.I[i-1][j-1] + this.score[this.s1[i]][this.s2[j]];
									this.pointers[i][j] = Alignment.diagI;
							} else if(local) {																// 0 >
									this.M[i][j] = 0;
									this.pointers[i][j] = Alignment.end;
							}
						} else if (this.D[i-1][j-1] > this.I[i-1][j-1]) {							// D > M I
								if (!local || this.D[i-1][j-1] + this.score[this.s1[i]][this.s2[j]] > 0) {											// D > M I 0
									this.M[i][j] = this.D[i-1][j-1] + this.score[this.s1[i]][this.s2[j]];
									this.pointers[i][j] = Alignment.diagD;							
								} else if(local) {															// 0 >
									this.M[i][j] = 0;
									this.pointers[i][j] = Alignment.end;							
								}
						} else if (!local || this.I[i-1][j-1] + this.score[this.s1[i]][this.s2[j]] > 0) {											// I > D > M 0
								this.M[i][j] = this.I[i-1][j-1] + this.score[this.s1[i]][this.s2[j]];
								this.pointers[i][j] = Alignment.diagI;
						} else if (local){																	// 0 >
							this.M[i][j] = 0;
							this.pointers[i][j] = Alignment.end;											
						}
						
						if (local && this.M[i][j] > this.finalScore) {
							this.finalScore = this.M[i][j];
							maxR = i;
							maxC = j;
						}

						// Calculating D[i][j]
						if (this.M[i - 1][j] - this.gapA > this.D[i - 1][j]) { //if a new gap has started
							this.D[i][j] = this.M[i - 1][j] - (this.gapA + this.gapB);
							this.pointersDel[i][j] = Alignment.delM;
						} else {
							this.D[i][j] = this.D[i - 1][j] - this.gapB;
							this.pointersDel[i][j] = Alignment.delD;
						}

						// Calculating I[i][j]
						if (this.M[i][j - 1] - this.gapA > this.I[i][j - 1]) { //if a new gap has started
							this.I[i][j] = this.M[i][j - 1] - (this.gapA + this.gapB);
							this.pointersIns[i][j] = Alignment.insM;
						} else {
							this.I[i][j] = this.I[i][j - 1] - this.gapB;
							this.pointersIns[i][j] = Alignment.insI;
						}

					}
				}

				// Trace back
				if (local){
					traceBack(maxR, maxC, this.pointers[maxR][maxC], new StringBuilder(), new StringBuilder());
					return;
				}
				//if global
				if (this.M[m][n] > this.D[m][n]) {
					if (this.M[m][n] > this.I[m][n]) {
						this.finalScore = this.M[m][n];
						traceBack(m, n,  this.pointers[m][n], new StringBuilder(n), new StringBuilder(m));
					} else {
						this.finalScore = this.I[m][n];
						traceBack(m, n,  this.pointersIns[m][n], new StringBuilder(n), new StringBuilder(m));
					}
				} else if (this.D[m][n] > this.I[m][n]) {
					this.finalScore = this.D[m][n];
					traceBack(m, n,  this.pointersDel[m][n], new StringBuilder(n), new StringBuilder(m));
				} else {
					this.finalScore = this.I[m][n];
					traceBack(m, n,  this.pointersIns[m][n], new StringBuilder(n), new StringBuilder(m));
				}
				
			}

	
	protected void initializeAffineMatrix() {
		if (this.local){
			for (int i = 0; i <= n; i++) { // base row
				this.M[0][i] = 0;
				this.pointers[0][i] = end;
				this.D[0][i] = this.minInf;
				this.I[0][i] = this.minInf;
			}
			for (int i = 1; i <= m; i++) {	// base col
				this.M[i][0] = 0;
				this.pointers[i][0] = end;
				this.D[i][0] = this.minInf;
				this.I[i][0] = this.minInf;
			}
		}
		else{
			this.M[0][0] = 0;
			this.D[0][0] = -this.gapA;
			this.I[0][0] = -this.gapA;
			
			for (int i = 1; i <= n; i++) {	// base row
				this.M[0][i] = this.minInf;
				this.D[0][i] = this.minInf;
				this.I[0][i] = -(this.gapA + (this.gapB * i));
				this.pointersIns[0][i] = insI;
			}
			for (int i = 1; i <= m; i++) {	// base col
				this.M[i][0] = this.minInf;
				this.D[i][0] = -(this.gapA + (this.gapB * i));
				this.pointersDel[i][0] = delD;
				this.I[i][0] = this.minInf;
			}
			
		}
		
	}

	// Previous recursive stackoverflow traceback
//	@Override
	public void traceBackRecur(int row, int col, int pointer, StringBuilder SB_Target,
			StringBuilder SB_Query) {
		if (this.M[row][col]==0 && this.local || (row==0 && col==0)){
			this.targetOutput = SB_Target.reverse().toString();
			this.queryOutput = SB_Query.reverse().toString();
			return;
		}
		switch (pointer) {
		case end: //for local only
			targetOutput = SB_Target.reverse().toString();
			queryOutput = SB_Query.reverse().toString();
			return;
			// Match / Replace
		case diagM :
			SB_Target.append(this.target.charAt(col-1));
			SB_Query.append(this.query.charAt(row-1));
			traceBack(row-1, col-1, this.pointers[row-1][col-1], SB_Target, SB_Query);
			break;
		case diagI :
			SB_Target.append(this.target.charAt(col-1));
			SB_Query.append(this.query.charAt(row-1));
			traceBack(row-1, col-1, this.pointersIns[row-1][col-1], SB_Target, SB_Query);
			break;
		case diagD :
			SB_Target.append(this.target.charAt(col-1));
			SB_Query.append(this.query.charAt(row-1));
			traceBack(row-1, col-1, this.pointersDel[row-1][col-1], SB_Target, SB_Query);
			break;
			// Insertion
		case insM :
			SB_Target.append(this.target.charAt(col-1));
			SB_Query.append("_");
			traceBack(row, col-1,this.pointers[row][col-1],  SB_Target, SB_Query);
			break;
		case insI :
			SB_Target.append(this.target.charAt(col-1));
			SB_Query.append("_");
			traceBack(row, col-1,this.pointersIns[row][col-1], SB_Target, SB_Query);
			break;
			// Deletion
		case delM :
			SB_Target.append("_");
			SB_Query.append(this.query.charAt(row-1));
			traceBack(row-1, col, this.pointers[row-1][col], SB_Target, SB_Query);
			break;
		case delD :
			SB_Target.append("_");
			SB_Query.append(this.query.charAt(row-1));
			traceBack(row-1, col, this.pointersDel[row-1][col], SB_Target, SB_Query);
			break;
		}
	}
	
	@Override
	public void traceBack(int row, int col, int pointer, StringBuilder SB_Target,
			StringBuilder SB_Query) {
		while (!((this.M[row][col]==0 && this.local) || (row==0 && col==0))){
			switch (pointer) {
			case end: //for local only
				targetOutput = SB_Target.reverse().toString();
				queryOutput = SB_Query.reverse().toString();
				return;
				// Match / Replace
			case diagM :
				SB_Target.append(this.target.charAt(col-1));
				SB_Query.append(this.query.charAt(row-1));
				row--;
				col--;
				pointer = this.pointers[row][col];
				break;
			case diagI :
				SB_Target.append(this.target.charAt(col-1));
				SB_Query.append(this.query.charAt(row-1));
				row--;
				col--;
				pointer = this.pointersIns[row][col];
				break;
			case diagD :
				SB_Target.append(this.target.charAt(col-1));
				SB_Query.append(this.query.charAt(row-1));
				row--;
				col--;
				pointer = this.pointersDel[row][col];
				break;
				// Insertion
			case insM :
				SB_Target.append(this.target.charAt(col-1));
				SB_Query.append("_");
				col--;
				pointer = this.pointers[row][col];
				break;
			case insI :
				SB_Target.append(this.target.charAt(col-1));
				SB_Query.append("_");
				col--;
				pointer = this.pointersIns[row][col];
				break;
				// Deletion
			case delM :
				SB_Target.append("_");
				SB_Query.append(this.query.charAt(row-1));
				row--;
				pointer = this.pointers[row][col];
				break;
			case delD :
				SB_Target.append("_");
				SB_Query.append(this.query.charAt(row-1));
				row--;
				pointer = this.pointersDel[row][col];
				break;
			}
		}
		this.targetOutput = SB_Target.reverse().toString();
		this.queryOutput = SB_Query.reverse().toString();
		return;
	}
	
	public void traceBack() {
		
	}
}

	

