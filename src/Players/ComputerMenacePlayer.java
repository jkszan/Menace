package src.Players;
import java.util.LinkedList;
import src.common.*;
import src.Gameboards.MenaceTicTacToeGame;
import src.Gameboards.TicTacToeGame;


public class ComputerMenacePlayer extends Player {


	//allGames represents a LinkedList of LinkedLists which represent all possible unique gamestates
	//Each LinkedList in allGames corresponds to another layer of depth within the game. For example, in the 1st LinkedList
	//there would be 1 or 0 cells placed depending on if it went first or second
	private LinkedList<LinkedList<MenaceTicTacToeGame>> allGames;

	private LinkedList<MenaceTicTacToeGame> gamesVisited;
	private LinkedList<Integer> movesPlayed;

	
	public ComputerMenacePlayer(){
		super();

		//Initializing allGames, gamesVisited, and movesPlayed
		allGames = new LinkedList<LinkedList<MenaceTicTacToeGame>>();
		gamesVisited = new LinkedList<MenaceTicTacToeGame>();
		movesPlayed = new LinkedList<Integer>();

		//Creating the lists for the first iteration
		allGames.add(new LinkedList<MenaceTicTacToeGame>());
		allGames.get(0).add(new MenaceTicTacToeGame());

		//Build all the other layers by adding the possible next moves to the previously built games
		//For every move (Perfect Player or their opponent)
		for(int i=1; i<= 9; i++) {
			LinkedList<MenaceTicTacToeGame> newList; 	
			newList = new LinkedList<MenaceTicTacToeGame>();	//Make a new list for the new layer
			allGames.add(newList);								//Add that list to the allGames list
			for(MenaceTicTacToeGame game: allGames.get(i-1)){	//For all games in the previous iteration
				if(game.getGameState() == GameState.PLAYING) {	//If they are still in process
					for(int j = 0; j < 9; j++) {				//Iterate through their cells
						if(game.valueAt(j) == CellValue.EMPTY) {								//If a cell is empty
							MenaceTicTacToeGame newGame = new MenaceTicTacToeGame(game,j);	//Create a new game with this as a base and that move played
							
							boolean isNew = true;										//Then, check if that game is already in the new layer
							for(MenaceTicTacToeGame existingGame: allGames.get(i)){
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
	}


	public  void play(TicTacToeGame game) {

		if(game.getLevel() == game.lines*game.columns){
			throw new IllegalArgumentException("Game is finished already!");
		}
	
		// This finds the game at the current level of depth that corresponds to the game provided, and plays the menace selected move which is determined randomly
		for(MenaceTicTacToeGame menaceGame: allGames.get(game.getLevel())){
			if(menaceGame.equalsWithSymmetry(game)){
				int movePicked = menaceGame.pickMove();
				gamesVisited.add(menaceGame);
				movesPlayed.add(movePicked);
				game.play(movePicked);
				return;
			}
		}

		//Should never reach here
		throw new IllegalStateException("Game not found: " + game);

	}

	//This method overwrites gameFinished in the Player Interface to support the MENACE learning function
	public void gameFinished(GameState result){
		super.gameFinished(result);

		if(result == GameState.PLAYING){
			throw new IllegalArgumentException("Game not finished");
		}

		if(result == GameState.DRAW){
			gameDrawn();
		}
		else if((result == GameState.XWIN && myMove == CellValue.X) || (result == GameState.OWIN && myMove == CellValue.O)){
			gameWon();
		}
		else{
			gameLost();
		}
		
	}

	//Gives each move played 3 beads as a reward for a win, making it more likely
	private void gameWon(){

		MenaceTicTacToeGame currentGame;
		Integer movePlayed;

		for(int i = 0; i < gamesVisited.size(); i++){
			currentGame = gamesVisited.get(i);
			movePlayed = movesPlayed.get(i);
			currentGame.setBeadCount(movePlayed, currentGame.getBeadCount(movePlayed) + 3);
		}

		gamesVisited.clear();
		movesPlayed.clear();
	}

	//Takes one bead away from each move played to penalize it for a loss, making it less likely to happen again
	private void gameLost(){

		MenaceTicTacToeGame currentGame;
		Integer movePlayed;

		for(int i = 0; i < gamesVisited.size(); i++){
			currentGame = gamesVisited.get(i);
			movePlayed = movesPlayed.get(i);
			currentGame.setBeadCount(movePlayed, currentGame.getBeadCount(movePlayed) - 1);
		}

		gamesVisited.clear();
		movesPlayed.clear();
	}

	//Gives one bead to each move played as a reward for a win, making it more likely to happen again
	private void gameDrawn(){

		MenaceTicTacToeGame currentGame;
		Integer movePlayed;

		for(int i = 0; i < gamesVisited.size(); i++){
			currentGame = gamesVisited.get(i);
			movePlayed = movesPlayed.get(i);
			currentGame.setBeadCount(movePlayed, currentGame.getBeadCount(movePlayed) + 1);
		}

		gamesVisited.clear();
		movesPlayed.clear();
	}

}