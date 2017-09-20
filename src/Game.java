import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Game {
	private final String PLAYER_SYMBOL;
	private final String CPU_SYMBOL;
	private String[][] board;
	private ArrayList<Move> allMoves = new ArrayList<>();
	private Scanner scanner;
	private int capacity;
	
	/**
	 * Creates a Game instance with a board of size 'size' and scanner instance to read input
	 * @param size		The size of the board
	 * @param scanner	Instance of Scanner class to read input
	 */
	public Game(int size, Scanner scanner){
	  this.capacity = size * size;
	  this.scanner = scanner; // store Scanner locally to properly close resource
	  this.PLAYER_SYMBOL = getUserSymbol(); // assign symbol to represent user symbol
	  this.CPU_SYMBOL = PLAYER_SYMBOL.equals("X") ? "O" : "X"; // assign symbol (X,O) to represent CPU moves
	  this.board = new String[size][size]; // initialize board to hold moves and empty slots
	  
	  for(int r = 0, len = size; r < len; r++){
		  for(int c = 0; c < size; c++){
			  board[r][c] = "-";
			  allMoves.add(new Move(r+1, c+1));
	    }
	  } 
	}
	
	/**	
	 * Creates a Game instance with a board of size 'size'
	 * @param size	The size of the board
	 */
	public Game(int size){
		this(size, new Scanner(System.in));
	}
	
	/**
	 * Calculates the optimal move to make based on the state of the board (Used by CPU) 
	 * 
	 * @param isPlayerTurn	Indicates who's turn it is to choose a move
	 * @param moveMade		References move made that led to the current board state
	 * @param alpha			Stores the worst possible score the CPU knows it can achieve (lower bound)
	 * @param beta			Stores the worst possible score the player knows it can achieve (upper bound)
	 * @return				Optimal move found after looking at every possible move
	 */
	private Move minimax(boolean isPlayerTurn, Move moveMade, int d, int alpha, int beta){
		int depth = d; 
		Move bestMove = new Move(-1, -1);
		String player = isPlayerTurn ? PLAYER_SYMBOL : CPU_SYMBOL;
		ArrayList<Move> possibleMoves = getPossibleMoves();		
		boolean boardIsFull = (possibleMoves.size() == 0);
		
		// Check for end conditions (board is full or winner is found)
		if (winnerFound(moveMade)){
			String winner = moveMade.getPlayer();
			int score = winner.equals(PLAYER_SYMBOL) ? -1 : 1;
//			if(score == 1)
//				System.out.println("Returning win move at depth: " + depth);
			moveMade.setScore(score);
			moveMade.setDepth(depth);
			return moveMade;
		} else if (boardIsFull){
			moveMade.setScore(0);
			moveMade.setDepth(depth);
			//System.out.println("Returning full board at depth: " + depth);
			return moveMade;
		}
					
		if(!isPlayerTurn){
			bestMove.setScore(-2);			
			
			// Iterate & test all possible moves
			for(Move m : possibleMoves){
				testMove(m, player);
				m.setPlayer(player);
				Move move = minimax(true, m, depth+1, alpha, beta);
				int moveScore = move.getScore();
				int moveDepth = move.getDepth();
				undoTestMove(m);
				
				// Test if move tested yielded a better score than our current best move
				if(moveScore > bestMove.getScore() || (moveScore == bestMove.getScore() && moveDepth < bestMove.getDepth())){
					if(depth == 1)
						System.out.println(moveScore + " is replacing " + bestMove.getScore() + " with depth " + moveDepth);
					bestMove.x = m.x;
					bestMove.y = m.y;
					bestMove.setDepth(moveDepth);
					bestMove.setScore(moveScore);
				}
				
				// Test if move tested yielded a better score than our currently best known move
				if(moveScore > alpha){
					alpha = moveScore;
				}
				
				// If alpha value is guaranteed to be worse than the parent nodes guaranteed best move, 
				// prune the rest of the moves
				if(beta <= alpha){
					break;
				}
				
				if(depth == 1){
					System.out.println("The current move " + m + " yielded a score of " + moveScore + " at depth " + moveDepth);
					System.out.println("Our best move " + bestMove + " yielded a score of " + bestMove.getScore() + " at depth " + bestMove.getDepth());
				}
			}
		} else {
			bestMove.setScore(2);
			
			// Iterate & test all possible moves
			for(Move m : possibleMoves){
				testMove(m, player);
				m.setPlayer(player);
				Move move = minimax(false, m, depth+1, alpha, beta);
				int moveScore = move.getScore();
				int moveDepth = move.getDepth();
				
				undoTestMove(m);
				
				// Test if move tested yielded a better score than our current best move
				if(moveScore < bestMove.getScore()){
					bestMove.x = m.x;
					bestMove.y = m.y;
					bestMove.setDepth(moveDepth);
					bestMove.setScore(moveScore);
				}
				
				// Test if move tested yielded a better score than our currently best known move
				if(moveScore < beta){
					beta = moveScore;
				}
				
				// If beta value is guaranteed to be worse than the parent nodes guaranteed best move,
				// prune the rest of the moves
				if(beta <= alpha){
					break;
				}
			}
		}	
		return bestMove;
	}
	
	/**
	 * Modifies the state of the board given the move made by either the user or CPU
	 * 
	 * @param move			Move to make
	 * @param isPlayerTurn	Indicates whether it's the players turn or the CPU's turn
	 */
	public void makeMove(Move move, boolean isPlayerTurn){
		int row = move.x - 1;
		int col = move.y - 1;
		
		if(isPlayerTurn){
			if(blockIsAvailable(row, col)){
				board[row][col] = PLAYER_SYMBOL;
				move.setPlayer(PLAYER_SYMBOL);
				capacity--;
				
				if(winnerFound(move)){
					System.out.println("You won!");
					displayBoard();
					scanner.close();
					return;
				} else if(boardIsFull()){
					System.out.println("It's a draw");
					displayBoard();
					scanner.close();
					return;
				} else {
					Move CPUMove = minimax(false, move, 1, -2, 2);
					makeMove(CPUMove, false);	
				}	
				
			} else {
				System.out.println("That move has already been made, try another one");
				Move newMove = askAndValidateMove();
				makeMove(newMove, true);
			}
		} else {
			board[row][col] = CPU_SYMBOL;
			move.setPlayer(CPU_SYMBOL);
			capacity--;

			if(winnerFound(move)){
				System.out.println("You lost");
				displayBoard();
				scanner.close();
				return;
			} else if (boardIsFull()){
				System.out.println("It's a draw");
				displayBoard();
				scanner.close();
				return;
			} else {
				displayBoard();
				Move userMove = askAndValidateMove();
				makeMove(userMove, true);
			}
		}
	}
	
	/* ----- Utility Methods ----- */
		
	/**	
	 * Asks the user to pick which symbol identifies them and returns it
	 * 
	 * @return symbol representing user
	 */
	private String getUserSymbol(){  
	  System.out.println("Please pick your symbol (O or X):");
	  String symbol = scanner.next();
	  while(!symbol.equals("X") && !symbol.equals("O")){
		  System.out.println("Please pick one of the two symbols: O or X");
		  symbol = scanner.next();
	  }
	  System.out.println("Your symbol is: " + symbol);
	  return symbol;
	}
	
	/**
	 * Generates a list of all the moves a user can make based on the current state of the board
	 * 
	 * @return list of possible moves player can make
	 */
	private ArrayList<Move> getPossibleMoves(){
		ArrayList<Move> moves = new ArrayList<>();
		for(Move move : allMoves){
			if(board[move.x - 1][move.y - 1] == "-")
				moves.add(move);
		}
		return moves;
	}
	
	/**
	 * Checks if move position of the move is available
	 * @param row	Row position of move
	 * @param col	Column position of move
	 * @return		Whether the location of the move is available
	 */
	public boolean blockIsAvailable(int row, int col){
		return (board[row][col].equals("-"));
	}
	
	
	/**
	 * Temporarily modifies the state of the board to test the given move
	 * 
	 * @param move			 The move to be tested
	 * @param currentPlayer	 The player making the move
	 */
	private void testMove(Move move, String currentPlayer){
		int row = move.x - 1;
		int col = move.y - 1;
	
		board[row][col] = currentPlayer;
	}
	
	/**
	 * Undoes a move by 'reseting' the state of the board to the one before testing the move
	 * 
	 * @param move	The move to undo
	 */
	public void undoTestMove(Move move){
		int row = move.x - 1;
		int col = move.y - 1;
		
		board[row][col] = "-";
	}
	
	/**
	 * Checks if there's a winner in the current board state given the move made
	 * @param move	Move made that led to current state of the board
	 * @return		Whether there is a winner or not
	 */
	private boolean winnerFound(Move move){
		String symbol = move.getPlayer();
		int currentRow = move.x - 1;
		int currentCol = move.y - 1;
		
		int row = 0;
		int col = 0;
		int diag = 0;
		int rdiag = 0;
		int n = board.length;
		
		for(int i = 0; i < n; i++){
			
			// check for full row
			if(board[currentRow][i] == symbol){
				row++;
			}
			
			// check for full column
			if(board[i][currentCol] == symbol){
				col++;
			}
			
			// check for full diagonal 
			if(board[i][i] == symbol){
				diag++;
			}
			
			// check for full reverse diagonal
			if(board[i][(n - 1)-i] == symbol){
				rdiag++;
			}
		}

		if(row == n || col == n || diag == n || rdiag == n){
			return true;
		} else {
			return false;
		}
	}
	
	/*
	 * Displays the current state of the board
	 */
	private void displayBoard(){
		
		for(int r = 0, len = board.length; r < len; r++){
		  System.out.println();
		    for(int c = 0; c < len; c++){
			  System.out.print(board[r][c] + " ");
		    }
		}
	}

	/*
	 * Asks user to write their move position, then validates if it was written correctly.
	 * If valid, return Move object. If invalid, tell them to try again.
	 */
	private Move askAndValidateMove(){
		Move userMove;
		
		System.out.println("\nPick your move. [Pattern should be 'row,column']:");
		String userInput = scanner.next();
		String[] userInputArray = userInput.split(",");
		int[] input = new int[2];
		int max = board.length;
		
		int i = 0;
		for(String s : userInputArray){
			input[i++] = Integer.parseInt(s);
		}
		boolean inputIsCommaSeparated = Pattern.matches("\\d{1},\\d{1}", userInput);
		boolean inputsIsWithinBounds = ((input[0] <= max &&  input[0] > 0) && (input[1] <= max &&  input[1] > 0));

		if(inputIsCommaSeparated && inputsIsWithinBounds){
			userMove = new Move(userInputArray[0], userInputArray[1]);
			return userMove;
		} else {
			System.out.println("Sorry, something's wrong with your input. Try again");
			userMove = askAndValidateMove();
			return userMove;
		}
	}
	
	/*
	 * Checks if the board is currently full 
	 */
	private boolean boardIsFull(){
		return capacity == 0;
	}
	
	public static void main(String[] args){
		Scanner sc = new Scanner(System.in);
		Game testBoard = new Game(3, sc);
		Move move = testBoard.askAndValidateMove();
		testBoard.makeMove(move, true);
	}
}
