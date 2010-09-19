package javiator.util;

public class ShutdownMessage implements Copyable {
	public boolean flag;
	
	public ShutdownMessage()
	{		
		this(false);		
	}
	
	public ShutdownMessage(boolean flag)
	{		
		this.flag = flag;		
	}
	
	public Object clone()
	{
		return new ShutdownMessage(flag);		
	}
	
	public ShutdownMessage deepClone()
	{
		return (ShutdownMessage)clone();
	}

	public void copyTo(Copyable copy)
	{
		((ShutdownMessage) copy).flag = flag;
	}
}
