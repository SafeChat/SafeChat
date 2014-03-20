package de.zakath.safechat.listener;

import de.zakath.safechat.eventargs.MessageReceivedEventArgs;

public interface MessageReceivedListener
{
	void HandleMessageReceived(Object sender, MessageReceivedEventArgs e);
}
