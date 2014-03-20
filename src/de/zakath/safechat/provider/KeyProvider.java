package de.zakath.safechat.provider;

import java.io.*;
import java.security.*;
import java.security.spec.*;

import android.content.*;
import android.util.Log;
import de.zakath.simplecrypt.*;
import de.zakath.simplecrypt.RSA.VerifyResult;
import de.zakath.simplenetwork.*;
import de.zakath.simplenetwork.eventargs.*;
import de.zakath.simplenetwork.eventlistener.*;
import de.zakath.safechat.R;
import de.zakath.safechat.controller.callbacks.*;
import de.zakath.safechat.models.*;

public class KeyProvider
{

	private static KeyPair _own = null;
	private static KeyPair _server = null;

	public static KeyPair getOwnKey()
	{
		if (_own == null)
		{
			try
			{
				_own = (KeyPair) de.zakath.simplenetwork.SerializationUtils
						.deserialize(ContextProvider.getAppContext()
								.openFileInput("Local.key"));
			} catch (FileNotFoundException e)
			{
				Log.w("KEY_PROVIDER",
						"No key file was found! Apllication will exit now!");
				System.exit(-1);
			}
		}
		return _own;
	}

	public static void setOwnKey(KeyPair kp)
	{

		try
		{
			de.zakath.simplenetwork.SerializationUtils.serialize(
					kp,
					ContextProvider.getAppContext().openFileOutput("Local.key",
							Context.MODE_PRIVATE));
			_own = kp;
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}

	}

	public static void createNewOwnKey()
	{
		setOwnKey(RSA.createKeyPair());
	}

	public static void resetOwnKey()
	{
		_own = null;
	}

	public static void getKeybyUID(final int id,
			final IKeyRequestCallback callback)
	{

		if (KeyModel.isKeyPresentInDB(id))
			callback.HandleKeyRequestCallback(KeyModel.getKeyfromDB(id), id);
		else
		{
			Message m = new Message(UserProvider.getUID(), -1,
					Message.MessageType.KeyRequest);
			Message innerm = new Message(UserProvider.getUID(), -1,
					Message.MessageType.KeyRequest);
			innerm.setField("RequestID", Integer.toString(id));
			m.setPayload(CryptoProvider.encryptToServer(innerm.toByteArray()));

			ClientProvider.addNewMessageListener(new NewMessageListener()
			{

				@Override
				public void OnNewMessage(Object arg0, NewMessageEventArgs e)
				{
					if (e.getMessage().getType() == Message.MessageType.KeyResponse
							&& Integer.parseInt(e.getMessage().getField(
									"RequestID")) == id)
					{
						VerifyResult result = CryptoProvider.verifywithServer(e
								.getMessage().getPayload());
						if (result.isVerifyed())
						{
							KeyPair kp = (KeyPair) SerializationUtils
									.deserialize(CryptoProvider
											.decryptwithLocal(result.Data()));
							callback.HandleKeyRequestCallback(kp, id);
							ClientProvider.removeNewMessageListener(this);
						}
					}
				}
			});
			ClientProvider.sendMessage(m);
		}

	}

	public static KeyPair getServerKey()
	{
		if (_server == null)
		{
			InputStream is = ContextProvider.getAppContext().getResources()
					.openRawResource(R.raw.server);
			try
			{
				byte[] _buffer = new byte[is.available()];

				is.read(_buffer);

				KeyFactory keyFactory = KeyFactory.getInstance("RSA");

				X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
						_buffer);
				PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

				Log.d("KEY_PROVIDER",
						"Key MD5: "
								+ Hex.encode(MD5.computeHash(publicKey
										.getEncoded())));

				_server = new KeyPair(publicKey, null);
			} catch (Exception ex)
			{
				ex.printStackTrace();
				System.exit(-1);
			}
		}
		return _server;
	}
}
