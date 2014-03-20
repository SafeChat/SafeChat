package de.zakath.safechat.models;

import android.database.Cursor;
import de.zakath.safechat.models.db.*;

public class UserModel
{
	private static UserDBModel _db;
	private static Object _synclock = new Object();

	public static String getDisplayName(int id)
	{
		initDBModel();
		Cursor c = _db.executeQuery("SELECT * FROM Usertable WHERE id = "
				+ Integer.toString(id) + ";");
		c.moveToFirst();
		if (c.getPosition() < 0)
		{
			// TODO DEBUG!
			return null;
		} else
		{
			return c.getString(2);
		}
	}

	public static void setDisplayName(int id, String name)
	{
		initDBModel();
		// Bestimmen, ob Insert oder Update...
		Cursor c = _db.executeQuery("SELECT * FROM Usertable WHERE id = "
				+ Integer.toString(id) + ";");
		c.moveToFirst();
		if (c.getPosition() < 0) // TODO DEBUG
		{ // Es gibt keinen Eintrag, also Insert
			_db.executeQuery("INSERT INTO Usertable(uid, name) VALUES ("
					+ Integer.toString(id) + ",'" + name + "');");
		} else
		// Es gibt den Eintrag schon, also Update
		{
			_db.executeQuery("UPDATE Usertable SET name = '" + name
					+ "' WHERE uid = " + Integer.toString(id) + ";");
		}

	}

	private static void initDBModel()
	{
		synchronized (_synclock)
		{
			if (_db == null)
				_db = new UserDBModel();
		}
	}

}
