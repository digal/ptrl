package ptrl.util;

import java.io.Serializable;

/**
 * Describes a simple message, including its text, time and turn number. 
 * 
 * @author Digal
 */
public class Message implements Serializable
{
	
	/**
	 * Creates a message.
	 * 
	 * @param t message text.
	 * @param trn turn number.
	 * @param ts timestamp.
	 */
	public Message(String t, int trn,  long ts)
	{
		text=t;
		turn=trn;
		timestamp=ts;
		shown=false;
	}

	/**
	 * Creates a message.
	 * 
	 * @param t message text.
	 * @param gt GameTime object to take turn and time info from.
	 */
	public Message(String t, GameTime gt)
	{
		this(t, gt.getCurrentTurn(), gt.getCurrentTimestamp());
	}
	
	/**
	 * Creates an empty message.
	 */
	public Message()
	{
		text="";
		turn=0;
		timestamp=0;
	}

	/**
	 * Creates a message clone. (See Object.clone)
	 * 
	 * @return a cloned message (of the Object type).
	 */

	public Object clone()
	{
		Message m = new Message();
		m.text=text;
		m.turn=turn;
		m.timestamp=timestamp;
		return m;
	}
	
	/**
	 * 
	 * @return message text.
	 */
	public String getText()
	{
		return text;
	}
	
	/**
	 * @return message timestamp.
	 */
	public long getTimestamp()
	{
		return timestamp;
	}

	/**
	 * @return message's ¹ of turn.
	 */
	
	public int getTurn()
	{
		return turn;
	}	
	
	public String toString()
	{
		return "["+turn+"]: "+text;
	}
	
	public String show()
	{
		shown=true;
		return text;
	}

	public String show(boolean deactivate)
	{
		if (deactivate) shown=true;
		return text;
	}
	
	public boolean isShown()
	{
		return shown;
	}
	
	private String text;
	private int turn;
	private long timestamp;
	private boolean shown;
}
