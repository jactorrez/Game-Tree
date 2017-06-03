
public class PlayerMove extends Move{
	
	public boolean isPlayer; 
	public boolean hasMove; 
	
	public PlayerMove(int x, int y, boolean isPlayer){
		super(x, y);
		this.isPlayer = isPlayer;
	}
	
	public PlayerMove(int x, int y){
		this(x, y, false);
	}
	
	public PlayerMove(){
		super(-2, -2);
		this.hasMove = false;
	}
}
