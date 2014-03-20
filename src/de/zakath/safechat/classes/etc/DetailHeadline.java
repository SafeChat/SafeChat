package de.zakath.safechat.classes.etc;

import android.graphics.drawable.Drawable;

public class DetailHeadline
{

	private Drawable _profilpic;
	private String _displayname;

	public Drawable getProfilPicture()
	{
		return _profilpic;
	}

	public String getDisplayName()
	{
		return _displayname;
	}

	public DetailHeadline(String displayname, Drawable profilpicture)
	{
		_displayname = displayname;
		_profilpic = profilpicture;
	}

}
