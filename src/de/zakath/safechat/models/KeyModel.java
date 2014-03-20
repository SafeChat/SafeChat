package de.zakath.safechat.models;

import java.security.*;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import de.zakath.safechat.models.db.KeyDBModel;

public class KeyModel
{

	static KeyDBModel _db = null;
	private static Object _synclock = new Object();

	public static boolean isKeyPresentInDB(int id)
	{
		initDBModel();
		return _db.executeQuery(
				"SELECT * FROM " + KeyDBModel.TABLE_NAME + "WHERE "
						+ KeyDBModel.KEY_UID + " = " + Integer.toString(id)
						+ ";").getCount() > 0;

	}

	public static KeyPair getKeyfromDB(int id)
	{
		initDBModel();
		Cursor c = _db.executeQuery("SELECT * FROM " + KeyDBModel.TABLE_NAME
				+ "WHERE " + KeyDBModel.KEY_UID + " = " + Integer.toString(id)
				+ ";");
		if (c.getCount() < 1)
			return null;
		c.moveToFirst();
		return new KeyPair(
				(PublicKey) de.zakath.simplenetwork.SerializationUtils.deserialize(c
						.getBlob(2)), null);
	}

	public static void setKeyToDB(KeyPair key, int id)
	{
		initDBModel();
		SQLiteStatement s = _db.getStatement("INSERT INTO "
				+ KeyDBModel.TABLE_NAME + " (" + KeyDBModel.KEY_UID + ", "
				+ KeyDBModel.KEY_PUBLICKEY + ") VALUES ("
				+ Integer.toString(id) + ", ?);");
		s.bindBlob(1, de.zakath.simplenetwork.SerializationUtils.serialize(key
				.getPublic()));
		s.execute();
	}

	private static void initDBModel()
	{
		synchronized (_synclock)
		{
			if (_db == null)
				_db = new KeyDBModel();
		}

	}
}
