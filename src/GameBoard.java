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
	private int count = 0;

	
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
		int y = move.y - 1;
		
		if(isPlayerTurn){
			board[x][y] = playerSymbol;
			if(hasWinner(move, true)){
				System.out.println("THE PLAYER WON!");
				return;
			}
			displayBoard();
			boardFill++;
			removeMove(move);
			BestMove CPUMove = chooseMove(false, move);
			System.out.println(CPUMove);
			makeMove(CPUMove, false);
		} else {
			System.out.println("\n" + "Computer responded with move: " + move);
			board[x][y] = CPUSymbol;
			if(hasWinner(move, true)){
				System.out.println("THE COMPUTER WON!");
				return;
			}
			displayBoard();
			removeMove(move);
		    boardFill++;
			System.out.println("\n" + "Pick your next move:");
			Scanner sc = new Scanner(System.in);
			String[] userMove = sc.next().split(",");
			sc.close();
			makeMove(new Move(userMove[0], userMove[1]), true);
			
		}
	}
	
	public void testMove(Move move, boolean isPlayerTurn){
		int x = move.x - 1;
		int y = move.y - 1;
		
		if(isPlayerTurn){
			board[x][y] = playerSymbol;
			boardFill++;
		} else {
			board[x][y] = CPUSymbol;
			boardFill++;
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
			System.out.println("THERE WAS A WINNER");
			boolean lastPlayer = !isPlayerTurn;
			winningSymbol = lastPlayer ? playerSymbol : CPUSymbol;
			int score = winningSymbol == playerSymbol ? -1 : 1;
			best.score = score;
			return best;
		} else if (isFull()){
			System.out.println("ITS A TIE");
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
		System.out.println();
		System.out.println("Size of arraylist: " + size);
		
		while(index < 4){
			System.out.println("ran " + index + " times");
			Move m = possibleMoves.get(index);
			System.out.println();
			System.out.println("Trying out move: " + m);
			testMove(m, isPlayerTurn);
			++index;
			displayBoard();
			removeMove(m);
			displayMoves();
			reply = chooseMove(!isPlayerTurn, m);
			
			undoMove(m);
			
			
			if((isPlayerTurn && reply.score < best.score) || ((!isPlayerTurn) && reply.score > best.score)){
				best.x = m.x;
				best.y = m.y;
				best.score = reply.score;
				System.out.println("CHANGED SCORE TO: " + best.score);
			}
		}
		

		System.out.println("\n Best: " + best);
		return best;
	}
	
	public boolean hasWinner(Move move, boolean checkPlayerWin){
		String symbolToCheck = (checkPlayerWin == true ? playerSymbol : CPUSymbol);
		int currentX = move.x - 1;
		int currentY = move.y - 1;
		
		System.out.println();
		System.out.println("Checking for winner with symbol: " + symbolToCheck);
		
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
		return (boardFill == boardSize ? true : false);
	}
	
	public static void main(String[] args){
		GameBoard testBoard = new GameBoard(3);
	}
}
