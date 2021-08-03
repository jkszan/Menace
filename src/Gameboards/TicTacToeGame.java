/**
 * The class TicTacToeGame contains the grid and tracks its progress.
 * It automatically maintains the current state of
 * the game as players are making moves.
 *
 * @author Guy-Vincent Jourdan, University of Ottawa
 * @author Jacob Kszan
 * @author Sebastian Larrivee
 */

package src.Gameboards;
import src.common.*;

public class TicTacToeGame {

	//The game board, stored in an array
	 private CellValue[] board;
 
 
	//Records the number of rounds played so far, starting at 0
	 private int level;
 
	//Records the current state of the game using the GameState enum
	 private GameState gameState;
 
 
	//Lists the number of rows in the grid (In this MENACE implementation, it is usually 3)
	 public final int lines;
 
	//Lists the number of columns in the grid (In this MENACE implementation, it is usually 3)
	 public final int columns;
 
 
	//Determines the amount of X's or O's that need to be in a row to win, this is usually 3
	 public final int sizeWin;
 
 
 
	 /**
	  * transformedBoard is used to iterate through all
	  * game symetries. We use an indirection, so as to not
	  * modify the instance variable board. In the
	  * current symmetry the cell at index i is accessed
	  * via board[transformedBoard[i]]
	  */
	 protected int[] transformedBoard;
 
	 //Specifies maximum allowable symmetry depth.
	 private int MAX_SYMMETRY_DEPTH;
 
	 //Variable to keep track of symmetry depth.
	 private int symmetryDepth;
 
 
	
	 //Default constructor, for a game of 3x3, which must align 3 cells
	 public TicTacToeGame(){
		 this(3,3,3);
	 }
 
	//Constructor that allows definition of the number of rows and columns
	 public TicTacToeGame(int lines, int columns){
		 this(lines, columns, 3);
	 }
 
	//Constructor that allows definition of the number of rows, columns, and the number of X's or O's needed to win
	 public TicTacToeGame(int lines, int columns, int sizeWin){
		 this.lines = lines;
		 this.columns = columns;
		 this.sizeWin = sizeWin;
		 board = new CellValue[lines*columns];
		 for(int i = 0; i < lines*columns ; i ++) {
			 board[i] = CellValue.EMPTY;
		 }
		 level = 0;
		 gameState = GameState.PLAYING;
 
		 //Creates int array of required size.
		 transformedBoard = new int[lines*columns];
 
		 //Fills in transformedBoard with default values.
		 this.reset();
 
		 //Sets maximum symmetry depth depending on game board shape. Essentially, this distinction exists to widen the parameters
		 //of symmetry checking in square boards, as you can rotate them to find extra equivalent boards
		 if(lines == columns){
			 MAX_SYMMETRY_DEPTH = 8;
		 } else {
			 MAX_SYMMETRY_DEPTH = 4;
		 }
	 }
 
 
	/**
	 * constructor allowing to create a new game based
	 * on an exisiting game, on which one move is added.
	 * The resulting new instance is a deep copy of
	 * the game reference passed as parameter.
	 * @param base
	 *  the TicTacToeGame instance on which this new game
	 *  is based
	 * @param next
	 *  the index of the next move.
	*/
 
	 public TicTacToeGame(TicTacToeGame base, int next){
 
		 lines = base.lines;
		 columns = base.columns;
		 sizeWin = base.sizeWin;
 
		 //If the next attribute doesn't correspond to an index within the scope of the board
		 if(next < 0 || next >= lines*columns){
			 throw new IllegalArgumentException("Illegal position: " + next);
		 }
 
		 //If the suggested next position is already occupied by an X or O
		 if(base.board[next] != CellValue.EMPTY) {
			 throw new IllegalArgumentException("CellValue not empty: " + next + " in game " + base);
		 }
 
		 //Creating a new board that is a deep copy of the old board
		 board = new CellValue[lines*columns];
		 for(int i = 0; i < lines*columns ; i ++) {
			 board[i] = base.board[i];
		 }
 
		 //Incrementing level
		 level = base.level+1;
 
		 //Performing next move
		 board[next] = base.nextCellValue();
 
		 //This updates the GameState if the game is still ongoing. If a player continues to put down X's or O's after
		 //the game is over we allow them to continue but we keep the same GameState value
		 if(base.gameState == GameState.PLAYING) {
			 setGameState(next);
		 }
		 else{
		 	gameState = base.gameState;
		 }
 
		 //Creates new integer array of required size.
		 this.transformedBoard = new int[base.lines * base.columns];
 
		 //Copies over maximum symmetry depth from other instance of TicTacToeGame.
		 this.MAX_SYMMETRY_DEPTH = base.MAX_SYMMETRY_DEPTH;
 
		 //Sets symmetryDepth and transformedBoard to default values.
		 this.reset();
	 }
 
 

	 //A comparator for the TicTacToeGame object
	 public boolean equals(Object o) {
		
		//If o is null 
		if(o == null) {
			 return false;
		 }

		 //If o is not an instance of TicTacToeGame
		 if(getClass() != o.getClass()){
			 return false;
		 }
 
		 //Cast o to TicTacToeGame
		 TicTacToeGame other = (TicTacToeGame)o;
 
		 //Check all the attributes of the games to see if they are equal
		 if((level != other.level) 	||
			 (lines != other.lines) 	||
			 (columns != other.columns)||
			 (sizeWin != other.sizeWin)){
			 return false;
		 }
		 for(int i = 0; i < board.length ; i++ ) {
			 if(board[i]!= other.board[i]) {
				 return false;
			 }
		 }
		 return true;
	 }
 
	/**
	 * getter for the variable level
	 * @return
	 * 	the value of level
	 */
	 public int getLevel(){
		 return level;
	 }
 
 
	/**
	 * getter for the variable gameState
	 * @return
	 * 	the value of gameState
	 */
	 public GameState getGameState(){
		 return gameState;
	 }
 
	/**
	 * returns the cellValue that is expected next,
	 * in other word, which played (X or O) should
	 * play next.
	 * This method does not modify the state of the
	 * game.
	 * @return
	 *  the value of the enum CellValue corresponding
	 * to the next expected value.
	   */
	 public CellValue nextCellValue(){
		 return (level%2 == 0) ? CellValue.X : CellValue.O;
	 }
 
	/**
	 * returns the value  of the cell at
	 * index i.
	 * If the index is invalid, an error message is
	 * printed out. The behaviour is then unspecified
		* @param i
	 *  the index of the cell in the array board
	 * @return
	 *  the value at index i in the variable board.
	   */
	 public CellValue valueAt(int i) {
 
		 if(i < 0 || i >= lines*columns){
			 throw new IllegalArgumentException("Illegal position: " + i);
		 }
 
		 return board[i];
	 }
 
	/**
	 * This method is call by the next player to play
	 * at the cell  at index i.
	 * If the index is invalid, an error message is
	 * printed out. The behaviour is then unspecified
	 * If the chosen cell is not empty, an error message is
	 * printed out. The behaviour is then unspecified
	 * If the move is valide, the board is updated, as well
	 * as the state of the game.
	 * To faciliate testing, is is acceptable to keep playing
	 * after a game is already won. If that is the case, the
	 * a message should be printed out and the move recorded.
	 * the  winner of the game is the player who won first
		* @param i
	 *  the index of the cell in the array board that has been
	 * selected by the next player
	   */
	 public void play(int i) {
 
		 if(i < 0 || i >= lines*columns){
			 throw new IllegalArgumentException("Illegal position: " + i);
		 }
		 if(board[i] != CellValue.EMPTY) {
			 throw new IllegalArgumentException("CellValue not empty: " + i + " in game " + toString());
		 }
 
		 board[i] = nextCellValue();
		 level++;
		 if(gameState == GameState.PLAYING) {
			 setGameState(i);
		 }
	 }
 
 
	/**
	 * A helper method which updates the gameState variable
	 * correctly after the cell at index i was just set.
	 * The method assumes that prior to setting the cell
	 * at index i, the gameState variable was correctly set.
	 * it also assumes that it is only called if the game was
	 * not already finished when the cell at index i was played
	 * (the the game was playing). Therefore, it only needs to
	 * check if playing at index i has concluded the game
	 *
		* @param i
	 *  the index of the cell in the array board that has just
	 * been set
	   */
 
 
	 private void setGameState(int index){
 
		 int left = Math.min(sizeWin-1,index%columns);
		 int right= Math.min(sizeWin-1,columns - (index%columns +1));
		 if( (countConsecutive(index-1, left,-1,board[index]) +
				 countConsecutive(index+1, right,1,board[index]))
			 >= sizeWin-1 ) {
			 setGameState(board[index]);
			 return;
		 }
 
 
 
		 int up 	= Math.min(sizeWin-1,index/columns);
		 int down= Math.min(sizeWin-1, lines - (index/columns +1));
		 if( (countConsecutive(index-columns, up,-columns,board[index]) +
				 countConsecutive(index+columns, down,columns,board[index]))
			 >= sizeWin-1 ) {
			 setGameState(board[index]);
			 return;
		 }
 
		 int upLeft = Math.min(up, left);
		 int downRight= Math.min(down, right);
		 if( (countConsecutive(index-(columns+1), upLeft,-(columns+1),board[index]) +
				 countConsecutive(index+(columns+1), downRight,columns+1,board[index]))
			 >= sizeWin-1 ) {
			 setGameState(board[index]);
			 return;
		 }
 
		 int upRight= Math.min(up, right);
		 int downLeft = Math.min(down, left);
		 if( (countConsecutive(index-(columns-1), upRight,-(columns-1),board[index]) +
				 countConsecutive(index+(columns-1), downLeft,columns-1,board[index]))
			 >= sizeWin-1 ) {
			 setGameState(board[index]);
			 return;
		 }
 
 
		 if (level == lines*columns) {
			 gameState = GameState.DRAW;
		 } else {
			 gameState = GameState.PLAYING;
		 }
 
	 }
 
 
	 //Helper method that counts the consecutive X's and O's on the board to determine if a player has won
	 private int countConsecutive(int startingPosition, int numberOfSteps,
		 int stepGap, CellValue value){
 
		 int result= 0;
		 for(int i = 0; i < numberOfSteps;i++){
			 if(board[startingPosition + i*stepGap] != value)
				 break;
			 result++;
		 }
		 return result;
 
	 }
 
	 //Helper method to set the GameState value
	 private void setGameState(CellValue value){
		 switch(value){
			 case X:
				 gameState = GameState.XWIN;
				 break;
			 case O:
				 gameState = GameState.OWIN;
				 break;
			 default:
				 throw new IllegalArgumentException("cannot set Game State to value " + value);
		 }
	 }
 
 
	/**
	 * Returns a String representation of the game
		* @return
	 *  String representation of the game
	   */
 
	 public String toString(){
		 String res = "";
		 for(int i = 0; i < lines ; i++){
			 if(i>0) {
				 for(int j = 0; j < 4*columns - 1; j++){
					 res+="-";
				 }
				 res+= Utils.NEW_LINE;
			 }
			 for(int j = 0; j < columns ; j++){
				 switch(board[i*columns + j]){
					 case X:
						 res+= " X ";
						 break;
					 case O:
						 res+= " O ";
						 break;
					 default:
						 res+=  "   ";
				 }
				 if(j<columns - 1){
					 res += "|";
				 } else{
					 res += Utils.NEW_LINE;
				 }
			 }
		 }
		 return res ;
 
	 }
 
 
	 //Resets the symmetry values and clears the transformedBoard
	 public void reset(){
			 //Sets the symmetry depth to default.
			 symmetryDepth = 0;
			 //sets the transformedBoard array back to default values.
			 for(int i = 0; i < transformedBoard.length; i++){
				 transformedBoard[i] = i;
			 }
 
	 }
 
	 /**
	  * checks if there are more symmetries to go through
	  *
	  * @return
	  *   true iff there are additional symmetries
	  */
	 public boolean hasNext(){
			 //checks to see if maximum symmetry depth has been hit.
			 return symmetryDepth < MAX_SYMMETRY_DEPTH;
	 }
 
	 /**
	  * Computes the next symmetries and stores it in
	  * the array "transform".
	  * Requires that this.hasNext() == true
	  */
	 public void next(){
			 //2 cases depending on game board shape.
			 //This one will apply required symmetry operations on an nxm board.
			 if(MAX_SYMMETRY_DEPTH == 4){
				 switch(symmetryDepth){
 
					 case 0: //ID
									 symmetryDepth++;
									 break;
					 case 1: //Horizontal Symmetry
									 Utils.horizontalFlip(this.lines, this.columns, this.transformedBoard);
									 symmetryDepth++;
									 break;
					 case 2: //Vertical Symmetry
									 Utils.verticalFlip(this.lines, this.columns, this.transformedBoard);
									 symmetryDepth++;
									 break;
					 case 3: //Horizontal Symmetry
									 Utils.horizontalFlip(this.lines, this.columns, this.transformedBoard);
									 symmetryDepth++;
									 break;
					 case 4: //Error
									 throw new IllegalStateException();
					 default: //Error
									 throw new IllegalStateException();
				 }
				 //And this one will apply required symmetry operations on an nxn board.
			 } else if(MAX_SYMMETRY_DEPTH == 8){
				 switch(symmetryDepth){
 
					 case 0: //ID
									 symmetryDepth++;
									 break;
					 case 1: //Rotate
									 Utils.rotate(this.lines, this.columns, this.transformedBoard);
									 symmetryDepth++;
									 break;
					 case 2: //Rotate
									 Utils.rotate(this.lines, this.columns, this.transformedBoard);
									 symmetryDepth++;
									 break;
					 case 3: //Rotate
									 Utils.rotate(this.lines, this.columns, this.transformedBoard);
									 symmetryDepth++;
									 break;
					 case 4: //Horizontal Symmetry
									 Utils.horizontalFlip(this.lines, this.columns, this.transformedBoard);
									 symmetryDepth++;
									 break;
					 case 5: //Rotate
									 Utils.rotate(this.lines, this.columns, this.transformedBoard);
									 symmetryDepth++;
									 break;
					 case 6: //Rotate
									 Utils.rotate(this.lines, this.columns, this.transformedBoard);
									 symmetryDepth++;
									 break;
					 case 7: //Rotate
									 Utils.rotate(this.lines, this.columns, this.transformedBoard);
									 symmetryDepth++;
									 break;
					 case 8: //Error
									 throw new IllegalStateException();
					 default: //Error
									 throw new IllegalStateException();
				 }
			 }
 
	 }
 
 
 
   /**
	 * Compares this instance of the game with the
	 * instance passed as parameter. Return true
	 * if and only if the two instance represent
	 * the same state of the game, up to symmetry.
		* @param other
	 *  the TicTacToeGame instance to be compared with this one
	   */
	   public boolean equalsWithSymmetry(TicTacToeGame other){
			 //Checks to see if other is not null.
			 if(other == null){
				 return false;
				 //Checks to see if parameter of game are equal.
			 } else if((this.lines != other.lines) ||
			   (this.columns != other.columns) ||
				  (this.level != other.level) ||
				  (this.sizeWin != other.sizeWin) ||
				 (this.MAX_SYMMETRY_DEPTH != other.MAX_SYMMETRY_DEPTH)){
				 return false;
			 }
 
			 //Resets transformedBoard back to default values.
			 this.reset();
 
			 //Creates variables used in the comparison of 2 boards.
			 int equalCellCount, index;
 
			 //Compares all of the symmetries of the current board against the
			 //other board being compared.
			 while(this.hasNext()){
				 //Increments the symmetry depth by 1. Catches an error if one is
				 //thrown.
				 try{
					 this.next();
				 } catch(IllegalStateException e){
					 return false;
				 }
				 //Checks each cell to see if they match.
				 equalCellCount = 0;
				 for(index = 0; index < (this.lines * this.columns); index++){
					 if(this.board[transformedBoard[index]] == other.board[index]){
						 equalCellCount++;
					 }
				 }
				 //Checks to see if all of the cells matched, if so then they match
				 //with symmetry and true is returned.
				 if(equalCellCount == this.lines * this.columns){
					 return true;
				 }
 
			 }
			 //If the method makes it here, then the boards are not equals to each other
			 //using symmetry.
			 return false;
		 }
 
	  /**
	 * Returns a String representation of the game as currently trasnsformed
	 *
		* @return
	 *  String representation of the game
	   */
 
	 public String toStringTransformed(){
		 if(transformedBoard == null) {
			 throw new NullPointerException("transformedBoard not initialized");
		 }
 
		 String res = "";
		 for(int i = 0; i < lines ; i++){
			 if(i>0) {
				 for(int j = 0; j < 4*columns - 1; j++){
					 res+="-";
				 }
				 res+= Utils.NEW_LINE;
			 }
			 for(int j = 0; j < columns ; j++){
				 switch(board[transformedBoard[i*columns + j]]){
					 case X:
						 res+= " X ";
						 break;
					 case O:
						 res+= " O ";
						 break;
					 default:
						 res+=  "   ";
				 }
				 if(j<columns - 1){
					 res += "|";
				 } else{
					 res += Utils.NEW_LINE;
				 }
			 }
		 }
		 return res ;
 
	 }
 }