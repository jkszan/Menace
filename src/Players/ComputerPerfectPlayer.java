package src.Players;
import java.util.LinkedList;
import src.Gameboards.TicTacToeGame;
import src.Gameboards.PerfectTicTacToeGame;
import src.common.*;

//The theoretical perfect computer player, this is implemented via brute force checking every single game to see what the best winning chances are in each gamestate
public class ComputerPerfectPlayer extends Player {
	
	//allGames represents a LinkedList of LinkedLists which represent all possible unique gamestates
	//Each LinkedList in allGames corresponds to another layer of depth within the game. For example, in the 1st LinkedList
	//there would be 1 or 0 cells placed depending on if it went first or second
	private LinkedList<LinkedList<PerfectTicTacToeGame>> allGames;

	//Constructor
	public ComputerPerfectPlayer(){
		super();

		//Initializing allGames
		allGames = new LinkedList<LinkedList<PerfectTicTacToeGame>>();

		//Creating the lists for the first iteration
		allGames.add(new LinkedList<PerfectTicTacToeGame>());
		allGames.get(0).add(new PerfectTicTacToeGame());

		//Build all the other layers by adding the possible next moves to the previously built games
		//For every move (Perfect Player or their opponent)
		for(int i=1; i<= 9; i++) {
			LinkedList<PerfectTicTacToeGame> newList; 	
			newList = new LinkedList<PerfectTicTacToeGame>();	//Make a new list for the new layer
			allGames.add(newList);								//Add that list to the allGames list
			for(PerfectTicTacToeGame game: allGames.get(i-1)){	//For all games in the previous iteration
				if(game.getGameState() == GameState.PLAYING) {	//If they are still in process
					for(int j = 0; j < 9; j++) {				//Iterate through their cells
						if(game.valueAt(j) == CellValue.EMPTY) {								//If a cell is empty
							PerfectTicTacToeGame newGame = new PerfectTicTacToeGame(game,j);	//Create a new game with this as a base and that move played
							
							boolean isNew = true;										//Then, check if that game is already in the new layer
							for(PerfectTicTacToeGame existingGame: allGames.get(i)){
								if(newGame.equalsWithSymmetry(existingGame)){
									isNew = false;
									break;
								}
							}
							if(isNew) {
								newList.add(newGame);		//If it is not in the new layer, add it to the new layer
							}					
						}
					}
				}

			}
		}

		//Adding game outcomes

		//For every layer except the last
		for(int i=8; i>= 0; i--) {	
			for(PerfectTicTacToeGame game: allGames.get(i)){												//For every game in that layer
				if(game.getGameOutcome() == PerfectTicTacToeGame.NOT_SET) {									//If the game outcome is NOT_SET
					for(int j=0; j < 9; j++) {																//For every cell in that game
						if(game.valueAt(j) == CellValue.EMPTY) {											//If that cell is empty
							PerfectTicTacToeGame newGame = new PerfectTicTacToeGame(game,j);				//Create a new game with that cell played
					
							for(PerfectTicTacToeGame existingGame: allGames.get(i+1)){					
								if(newGame.equalsWithSymmetry(existingGame)){								//Check if the new game is equal to an existing game from the layer above

									if(existingGame.getGameOutcome() == PerfectTicTacToeGame.WIN) {			//If it is equal to a game from the layer above, reverse the outcome
										game.setMoveOutcome(j,PerfectTicTacToeGame.LOSE);
									} else if(existingGame.getGameOutcome() == PerfectTicTacToeGame.LOSE) {
										game.setMoveOutcome(j,PerfectTicTacToeGame.WIN);
									} else if(existingGame.getGameOutcome() == PerfectTicTacToeGame.DRAW) {
										game.setMoveOutcome(j,PerfectTicTacToeGame.DRAW);
									} else {
										System.out.println(existingGame);
										throw new IllegalStateException("This should not be happening");
									}
									break;
								}
							}						
						}
					}
				}

			}
		}

	}


	//play function plays every time a move needs to be decided
	public  void play(TicTacToeGame game) {

		if(game.getLevel() == game.lines*game.columns){
			throw new IllegalArgumentException("Game is finished already!");
		}
	
		// This finds the game at the current level of depth that corresponds to the game provided, and plays the calculated perfect move for it
		for(PerfectTicTacToeGame perfectGame: allGames.get(game.getLevel())){
			if(perfectGame.equalsWithSymmetry(game)){
				game.play(perfectGame.choosePerfectMove());
				return;
			}
		}

		//Should never reach here
		throw new IllegalStateException("Game not found: " + game);

	}

}