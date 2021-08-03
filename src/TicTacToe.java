package src;
import src.common.*;
import src.Gameboards.*;
import src.Players.*;

public class TicTacToe {

    //Defines the amount of training time for each MENACE training operation
    public static final int TRAINING_ROUNDS = 500;

    //This method works the logistics of training MENACE
    private static void train(Player[] players) {

        //Similarly to how we randomize the starting player in the main method, we randomize the starting player here as well
        int first  = Utils.generator.nextInt(2);
        int numberOfPlays = TRAINING_ROUNDS;

        //This loop iterates through TRAINING_ROUNDS number of games 
        while(numberOfPlays > 0) {
            TicTacToeGame game = new TicTacToeGame();
            int turn = (first++)%2;
            players[turn%2].startNewGame(CellValue.X);      //Determines who gets X
            players[(turn+1)%2].startNewGame(CellValue.O);  //Determines who gets O
            while(game.getGameState() == GameState.PLAYING) {
                players[turn%2].play(game);
                turn++;
            }
            players[0].gameFinished(game.getGameState());
            players[1].gameFinished(game.getGameState());
            numberOfPlays--;
        }
        System.out.println("player 1: " + players[0]) ;  
        System.out.println("player 2: " + players[1]) ;  
   }


    //Main of the menace application
     public static void main(String[] args) {

        //Initializing our players. 2 MENACE training sets, one computer that plays random moves, and one "perfect" computer player
        ComputerMenacePlayer menace = new ComputerMenacePlayer();
        ComputerMenacePlayer menace2 = new ComputerMenacePlayer();
        ComputerRandomPlayer random = new ComputerRandomPlayer();
        ComputerPerfectPlayer  perfect = new ComputerPerfectPlayer();

        //Additionally, we initialize a HumanPlayer to allow the user to play against MENACE
        HumanPlayer human = new HumanPlayer();

        //Array players is an array of 2 players that are going to play (Training or human games)
        Player[] players = new Player[2];

        //This is an integer that randomly determines who goes first in the first round in a MENACE vs Player game, it alternates for every game after
        int first = Utils.generator.nextInt(2);

        //We instantiate the main MENACE set to play, in every option below it is a player
        players[0] = new ComputerMenacePlayer();
        boolean stop = false;

        //User command loop
        while(!stop) {
            System.out.println("(1) Human to play menace");
            System.out.println("(2) Train Menace against perfect player");
            System.out.println("(3) Train Menace against random player");
            System.out.println("(4) Train Menace against another menace");
            System.out.println("(5) Delete (both) Menace training sets");
            System.out.println("(Q) Quit");
            String answer = Utils.console.readLine().toLowerCase();

            //Checks user command
            switch(answer){
                case "q":
                stop = true;
                break;

                case "5":
                menace = new ComputerMenacePlayer();
                players[0] = menace;
                menace2 = new ComputerMenacePlayer();
                break;

                case "4":
                players[1] = menace2;
                train(players);
                break;

                case "3":
                players[1] = random;
                train(players);
                break;

                case "2":
                players[1] = perfect;
                train(players);
                break;

                case "1":
                //Creates and operates a MENACE vs player game
                players[1] = human;

                TicTacToeGame game;
                game = new TicTacToeGame();
                int turn = (first++);
                players[turn%2].startNewGame(CellValue.X);
                players[(turn+1)%2].startNewGame(CellValue.O);
                while(game.getGameState() == GameState.PLAYING) {
                    players[turn%2].play(game);
                    turn++;
                }
                
                players[0].gameFinished(game.getGameState());
                players[1].gameFinished(game.getGameState());
                
                System.out.println(game);
                System.out.println("Result: " + game.getGameState());
                System.out.println(players[0]);
                break;

                default:
                System.out.println("Invalid Command");
            }
            
        }
    } 

}