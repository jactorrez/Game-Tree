
public class Move {
	public int x;
	public int y;
	private String player;
	private int score;

	public Move(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public Move(String x, String y){
		this.x = Integer.parseInt(x);
		this.y = Integer.parseInt(y);
	}

	@Override
	public boolean equals(Object toCompare){
		Move compareTest = (Move) toCompare;
		
		if(compareTest.x == this.x && compareTest.y == this.y){
			return true;
		} else {
			return false;
		}
	}
	
	@Override 
	public String toString(){
		return "Move [" + x + "," + y + "]";
	}
	
	/* ---- Accessor & update methods ---- */
	
	public void setScore(int score){
		this.score = score;
	}
	
	public int getScore(){
		return score;
	}
	
	public void setPlayer(String symbol){
		this.player = symbol;
	}
	
	public String getPlayer(){
		return player;
	}
}
