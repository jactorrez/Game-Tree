import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class GameBoard {
	private int boardSize; 
	private String winningSymbol;
	private String playerSymbol;
	private String CPUSymbol;
	private String[][] board;
	private ArrayList<Move> possibleMoves = new ArrayList<>();
	private Scanner scanner;
	
	public GameBoard(int size, Scanner scanner){
	  this.scanner = scanner;
	  this.boardSize = size * size;
	  this.playerSymbol = getUserSymbol();
	  this.CPUSymbol = playerSymbol.equals("X") ? "O" : "X";
	  System.out.println("CPUSymbol is: " + CPUSymbol);
	  this.board = new String[size][size];

	  for(int i = 0, len = board.length; i < len; i++){
		  for(int z = 0, len2 = board[i].length; z < len2; z++){
			  board[i][z] = "-";
			  possibleMoves.add(new Move(i+1, z+1));
	    }
	  } 
	}
	
	public GameBoard(int size){
		this(size, new Scanner(System.in));
	}
	
	private String getUserSymbol(){  
	  System.out.println("Please pick your symbol (O or X):");
	  String symbol = scanner.next();
	  System.out.println("Your symbol is: " + symbol);
	  return symbol;
	}

	public void displayBoard(){
		for(int i = 0, len = board.length; i < len; i++){
		  System.out.println();
		    for(int z = 0, len2 = board[i].length; z < len2; z++){
			  System.out.print(board[i][z] + " ");
		    }
		}
	}
	
	public void makeMove(Move move, boolean isPlayerTurn){
		int x = move.x - 1;
		int y = move.y - 1;
		
		if(isFull()){
			System.out.println("\nThe board is full, and there's no winner. It's a tie!");
			return;
		}
		
		if(isPlayerTurn){
			if(board[x][y] == "-"){
				board[x][y] = playerSymbol;
				removeMove(move);
				
				if(hasWinner(move, true)){
					displayBoard();
					System.out.println("You won!");
					scanner.close();
					return;
				}
				
				displayBoard();	
				BestMove CPUMove = chooseMove(false, move);
				makeMove(CPUMove, false);
			} else {
				System.out.println("This move has already been made, please choose another");
				Move newMove = askAndValidateMove();
				makeMove(newMove, true);
			}
		} else {
			board[x][y] = CPUSymbol;
			removeMove(move);
			
			if(hasWinner(move, false)){
				System.out.println();
				displayBoard();
				System.out.println("\nYou lost.");
				scanner.close();
				return;
			}
			
			System.out.println("\nThe computer responded:");
			displayBoard();

			Move userMove = askAndValidateMove();
			makeMove(userMove, true);
			
		}
	}
	
	public Move askAndValidateMove(){
		Move userMove;
		
		System.out.println("\nPick your move. [Pattern should be 'y,x']:");
		String userInput = scanner.next();
		String[] userInputArr = userInput.split(",");
		boolean stringMatches = Pattern.matches("\\d{1},\\d{1}", userInput);

		if(stringMatches){
			userMove = new Move(userInputArr[0], userInputArr[1]);
			return userMove;
		} else {
			System.out.println("Sorry, your input must match the pattern 'y,x'. Try again: ");
			Move fixedUserMove = askAndValidateMove();
			return fixedUserMove;
		}
	}
		
	public void testMove(Move move, boolean isPlayerTurn){
		int x = move.x - 1;
		int y = move.y - 1;
		
		if(isPlayerTurn){
			board[x][y] = playerSymbol;
		} else {
			board[x][y] = CPUSymbol;
		}
	}
	
	public void undoMove(Move move){
		int x = move.x - 1;
		int y = move.y - 1;
		
		board[x][y] = "-";
		possibleMoves.add(move);
	}
	
	private boolean removeMove(Move toRemove){
		return (possibleMoves.remove(toRemove));
	}
	
	public BestMove chooseMove(boolean isPlayerTurn, Move lastMove){
		BestMove best = new BestMove(1,1);
		BestMove reply; 
		
		if(hasWinner(lastMove, !isPlayerTurn)){
			boolean lastPlayer = !isPlayerTurn;
			winningSymbol = lastPlayer ? playerSymbol : CPUSymbol;
			int score = (winningSymbol == playerSymbol ? -1 : 1);
			best.score = score;
			return best;
		} else if (isFull()){
			best.score = 0;
			return best;
		}
				
		if(isPlayerTurn){
			best.score = 2;
		} else {
			best.score = -2;
		}
		
		int index = 0;
		int size = possibleMoves.size();
		
		while(index < size){
			Move m = possibleMoves.get(0);
			testMove(m, isPlayerTurn);
			index++;
			removeMove(m);
			reply = chooseMove(!isPlayerTurn, m);
			undoMove(m);

			if((isPlayerTurn && reply.score < best.score) || (!isPlayerTurn && best.score < reply.score)){
				best.x = m.x;
				best.y = m.y;
				best.score = reply.score;
			} 
		}
		
		return best;
	}
	
	private boolean hasWinner(Move move, boolean checkPlayerWin){
		String symbolToCheck = (checkPlayerWin == true ? playerSymbol : CPUSymbol);
		int currentX = move.x - 1;
		int currentY = move.y - 1;
		
		int row = 0;
		int col = 0;
		int diag = 0;
		int rdiag = 0;
		int n = board.length;
		
		for(int i = 0; i < board.length; i++){
			// check for full row
			if(board[currentX][i] == symbolToCheck){
				row++;
			}
			
			// check for full column
			if(board[i][currentY] == symbolToCheck){
				col++;
			}
			
			// check for full diagonal 
			if(board[i][i] == symbolToCheck){
				diag++;
			}
			
			// check for full reverse diagonal
			if(board[i][(n - 1)-i] == symbolToCheck){
				rdiag++;
			}
		}

		if(row == n || col == n || diag == n || rdiag == n){
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isFull(){
		boolean fullBoard = (boardSize - possibleMoves.size() == boardSize);
		return fullBoard;
	}
	
	public static void main(String[] args){
		Scanner sc = new Scanner(System.in);
		GameBoard testBoard = new GameBoard(3, sc);
		Move move = testBoard.askAndValidateMove();
		testBoard.makeMove(move, true);
	}
}
