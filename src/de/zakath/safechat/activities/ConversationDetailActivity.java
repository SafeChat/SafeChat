package de.zakath.safechat.activities;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListAdapter;
import de.zakath.safechat.classes.adapter.ConversationDetailAdapter;
import de.zakath.safechat.classes.entries.*;
import de.zakath.safechat.controller.ConversationDetailController;
import de.zakath.safechat.controller.callbacks.IConverastionDetailEntriesCallback;
import de.zakath.safechat.R;

public class ConversationDetailActivity extends ListActivity
{

	private ConversationDetailController _controller;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conversation_overview);
		getListView().setDividerHeight(0);
		_controller = new ConversationDetailController(getApplicationContext(),
				getIntent().getIntExtra("ID", 0));

		_controller.getEntries(new IConverastionDetailEntriesCallback()
		{

			@Override
			public void HandleConversationDetailCallback(MessageEntry[] entries)
			{
				ListAdapter adapter = new ConversationDetailAdapter(
						ConversationDetailActivity.this, entries);

				// Bind to our new adapter.
				setListAdapter(adapter);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.conversation_detail, menu);
		return true;
	}

	@Override
	protected void onStart()
	{
		_controller.onStart();
		super.onStart();
	}

	@Override
	protected void onPause()
	{
		_controller.onPaused();
super.onPause();
	}

	@Override
	protected void onRestart()
	{
		_controller.onRestart();
		super.onRestart();
	}

	@Override
	protected void onStop()
	{
		_controller.onStopped();
		super.onStop();
	}

	@Override
	protected void onResume()
	{
		_controller.onResume();
		super.onResume();
	}
}
