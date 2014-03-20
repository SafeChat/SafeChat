package de.zakath.safechat.provider;

import java.security.KeyPair;
import java.util.*;

import android.os.AsyncTask;

import de.zakath.safechat.classes.entries.MessageEntry;
import de.zakath.safechat.controller.callbacks.IKeyRequestCallback;
import de.zakath.safechat.eventargs.*;
import de.zakath.safechat.listener.*;
import de.zakath.safechat.models.MessageModel;
import de.zakath.simplecrypt.RSA.VerifyResult;
import de.zakath.simplenetwork.Message;
import de.zakath.simplenetwork.Message.MessageType;
import de.zakath.simplenetwork.eventargs.NewMessageEventArgs;
import de.zakath.simplenetwork.eventlistener.NewMessageListener;

public class MessageProvider implements NewMessageListener, MessageSendListener
{

	private static MessageProvider _listener;
	private static Object _synclock = new Object();

	public static void sendMessagetoUser(final String s, final int id)
	{
		KeyProvider.getKeybyUID(id, new IKeyRequestCallback()
		{

			@Override
			public void HandleKeyRequestCallback(KeyPair key, int id)
			{
				Message m = new Message(UserProvider.getUID(), id,
						MessageType.Message, CryptoProvider
								.signwithLocal(CryptoProvider.encryptToCustom(
										s.getBytes(), key)));
				m.setField("LocalID", Long.toString(MessageModel
						.addMessageEntrytoDB(new MessageEntry(s, UserProvider
								.getUID(), id, new java.util.Date().getTime(),
								false))));
				ClientProvider.sendMessage(m);
			}
		});
	}

	public static void init()
	{
		synchronized (_synclock)
		{
			if (_listener == null)
			{
				_listener = new MessageProvider();
				ClientProvider.addNewMessageListener(_listener);
				ClientProvider.addMessageSendListener(_listener);
				ClientProvider.Connect();
			} else
			{
				if (!ClientProvider.isConnected())
					ClientProvider.Connect();
			}
		}

	}

	@Override
	public void OnNewMessage(final Object sender, final NewMessageEventArgs e)
	{
		if (e.getMessage().getType() == MessageType.Message)
		{
			KeyProvider.getKeybyUID(e.getMessage().getSenderID(),
					new IKeyRequestCallback()
					{

						@Override
						public void HandleKeyRequestCallback(KeyPair key, int id)
						{
							VerifyResult result = CryptoProvider
									.verifywithCustom(e.getMessage()
											.getPayload(), key);
							if (result.isVerifyed())
							{
								String msg = new String(CryptoProvider
										.decryptwithLocal(result.Data()));
								MessageEntry entry = new MessageEntry(msg, e
										.getMessage().getSenderID(), e
										.getMessage().getTargetID(),
										new java.util.Date().getTime(), true);
								// In die Datenbankeintragen
								MessageModel.addMessageEntrytoDB(entry);

								raiseMessageReceived(new MessageReceivedEventArgs(
										entry));
							}

						}
					});
		}
	}

	@Override
	public void HandleMessageSend(Object sender, final MessageSendEventArgs e)
	{
		new AsyncTask<Void, Void, Void>()
		{

			@Override
			protected Void doInBackground(Void... arg0)
			{
				long id = Long.parseLong(e.getMessage().getField("LocalID"));
				MessageModel.changeMessageStatus(id, true);

				MessageEntry me = MessageModel.getMessageEntrybyID(id);
				raiseMessageTransmitted(new MessageTransmittedEventArgs(me));

				return null;
			}

		}.execute();

	}

	private static List<MessageReceivedListener> _receivedlistener = new ArrayList<MessageReceivedListener>();

	public static void addMessageReceivedListener(
			MessageReceivedListener listener)
	{
		_receivedlistener.add(listener);
	}

	public static void removeMessageReceivedListener(
			MessageReceivedListener listener)
	{
		_receivedlistener.remove(listener);
	}

	protected static void raiseMessageReceived(MessageReceivedEventArgs e)
	{
		for (MessageReceivedListener l : _receivedlistener)
		{
			l.HandleMessageReceived(null, e);
		}
	}

	private static List<MessageTransmittedListener> _transmitistener = new ArrayList<MessageTransmittedListener>();

	public static void addMessageTransmittedListener(
			MessageTransmittedListener listener)
	{
		_transmitistener.add(listener);
	}

	public static void removeMessageTransmittedListener(
			MessageTransmittedListener listener)
	{
		_transmitistener.remove(listener);
	}

	protected static void raiseMessageTransmitted(MessageTransmittedEventArgs e)
	{
		for (MessageTransmittedListener l : _transmitistener)
		{
			l.HandleMessageTransmitted(null, e);
		}
	}

}
