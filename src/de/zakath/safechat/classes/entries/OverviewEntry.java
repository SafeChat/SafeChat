package de.zakath.safechat.classes.entries;

import de.zakath.safechat.provider.*;
import android.R;
import android.graphics.drawable.Drawable;

public class OverviewEntry
{

	private int _id;
	private String _dispalyname;
	private String _lastmessage;
	private boolean _isyourmessage;
	private Drawable _profilpicture;

	public int getID()
	{
		return _id;
	}

	public String getDisplayName()
	{
		return _dispalyname;
	}

	public String getlastMessage()
	{
		return _lastmessage;
	}

	public boolean isYourMessage()
	{
		return _isyourmessage;
	}

	public Drawable getProfilPicture()
	{
		return _profilpicture;
	}

	public OverviewEntry(int id, String displayname,
			String lastmessage, Drawable profilpicture, boolean isyourmessage)
	{
		_id = id;
		_dispalyname = displayname;
		_lastmessage = lastmessage;
		_isyourmessage = isyourmessage;
		_profilpicture = profilpicture;
	}

	public static OverviewEntry fromMessageEntry(MessageEntry me)
	{
		int id = me.getSenderID() == UserProvider.getUID() ? me.getTargetID()
				: me.getSenderID();
		return new OverviewEntry(id,
				UserProvider.getDisplayName(id), me.getMessage(),
				ContextProvider.getAppContext().getResources()
						.getDrawable(R.drawable.sym_action_chat),
				me.getSenderID() == UserProvider.getUID());
	}
}
