
public class BestMove {
	public int score;
	public int moveXPos;
	public int moveYPos;
	
	public BestMove(int x, int y, int score){
		this.score = score;
		this.moveXPos = x;
		this.moveYPos = y;
	}
	
	public BestMove(){
		this(-2,-2,-2);
	}
}
