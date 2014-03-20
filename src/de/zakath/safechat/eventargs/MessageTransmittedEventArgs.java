package de.zakath.safechat.eventargs;

import de.zakath.safechat.classes.entries.MessageEntry;

public class MessageTransmittedEventArgs extends MessageEventArgs
{

	public MessageTransmittedEventArgs(MessageEntry entry)
	{
		super(entry);
	}

}
