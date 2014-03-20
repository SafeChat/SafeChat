package de.zakath.safechat.eventargs;

import de.zakath.safechat.classes.entries.MessageEntry;

public class MessageReceivedEventArgs extends MessageEventArgs
{

	public MessageReceivedEventArgs(MessageEntry entry)
	{
		super(entry);
	}

}
