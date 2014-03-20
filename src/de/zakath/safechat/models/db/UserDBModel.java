package de.zakath.safechat.models.db;

import de.zakath.safechat.provider.ContextProvider;
import android.database.Cursor;
import android.database.sqlite.*;

public class UserDBModel extends SQLiteOpenHelper
{

	public static final int VERSION = 1;
	protected static final String DATABASE_NAME = "CryptoChatClient";
	public static final String TABLE_NAME = "Usertable";

	public static final String KEY_ID = "id";
	public static final String KEY_UID = "uid";
	public static final String KEY_DISPLAYNAME = "name";

	public UserDBModel()
	{
		super(ContextProvider.getAppContext(), DATABASE_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String create = "CREATE TABLE Usertable (id INTEGER PRIMARY KEY, uid INTEGER, name TEXT);";

		db.execSQL(create);
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
