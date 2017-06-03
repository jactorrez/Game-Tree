
public class BestMove extends Move{
	public int score;
	
	public BestMove(int x, int y, int score){
		super(x, y);
		this.score = score;
	}
	
	public BestMove(int x, int y){
		this(x, y, -5);
	}
	
	@Override
	public String toString(){
		return "[" + x + "," + y + "]" + " Score: " + score;
	}
}
