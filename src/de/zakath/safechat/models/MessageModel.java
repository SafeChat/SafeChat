package de.zakath.safechat.models;

import java.util.*;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import de.zakath.safechat.classes.entries.MessageEntry;
import de.zakath.safechat.models.db.MessageDBModel;
import de.zakath.safechat.provider.UserProvider;

public class MessageModel
{

	private static MessageDBModel _db = null;
	private static Object _synclock = new Object();

	public static long addMessageEntrytoDB(MessageEntry entry)
	{
		initDBModel();
		int remoteid = entry.getSenderID() == UserProvider.getUID() ? entry
				.getTargetID() : entry.getSenderID();
		boolean ownmessage = entry.getSenderID() == UserProvider.getUID();
		String query = "INSERT INTO "
				+ MessageDBModel.TABLE_NAME
				+ "(msg, remoteid, ownmessage, timestamp, transmittstatus) VALUES ('"
				+ entry.getMessage() + "', " + Integer.toString(remoteid)
				+ ", " + (ownmessage ? "TRUE" : "FALSE") + ", ?, "
				+ (entry.isTransmitted() ? "TRUE" : "FALSE") + ");";
		SQLiteStatement s = _db.getStatement(query);
		s.bindLong(1, entry.getTimeStamp());
		return s.executeInsert();
	}

	public static void changeMessageStatus(long id, boolean transmitted)
	{
		initDBModel();
		String query = "UPDATE " + MessageDBModel.TABLE_NAME + " SET "
				+ MessageDBModel.KEY_TRANSMITTSTATUS + "="
				+ (transmitted ? "TRUE" : "FALSE") + ", "
				+ MessageDBModel.KEY_TIMESTAMP + "="
				+ Long.toString(new java.util.Date().getTime()) + " "
				+ "WHERE " + MessageDBModel.KEY_ID + "=" + Long.toString(id)
				+ ";";
		_db.executeSQL(query);
	}

	public static MessageEntry[] getMessageEntriesfromandtoID(int id)
	{
		initDBModel();
		Cursor c = _db
				.executeQuery("SELECT * FROM Messagetable WHERE remoteid = "
						+ Integer.toString(id) + ";");
		List<MessageEntry> _entries = new ArrayList<MessageEntry>();

		c.moveToFirst();
		while (!c.isAfterLast())
		{
			// TODO Debug hier
			boolean ownmsg = c.getInt(3) == 0 ? false : true;
			int senderid = ownmsg ? UserProvider.getUID() : c.getInt(2);
			int targetid = ownmsg ? c.getInt(2) : UserProvider.getUID();
			_entries.add(new MessageEntry(c.getString(1), senderid, targetid, c
					.getLong(4), c.getInt(4) == 0 ? false : true));
			c.moveToNext();
		}
		return _entries.toArray(new MessageEntry[_entries.size()]);
	}

	public static MessageEntry getMessageEntrybyID(long id)
	{
		initDBModel();
		Cursor c = _db.executeQuery("SELECT * FROM Messagetable WHERE id = "
				+ Long.toString(id) + ";");
		c.moveToFirst();
		boolean ownmsg = c.getInt(3) == 0 ? false : true;
		int senderid = ownmsg ? UserProvider.getUID() : c.getInt(2);
		int targetid = ownmsg ? c.getInt(2) : UserProvider.getUID();
		return new MessageEntry(c.getString(1), senderid, targetid,
				c.getLong(4), c.getInt(4) == 0 ? false : true);
	}

	public static MessageEntry[] getLatestEntries()
	{
		initDBModel();
		String firstquery = "SELECT DISTINCT remoteid FROM Messagetable";
		Cursor firstc = _db.executeQuery(firstquery);
		List<MessageEntry> _msgs = new ArrayList<MessageEntry>();

		firstc.moveToFirst();
		if (firstc.getCount() > 0)
		{
			while (!firstc.isAfterLast())
			{
				String query = "SELECT * FROM Mesagetable WHERE remoteid = "
						+ Integer.toString(firstc.getInt(2))
						+ " LIMIT 1 ORDER BY timestamp DESC;";
				Cursor c = _db.executeQuery(query);
				c.moveToFirst();

				boolean ownmsg = c.getInt(3) == 0 ? false : true;
				int senderid = ownmsg ? UserProvider.getUID() : c.getInt(2);
				int targetid = ownmsg ? c.getInt(2) : UserProvider.getUID();
				_msgs.add(new MessageEntry(c.getString(1), senderid, targetid,
						c.getLong(4), c.getInt(4) == 0 ? false : true));

				c.close();
				firstc.moveToNext();
			}
			return _msgs.toArray(new MessageEntry[_msgs.size()]);
		}
		return null;
	}

	private static void initDBModel()
	{
		synchronized (_synclock)
		{
			if (_db == null)
				_db = new MessageDBModel();
		}
	}

}
