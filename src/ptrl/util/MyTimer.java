package ptrl.util;

import java.util.Calendar;

public class MyTimer
{
	public MyTimer()
	{
		t_start=0;
	}
	
	public void start()
	{
		t_start=System.currentTimeMillis();
	}
	
	public long getValue()
	{
		return System.currentTimeMillis()-t_start;
	}
	
	
	private long t_start;
	
	
}
