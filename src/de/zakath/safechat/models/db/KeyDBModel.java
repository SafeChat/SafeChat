package de.zakath.safechat.models.db;

import de.zakath.safechat.provider.ContextProvider;
import android.database.Cursor;
import android.database.sqlite.*;

public class KeyDBModel extends SQLiteOpenHelper
{

	public static final int VERSION = 1;
	protected static final String DATABASE_NAME = "CryptoChatClient";
	public static final String TABLE_NAME = "Keytable";

	public static final String KEY_ID = "id";
	public static final String KEY_UID = "uid";
	public static final String KEY_PUBLICKEY = "publickey";

	public KeyDBModel()
	{
		super(ContextProvider.getAppContext(), DATABASE_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String creation = "CREATE TABLE " + TABLE_NAME + "(" + KEY_ID
				+ " INTEGER PRIMARY KEY, " + KEY_UID + " INTEGER,"
				+ KEY_PUBLICKEY + " BLOB);";
		db.execSQL(creation);
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
