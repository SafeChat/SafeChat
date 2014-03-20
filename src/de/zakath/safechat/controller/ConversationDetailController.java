package de.zakath.safechat.controller;

import java.util.*;

import de.zakath.safechat.classes.entries.*;
import de.zakath.safechat.classes.etc.DetailHeadline;
import de.zakath.safechat.controller.callbacks.*;
import de.zakath.safechat.eventargs.*;
import de.zakath.safechat.listener.*;
import de.zakath.safechat.models.MessageModel;
import de.zakath.safechat.provider.MessageProvider;
import android.R;
import android.content.Context;
import android.os.AsyncTask;

public class ConversationDetailController extends BaseController implements
		MessageTransmittedListener, MessageReceivedListener
{

	protected int _id;

	protected List<MessageEntry> _entries = new ArrayList<MessageEntry>();

	public ConversationDetailController(Context context, int id)
	{
		super(context);
		_id = id;
		MessageProvider.addMessageReceivedListener(this);
		MessageProvider.addMessageTransmittedListener(this);
	}

	public void getEntries(final IConverastionDetailEntriesCallback callback)
	{

		new AsyncTask<Void, Void, MessageEntry[]>()
		{

			@Override
			protected MessageEntry[] doInBackground(Void... arg0)
			{
				MessageEntry[] e = MessageModel
						.getMessageEntriesfromandtoID(_id);
				Arrays.sort(e);
				_entries = Arrays.asList(e);
				return e;
			}

			@Override
			protected void onPostExecute(MessageEntry[] entries)
			{
				callback.HandleConversationDetailCallback(entries);
			}

		}.execute();

	}

	public void getHeadline(final IDetailHeadlineCallback callback)
	{
		new AsyncTask<Void, Void, DetailHeadline>()
		{

			@Override
			protected DetailHeadline doInBackground(Void... params)
			{
				// Hier die Infos holen
				return new DetailHeadline("Test1", context.getResources()
						.getDrawable(R.drawable.sym_action_chat));
			}

			@Override
			protected void onPostExecute(DetailHeadline headline)
			{
				callback.HandleDetailheadlineCallback(headline);
			}

		}.execute();
	}

	@Override
	public void HandleMessageReceived(Object sender, MessageReceivedEventArgs e)
	{

	}

	@Override
	public void HandleMessageTransmitted(Object sender,
			MessageTransmittedEventArgs e)
	{

	}


}
