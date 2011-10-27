package ptrl.util;

import java.io.Serializable;

/**
 * Message stack is an object, that stores messages.
 * The message stack have a limited capacity (size). 
 *  
 * @author Digal
 */
public class MessageStack implements Serializable
{
	/**
	 * Creates an empty stack.
	 * 
	 * @param n - stack's capacity (max. number of messages)
	 */
	public MessageStack(int n)
	{
//		cursor=0;
		start=0;
		msgs_num=0;
		msgs=new Message[n];
	}

	/**
	 * @return maximum (not actual) number of messages (stack capacity).
	 */
	public int getMaxSize()
	{
		return msgs.length;
		
	}
	/**
	 * @return actual number of messages.
	 */
	public int getSize()
	{
		/*int size;
		if (start<=cursor) size=cursor-start;
		else size=getMaxSize()-(start-cursor)+1;
		return size;*/
		return msgs_num;
	}
	
	/**
	 * Adds a message. Message <b>clone</b> is added to stack.	
	 * 
	 * @param msg message to add.
	 */
	public void addMessage(Message msg)
	{
		if (msg==null) return;
		Message m=(Message)msg.clone();
		if (msgs_num<getMaxSize()) //we dont'need to move start position
		{
			msgs[msgs_num]=(Message)msg.clone();
		}
		else 
		{
			msgs[start]=(Message)msg.clone();
			if (start<getMaxSize()-1) start++;
			else start=0;
		}
		if (msgs_num<getMaxSize()) msgs_num++;
	}
	
	/**
	 * 
	 * @return an array of all messages storing in stack.
	 */
	public Message[] getMessages()
	{
		if (getSize()==0) return new Message[0];
		Message[] r = new Message[getSize()];
		if (getSize()<getMaxSize()) 
		{
			for (int i=0; i<getSize(); i++)
			{
				r[i]=(Message)msgs[i];
			}
		}
		else 
		{
			int cur=start;
			for (int i=0; i<getSize(); i++)
			{
				r[i]=(Message)msgs[cur].clone();
				if (cur<getMaxSize()-1) cur++;
				else cur=0;
			}
		}
		return r;

	}
	
	private Message[] msgs;
//	private int cursor;
	private int start;
	private int msgs_num;
}
