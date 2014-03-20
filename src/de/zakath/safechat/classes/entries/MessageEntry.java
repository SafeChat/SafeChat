package de.zakath.safechat.classes.entries;

public class MessageEntry implements Comparable<MessageEntry>
{
	private String _msg;
	private int _sender;
	private int _target;
	private long _timestamp;

	private boolean _istransmitted;

	public String getMessage()
	{
		return _msg;
	}

	public int getSenderID()
	{
		return _sender;
	}

	public int getTargetID()
	{
		return _target;
	}

	public long getTimeStamp()
	{
		return _timestamp;
	}

	public boolean isTransmitted()
	{
		return _istransmitted;
	}

	public MessageEntry(String msg, int Sender, int Target, long TimeStamp,
			boolean isTransmitted)
	{
		_msg = msg;
		_sender = Sender;
		_target = Target;
		_timestamp = TimeStamp;
		_istransmitted = isTransmitted;
	}

	@Override
	public int compareTo(MessageEntry another)
	{
		return (int) (_timestamp - another.getTimeStamp());
	}
}
