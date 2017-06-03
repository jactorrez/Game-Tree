import java.util.Scanner;

public class GameTree {
	
	public static void main(String[] args){
     	Scanner sc = new Scanner(System.in);
//		System.out.println("Are you X or O?");
//		String playerSign = sc.next();
//		System.out.println("You start first, make your first move. Good luck!");
		GameBoard game = new GameBoard(3, "X");
		
		System.out.println("Pick your first move [format should be 'x,y']:");
		String userMove = sc.next();
		String[] move = userMove.split(",");
		game.makeMove(new Move(move[0], move[1]), true);
	}
}
