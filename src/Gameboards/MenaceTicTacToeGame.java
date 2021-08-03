package src.Gameboards;
import src.common.*;

public class MenaceTicTacToeGame extends TicTacToeGame {
	
	private int[] beads = new int[9];


	public MenaceTicTacToeGame(){
		super(3,3,3);
		initializeBeads();
	}
	

	public MenaceTicTacToeGame(MenaceTicTacToeGame base, int next){
		super(base, next);
		initializeBeads();
	}

	public int getBeadCount(int cell){
		return beads[transformedBoard[cell]];
	}

	public void setBeadCount(int cell, int newBeadCount){
		beads[transformedBoard[cell]] = newBeadCount;
	}

	private void initializeBeads(){

		int beadStart = 0;

		switch(this.getLevel()){
			case 0:
			case 1:
			case 2:
				beadStart = 8;
				break;
			case 3:
			case 4:
				beadStart = 4;
				break;
			case 5:
			case 6:
				beadStart = 2;
				break;
			case 7:
			case 8:
			case 9:
				beadStart = 1;
				break;

		}

		for(int i = 0; i < beads.length; i++){
			
			if(this.valueAt(i) == CellValue.EMPTY){
				beads[i] = beadStart;
			}
			else{
				beads[i] = 0;
			}
		}
	}

	public int pickMove(){
		int totalBeads = 0;

		for(int beadCount: beads){
			totalBeads += beadCount;
		}

		if(totalBeads <= 0){
			for(int i = 0; i<= beads.length; i++){
				if(valueAt(transformedBoard[i]) == CellValue.EMPTY){
					return i;
				}
			}
		}

		int nextMove = Utils.generator.nextInt(totalBeads) + 1;

	
		for(int i = 0; i <= beads.length; i++){
			nextMove -= beads[transformedBoard[i]];
			if(nextMove <= 0){
				return i;
			}
		}
		return beads.length-1;
	}

}