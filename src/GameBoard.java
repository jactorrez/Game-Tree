import java.util.ArrayList;
import java.util.Scanner;

public class GameBoard {
	private int boardSize; 
	private int boardFill = 0;
	private String winningSymbol;
	private String playerSymbol;
	private String CPUSymbol;
	private String[][] board;
	private ArrayList<Move> possibleMoves = new ArrayList<>();
	private boolean COMPUTER = false;
	private boolean PLAYER = false;

	
	public GameBoard(int size, String playerSymbol){
	  this.boardSize = size * size;
	  this.playerSymbol = playerSymbol;
	  this.CPUSymbol = playerSymbol == "X" ? "O" : "X";
	  this.board = new String[size][size];

	  for(int i = 0, len = board.length; i < len; i++){
		  for(int z = 0, len2 = board[i].length; z < len2; z++){
			  board[i][z] = "-";
			  possibleMoves.add(new Move(i+1, z+1));
		  }
	   }
	}
	
	public GameBoard(int size){
		this(size, "X");
	}
	
	private void displayMoves(){
		for(Move move : possibleMoves){
			System.out.println("[" + move.x + "," + move.y + "]");
		}
	}

	public void displayBoard(){
		for(int i = 0, len = board.length; i < len; i++){
			System.out.println();
		  for(int z = 0, len2 = board[i].length; z < len2; z++){
			 System.out.print(" " + board[i][z] + " ");
		  }
		 }
	}
	
	public void makeMove(Move move, boolean isPlayerTurn){
		int x = move.x - 1;
		Scanner sc = new Scanner(System.in);
		boolean fullBoard = (boardSize - possibleMoves.size() == boardSize);

		int y = move.y - 1;
		
		if(isPlayerTurn){
			if(board[x][y] == "-"){
				board[x][y] = playerSymbol;
				removeMove(move);
				if(hasWinner(move, true)){
					System.out.println("THE PLAYER WON!");
					return;
				}
				displayBoard();	
				if(fullBoard){
					System.out.println("The board is full, and there's no winner. It's a tie!");
				}
				BestMove CPUMove = chooseMove(false, move);
				makeMove(CPUMove, false);
			} else {
				System.out.println("This move has already been made, please choose another");
				String[] userNewMove = sc.next().split(",");
				makeMove(new Move(userNewMove[0], userNewMove[1]), true);
			}
			
			
		} else {
			System.out.println("\n" + "Computer responded with move: " + move);
			board[x][y] = CPUSymbol;
			removeMove(move);
			if(hasWinner(move, false)){
				System.out.println("THE COMPUTER WON!");
				return;
			}
			displayBoard();
			
			if(fullBoard){
				System.out.println("The board is full, and there's no winner. It's a tie!");
			} else{
				System.out.println("\n" + "Pick your next move:");
				String[] userMove = sc.next().split(",");
				makeMove(new Move(userMove[0], userMove[1]), true);
			}
			
			
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
		System.out.println("Before checking winner: " + isPlayerTurn);
		
		if(hasWinner(lastMove, !isPlayerTurn)){
			boolean lastPlayer = !isPlayerTurn;
			winningSymbol = lastPlayer ? playerSymbol : CPUSymbol;
			int score = winningSymbol == playerSymbol ? -1 : 1;
			System.out.println("There was a winner, so I'm returning: " + score);
			best.score = score;
			return best;
		} else if (isFull()){
			best.score = 0;
			System.out.println("There was a tie, so I'm returning: 0");
			return best;
		}
		
		System.out.println("After checking winner: " + isPlayerTurn);
		
		if(isPlayerTurn){
			best.score = 2;
			System.out.println();
			System.out.println("This is a human level");
		} else {
			best.score = -2;
			System.out.println();
			System.out.println("This is a computer level");
		}
		
		int index = 0;
		int size = possibleMoves.size();
		System.out.println("The current size of the array of moves is: " + size);
		if(!possibleMoves.isEmpty()){
			while(index < size){
				System.out.println("------------NEW MOVE---------");
				Move m = possibleMoves.get(0);
				testMove(m, isPlayerTurn);
				System.out.println();
				System.out.println("Current index is: " + index + " out of " + size + " possible moves");
				System.out.println("Displaying board-------------");
				index++;
				displayBoard();
				removeMove(m);
				System.out.println();
				displayMoves();
				reply = chooseMove(!isPlayerTurn, m);
				System.out.println("REPLY MOVE: " + reply);
				System.out.println("The current best score is " + best);
				System.out.println("Is this a computer turn? " + !isPlayerTurn);
				System.out.println("Is the reply larger than the current best? " + (reply.score > best.score));
				undoMove(m);
	
				if((isPlayerTurn && reply.score < best.score) || (!isPlayerTurn && best.score < reply.score)){
					best.x = m.x;
					best.y = m.y;
					best.score = reply.score;
					System.out.println("Reply met criteria and is: " + reply + " and current best is: " + best);
				} 
			}
		}
		System.out.println("This is a decision node, so I'm returning: " + best);
		return best;
	}
	
	public boolean hasWinner(Move move, boolean checkPlayerWin){
		System.out.println("This is what the isFull method returns: " + (isFull() ? "true" : "false"));
		
		String symbolToCheck = (checkPlayerWin == true ? playerSymbol : CPUSymbol);
		int currentX = move.x - 1;
		int currentY = move.y - 1;
		
		int row = 0;
		int col = 0;
		int diag = 0;
		int rdiag = 0;
		int n = board.length;
		
		for(int i = 0; i < board.length; i++){
			
			// check row
			if(board[currentX][i].equals(symbolToCheck)){
				row++;
			}
			
			// check col
			if(board[i][currentY].equals(symbolToCheck)){
				col++;
			}
			
			// check diagonal 
			if(board[i][i].equals(symbolToCheck)){
				diag++;
			}
			
			// check reverse diagonal
			if(board[i][(n - 1)-i].equals(symbolToCheck)){
				rdiag++;
			}
		}
		
		if(row == n || col == n || diag == n || rdiag ==n){
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isFull(){
		boolean fullBoard = (boardSize - possibleMoves.size() == boardSize);
		System.out.println("The current fill level is: " + fullBoard + " out of a total size of " + boardSize);
		return fullBoard;
	}
	
	public static void main(String[] args){
		GameBoard testBoard = new GameBoard(3);
		System.out.println("Pick your first move [format should be 'x,y']:");
		Scanner sc = new Scanner(System.in);
		String userMove = sc.next();
		String[] move = userMove.split(",");
		testBoard.makeMove(new Move(move[0], move[1]), true);
	}
}
