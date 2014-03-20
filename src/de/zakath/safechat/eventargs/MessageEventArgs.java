package de.zakath.safechat.eventargs;

import de.zakath.safechat.classes.entries.MessageEntry;

public class MessageEventArgs
{

	private MessageEntry _msg;

	public MessageEventArgs(MessageEntry entry)
	{
		_msg = entry;
	}

	public MessageEntry getMessageEntry()
	{
		return _msg;
	}

}
