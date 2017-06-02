import java.util.ArrayList;
import java.util.Scanner;

public class GameBoard {
	private int boardSize; 
	private int boardFill = 0;
	private String playerSign;
	private String[][] board;
	private ArrayList<Move> possibleMoves = new ArrayList<>();
	
	public GameBoard(int size, String playerSign){
	  this.boardSize = size * size;
	  this.playerSign = playerSign;
	  this.board = new String[size][size];
	  
	  int count = 0;
	  
	  for(int i = 0, len = board.length; i < len; i++){
		  for(int z = 0, len2 = board[i].length; z < len2; z++){
			  board[i][z] = "-";
			  possibleMoves.add(new Move(i, z));
			  count++;
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
	
	public void makeMove(int x, int y, boolean isPlayerTurn){
		
		if(isPlayerTurn){
			board[--x][--y] = playerSign;
			boardFill++;
			displayBoard();
			BestMove CPUMove = chooseMove(false);
			makeMove(CPUMove.moveXPos, CPUMove.moveYPos, false);
		} else {
			System.out.println("\n" + "Computer responded:");
			board[--x][--y] = playerSign.equals("X") ? "O" : "X";
			boardFill++;
			displayBoard();
			System.out.println("\n" + "Pick your next move:");
			String[] userMove = new Scanner(System.in).next().split(",");
			makeMove(Integer.parseInt(userMove[0]), Integer.parseInt(userMove[1]), true);
		}
		
	}
	
	private void removeMove(int x, int y){
		int i = 0;
		System.out.println("ran");
	}
	
	public BestMove chooseMove(boolean isPlayerTurn){
		//check if there's a winner or if board is full
		BestMove best = new BestMove();
		BestMove reply; 
		
		if(isPlayerTurn){
			best.score = 2;
		} else {
			best.score = -2;
		}
		
		
		return best;
	}
	
//	public boolean hasWinner(){
//		
//	}
	
	public boolean isFull(){
		return (boardFill == boardSize ? true : false);
	}
	
	public static void main(String[] args){
	  GameBoard myBoard = new GameBoard(3);
	  myBoard.displayMoves();
	}
}
