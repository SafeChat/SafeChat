package de.zakath.safechat.models.db;

import de.zakath.safechat.provider.ContextProvider;
import android.database.Cursor;
import android.database.sqlite.*;

public class MessageDBModel extends SQLiteOpenHelper
{

	public static final int VERSION = 1;
	protected static final String DATABASE_NAME = "CryptoChatClient";
	public static final String TABLE_NAME = "Messagetable";

	public static final String KEY_ID = "id";
	public static final String KEY_MSG = "msg";
	public static final String KEY_REMOTE = "remoteid";
	public static final String KEY_OWNMESSAGE = "ownmessage";
	public static final String KEY_TIMESTAMP = "timestamp";
	public static final String KEY_TRANSMITTSTATUS = "transmittstatus";

	public MessageDBModel()
	{
		super(ContextProvider.getAppContext(), DATABASE_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String c = "CREATE TABLE Messagetable (id INTEGER PRIMARY KEY, msg TEXT, remoteid INTEGER, ownmessage BOOLEAN , timestamp TIMESTAMP, transmittstatus BOOLEAN);";

		db.execSQL(c);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	public void executeSQL(String s)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(s);
	}

	public Cursor executeQuery(String query)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		return db.rawQuery(query, null);
	}

	public SQLiteStatement getStatement(String query)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		return db.compileStatement(query);
	}
	
	
	
}
