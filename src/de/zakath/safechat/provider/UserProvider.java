package de.zakath.safechat.provider;

import java.io.FileNotFoundException;
import java.security.KeyPair;
import de.zakath.safechat.models.UserModel;
import de.zakath.simplecrypt.*;
import de.zakath.simplecrypt.RSA.VerifyResult;
import de.zakath.simplenetwork.*;
import de.zakath.simplenetwork.Message.MessageType;
import de.zakath.simplenetwork.eventargs.NewMessageEventArgs;
import de.zakath.simplenetwork.eventlistener.NewMessageListener;
import de.zakath.simplenetwork.misc.DualByteKey;

import android.content.Context;
import android.util.Log;

public class UserProvider
{

	private static int _id = -1;

	public static boolean isAccountExsisting()
	{
		initUserProvider();
		return _id != -1;
	}

	public static int getUID()
	{
		initUserProvider();
		return _id;
	}

	public static void setUpNewAccount(String username, String password)
	{

		Log.d("USER_PROVIDER_SET_UP", "Set up init...");
		// User KeyPair generieren!
		KeyPair kp = RSA.createKeyPair();
		Log.d("USER_PROVIDER_SET_UP", "Keypair created");

		// Und am server anmelden
		Message m = new Message(0, -1, MessageType.NewAccountRequest);
		Message innerm = new Message(0, -1, MessageType.NewAccountRequest);

		byte[] pswdhash = SHA1.computeHash(password.getBytes());
		DualByteKey dbk = new DualByteKey(kp.getPublic().getEncoded(),
				AES.encrypt(kp.getPrivate().getEncoded(), pswdhash));
		// Hier bei bedarf den privaten key mit verschlüsseln

		innerm.setField("username", username);
		innerm.setField("password", Hex.encode(SHA1.computeHash(pswdhash)));

		innerm.setPayload(SerializationUtils.serialize(dbk));

		// TODO Remove DEBUG
		byte[] buffer = CryptoProvider.encryptToServer(innerm.toByteArray());

		m.setPayload(buffer);

		ClientProvider.addNewMessageListener(new NewMessageListener()
		{

			@Override
			public void OnNewMessage(Object sender, NewMessageEventArgs e)
			{
				if (e.getMessage().getType() == MessageType.NewAccountResponse)
				{
					VerifyResult result = CryptoProvider.verifywithServer(e
							.getMessage().getPayload());
					if (result.isVerifyed())
					{
						Message innerm = Message.fromByteArray(CryptoProvider
								.decryptwithLocal(result.Data()));
						if (innerm.isFieldSet("Status")
								&& innerm.getField("Status").equals("Success"))
						{
							int id = e.getMessage().getTargetID();
							// Jetzt die ID speichern...

							try
							{
								SerializationUtils.serialize(
										id,
										ContextProvider.getAppContext()
												.openFileOutput("User.ID",
														Context.MODE_PRIVATE));
							} catch (FileNotFoundException ex)
							{
								ex.printStackTrace();
								// Fehler beim öffnen
							}
							// Sich selbst aus der liste löschen
							ClientProvider.removeNewMessageListener(this);
							// Und den user endgültig laden sowie die Verbindung
							// reseten
							initUserProvider();
							ClientProvider.ResetConnection();
							ClientProvider.Connect();
						} else
						{
							// Fehlerbehandlung
						}
					}
				}

			}
		});
		Log.d("USER_PROVIDER", "Response listener registered");
		
		// Und Anmeldung senden
		if (!ClientProvider.sendMessageDirect(m))
		{
			Log.e("USER_PROVIDER", "Failed to send usercreation message!");
			// TODO Irgendeine tolle fehlerbehandlung!
		}
		Log.d("USER_PROVIDER_SET_UP", "Message sent");

		// Und speicher
		KeyProvider.setOwnKey(kp);
		Log.d("USER_PROVIDER_SET_UP", "Key saved");

		
	}

	public static String getDisplayName(int id)
	{
		return UserModel.getDisplayName(id);
	}

	public static void setDisplayName(int id, String name)
	{
		UserModel.setDisplayName(id, name);
	}

	private static void initUserProvider()
	{
		try
		{
			_id = (Integer) de.zakath.simplenetwork.SerializationUtils
					.deserialize(ContextProvider.getAppContext().openFileInput(
							"User.ID"));
			Log.d("USER_PROVIDER",
					"User Account with id: " + Integer.toString(_id)
							+ " was found!");
		} catch (Exception ex)
		{
			Log.d("USER_PROVIDER", "No useraccount was found!");

		}
	}
}
