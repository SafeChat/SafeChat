package de.zakath.safechat.controller;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import de.zakath.safechat.classes.entries.*;
import de.zakath.safechat.controller.callbacks.IConversationOverviewEntriesCallback;
import de.zakath.safechat.models.MessageModel;
import de.zakath.safechat.provider.UserProvider;

public class ConversationOverviewController extends BaseController
{

	private boolean _isinSampleMode = false;

	public ConversationOverviewController(Context context)
	{
		super(context);
	}

	public void getEntries(final IConversationOverviewEntriesCallback callback)
	{
		if (!_isinSampleMode)
		{
			new AsyncTask<Void, Void, OverviewEntry[]>()
			{

				@Override
				protected OverviewEntry[] doInBackground(Void... params)
				{
					List<OverviewEntry> _entries = new ArrayList<OverviewEntry>();
					MessageEntry[] e = MessageModel.getLatestEntries();
					if (e != null)
					{
						for (MessageEntry me : MessageModel.getLatestEntries())
						{
							int id = me.getSenderID() == UserProvider.getUID() ? me
									.getTargetID() : me.getSenderID();
							_entries.add(new OverviewEntry(id, UserProvider
									.getDisplayName(id), me.getMessage(),
									context.getResources().getDrawable(
											android.R.drawable.sym_action_chat), me
											.getSenderID() == UserProvider
											.getUID()));
						}
						return _entries.toArray(new OverviewEntry[_entries
								.size()]);
					}
					return null;
				}

				@Override
				protected void onPostExecute(OverviewEntry[] entries)
				{
					callback.ConversationOverviewCallback(entries);
				}

			}.execute();
		} else
		{
			List<OverviewEntry> _entries = new ArrayList<OverviewEntry>();
			
			
			_entries.add(new OverviewEntry(1, "Max", "Hay! It`s a great chat..", null, false));
			_entries.add(new OverviewEntry(2, "Emil", "What`s up?", null, true));
			_entries.add(new OverviewEntry(3, "Dennis", "Never mind, i`m gonna go now", null, true));
			_entries.add(new OverviewEntry(4, "Sophie", "I like you :)", null, false));
			_entries.add(new OverviewEntry(5, "Lara", "Let`s have a meet? :D", null, false));
			
			callback.ConversationOverviewCallback(_entries.toArray(new OverviewEntry[_entries.size()]));
		}

	}

	public void setSampleMode(boolean mode)
	{
		_isinSampleMode = mode;
	}

}
