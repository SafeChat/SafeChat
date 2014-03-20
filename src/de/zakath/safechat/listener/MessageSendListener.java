package de.zakath.safechat.listener;

import de.zakath.safechat.eventargs.MessageSendEventArgs;

public interface MessageSendListener
{

	void HandleMessageSend(Object sender, MessageSendEventArgs e);
}
