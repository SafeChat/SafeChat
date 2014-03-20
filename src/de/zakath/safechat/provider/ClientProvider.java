package de.zakath.safechat.provider;

import java.util.*;

import android.util.Log;

import de.zakath.safechat.eventargs.MessageSendEventArgs;
import de.zakath.safechat.listener.MessageSendListener;
import de.zakath.simplecrypt.RSA.VerifyResult;
import de.zakath.simplenetwork.*;
import de.zakath.simplenetwork.Message.MessageType;
import de.zakath.simplenetwork.eventargs.ConnectionLostEventArgs;
import de.zakath.simplenetwork.eventargs.NewMessageEventArgs;
import de.zakath.simplenetwork.eventlistener.*;

public class ClientProvider implements NewMessageListener,
		ConnectionLostListener
{

	public static final String connect_ip = "192.168.111.2";

	private static BaseClient _client;
	private static ClientProvider _listener = new ClientProvider();
	private static boolean _conready = false;
	private static boolean _shouldconnected = false;
	private static boolean _accountcreationinprogess = false;
	private static boolean _isshutingdown = false;

	private static final Timer _timer;
	private static final TimerTask _timertask;

	private static List<Message> _msgcache = new ArrayList<Message>();

	static
	{
		_timer = new Timer();
		_timertask = new TimerTask()
		{

			@Override
			public void run()
			{
				if (_shouldconnected && !ClientProvider.isConnected())
				{
					Log.d("CLIENT_PROVIDER", "Trying to reconnect..");
					if (ClientProvider.Connect())
						Log.d("CLIENT_PROVIDER", "Connection succesfull");
					else
						Log.d("CLIENT_PROVIDER",
								"Connection not succesfull! Retry in ten seconds.");
				}
			}
		};
		_timer.scheduleAtFixedRate(_timertask, 1000 * 10, 1000 * 10);
	}

	private ClientProvider()
	{
	}

	public static boolean Connect()
	{
		_shouldconnected = true;
		if (_client == null)
		{

			if (UserProvider.isAccountExsisting())
			{
				_client = new BaseClient(connect_ip, 8000,
						UserProvider.getUID());
			} else
			{
				_client = new BaseClient(connect_ip, 8000, 0);
			}
		}
		if (!_client.isConnected())
		{
			boolean success = false;
			success = _client.connect();

			Log.d("CLIENT_PROVIDER", "Verbindung erfolgreich: "
					+ (success ? "TRUE" : "FALSE"));

			_client.addConnectionLostListener(_listener);
			_client.addNewMessageListener(_listener);

			// Und abwarten, ob wir akzeptiert werden, aber nur, wenn nicht doch
			// ein Account erstellt wird
			// dann kann dieser schritt übersürungen werden!
			if (!_accountcreationinprogess)
				ClientProvider
						.addNewMessageListener(_listener.new LoginHandler());

			return success;
		}
		return isConnected();
	}

	public static void ResetConnection()
	{
		_client.shutdown();
		_client.removeConnectionLostListener(_listener);
		_client.removeNewMessageListener(_listener);
		_client = null;
		_conready = false;
		_shouldconnected = false;
		_accountcreationinprogess = false;
	}

	public static void Shutdown()
	{
		if (_client != null  && !_isshutingdown)
		{
			_isshutingdown = true;
			_client.shutdown();
			_isshutingdown = false;
		}
		_shouldconnected = false;
	}

	public static boolean isConnected()
	{
		return _client != null && _client.isConnected();
	}

	public static boolean isConnectionReady()
	{
		return _conready;
	}

	public static void setisConnectionReady(boolean b)
	{
		_conready = b;
	}

	public static void sendMessage(Message m)
	{
		if (!isConnected())
			Connect();
		if (isConnected() && isConnectionReady())
		{
			_client.sendMessage(m);
			raiseMessageSend(m, new java.util.Date().getTime());
		} else
		{
			_msgcache.add(m);
		}

	}

	public static boolean sendMessageDirect(Message m)
	{
		if (!isConnected())
			Connect();
		if (isConnected())
		{
			_client.sendMessage(m);
			return true;
		} else
			return false;
	}

	private static void clearMessagesUp()
	{
		sendMessagePoll();
		sendMessageCache();
	}

	private static void sendMessagePoll()
	{
		Message m = new Message(UserProvider.getUID(), -1,
				MessageType.MessagePoll);
		ClientProvider.sendMessage(m);
	}

	private static void sendMessageCache()
	{
		for (Message m : _msgcache)
		{
			sendMessage(m);
		}
		_msgcache.clear();
	}

	public static void AccountCreationMode()
	{
		_accountcreationinprogess = true;
	}

	@Override
	public void OnConnectionLost(Object arg0, ConnectionLostEventArgs arg1)
	{
		ClientProvider.raiseConnectionLostEvent();
	}

	@Override
	public void OnNewMessage(Object arg0, NewMessageEventArgs arg1)
	{
		ClientProvider.raiseNewMessageEvent(arg1.getMessage());
	}

	private static final List<NewMessageListener> _newmsglisteners = new ArrayList<NewMessageListener>();

	public static void addNewMessageListener(NewMessageListener listener)
	{
		_newmsglisteners.add(listener);
	}

	public static void removeNewMessageListener(NewMessageListener listener)
	{
		_newmsglisteners.remove(listener);
	}

	protected static void raiseNewMessageEvent(Message m)
	{
		for (NewMessageListener l : _newmsglisteners)
		{
			l.OnNewMessage(_client, new NewMessageEventArgs(m));
		}
	}

	private static final List<ConnectionLostListener> _connectionlostlistener = new ArrayList<ConnectionLostListener>();

	public static void addConnectionLostListener(ConnectionLostListener listener)
	{
		_connectionlostlistener.add(listener);
	}

	public static void removeConnectionLostListener(
			ConnectionLostListener listener)
	{
		_connectionlostlistener.remove(listener);
	}

	protected static void raiseConnectionLostEvent()
	{
		ResetConnection();
		for (ConnectionLostListener l : _connectionlostlistener)
		{
			l.OnConnectionLost(_client, new ConnectionLostEventArgs());
		}
	}

	private static final List<MessageSendListener> _sendlistener = new ArrayList<MessageSendListener>();

	public static void addMessageSendListener(MessageSendListener listener)
	{
		_sendlistener.add(listener);
	}

	public static void removeMessageSendListener(MessageSendListener listener)
	{
		_sendlistener.remove(listener);
	}

	protected static void raiseMessageSend(Message m, long timestamp)
	{
		for (MessageSendListener l : _sendlistener)
		{
			l.HandleMessageSend(_client, new MessageSendEventArgs(m, timestamp));
		}
	}

	private class LoginHandler implements NewMessageListener
	{

		public LoginHandler()
		{

		}

		@Override
		public void OnNewMessage(Object sender, NewMessageEventArgs e)
		{
			// Die Nachrichte abfangen
			if (e.getMessage().getType() == Message.MessageType.Authinit)
			{
				VerifyResult result = CryptoProvider.verifywithServer(e
						.getMessage().getPayload());
				if (result.isVerifyed())
				{
					byte[] secret = Message.fromByteArray(result.Data())
							.getPayload();
					Message m = new Message(UserProvider.getUID(), -1,
							Message.MessageType.Auth);
					m.setPayload(CryptoProvider.encryptToServer(CryptoProvider
							.signwithLocal(secret)));

					ClientProvider.sendMessageDirect(m);
				}

			} else if (e.getMessage().getType() == Message.MessageType.Authack)
			{
				if (e.getMessage().isFieldSet("Status")
						&& e.getMessage().getField("Status").equals("Success"))
				{
					setisConnectionReady(true);
					clearMessagesUp();
					removeNewMessageListener(this);
				}

			}

		}
	}

}
