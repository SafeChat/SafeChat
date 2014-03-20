package de.zakath.safechat.listener;

import de.zakath.safechat.eventargs.MessageTransmittedEventArgs;

public interface MessageTransmittedListener
{
	void HandleMessageTransmitted(Object sender, MessageTransmittedEventArgs e);
}
