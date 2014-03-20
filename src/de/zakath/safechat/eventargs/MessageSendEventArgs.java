package de.zakath.safechat.eventargs;

import de.zakath.simplenetwork.Message;

public class MessageSendEventArgs
{

	private Message _m;
	private long _timestamp;
	
	
	public Message getMessage()
	{
		return _m;
	}
	public long getTimeStamp()
	{
		return _timestamp;
	}
	
	public MessageSendEventArgs(Message m, long timestamp)
	{
		
	}
	
}
