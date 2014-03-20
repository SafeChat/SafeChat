package de.zakath.safechat.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.ListAdapter;
import android.widget.ListView;
import de.zakath.safechat.classes.adapter.ConversationOverviewEntryAdapter;
import de.zakath.safechat.classes.entries.OverviewEntry;
import de.zakath.safechat.controller.ConversationOverviewController;
import de.zakath.safechat.controller.callbacks.IConversationOverviewEntriesCallback;
import de.zakath.safechat.provider.UserProvider;
import de.zakath.safechat.R;

public class ConversationOverviewActivity extends ListActivity
{

	private ConversationOverviewController _controller;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		_controller = new ConversationOverviewController(this);
		
		if (!UserProvider.isAccountExsisting()) // Erstmal schauen, ob wir einen
												// Account haben...
		{
			Intent i = new Intent(this, LoginActivity.class);
			startActivity(i);
		} else
		{
			setContentView(R.layout.activity_conversation_overview);
			UpdateOverview();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.conversation_overview, menu);
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);

		int _id = ((OverviewEntry) l.getItemAtPosition(position)).getID();
		Intent i = new Intent(getApplicationContext(),
				ConversationDetailActivity.class);
		i.putExtra("ID", _id);
		startActivity(i);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle presses on the action bar items
		switch (item.getItemId())
		{
		case R.id.action_enablesamplemode:

			_controller.setSampleMode(!item.isChecked());
			UpdateOverview();
			item.setChecked(!item.isChecked());

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void UpdateOverview()
	{
		_controller.getEntries(new IConversationOverviewEntriesCallback()
		{

			@Override
			public void ConversationOverviewCallback(OverviewEntry[] entries)
			{
				// Now create a new list adapter bound to the cursor.
				// SimpleListAdapter is designed for binding to a Cursor.
				if (entries != null)
				{
					ListAdapter adapter = new ConversationOverviewEntryAdapter(
							ConversationOverviewActivity.this, entries);
					// Bind to our new adapter.
					setListAdapter(adapter);
				} else
				{
					setListAdapter(null);
				}
			}
		});
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
