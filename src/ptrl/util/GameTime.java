package ptrl.util;

import java.io.Serializable;

public class GameTime implements Serializable
{
	public GameTime(long timestamp)
	{
		turn=0;
		ingame_timestamp=timestamp;
	}

	public void makeTurn(long t)
	{
		turn++;
		ingame_timestamp+=t;
	}
	
	public long getCurrentTimestamp()
	{
		return ingame_timestamp;
	}

	public int getCurrentTurn()
	{
		return turn;
	}	
	

	private int turn;
	private long ingame_timestamp;

}
