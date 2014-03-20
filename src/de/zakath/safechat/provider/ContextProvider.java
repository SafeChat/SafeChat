package de.zakath.safechat.provider;

import android.content.Context;

public class ContextProvider
{

	private static Context _c;

	public static Context getAppContext()
	{
		return _c;
	}

	public static void setContext(Context c)
	{
		_c = c;
	}

}
