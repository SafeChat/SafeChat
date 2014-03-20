package de.zakath.safechat.controller;

import de.zakath.safechat.provider.*;
import android.content.Context;
import android.os.AsyncTask;

public abstract class BaseController
{
	protected final Context context;

	public BaseController(Context context)
	{
		this.context = context;
		ContextProvider.setContext(context);
		if (UserProvider.isAccountExsisting())
		{
			new AsyncTask<Void, Void, Void>()
			{

				@Override
				protected Void doInBackground(Void... params)
				{
					MessageProvider.init();
					return null;
				}

			}.execute();
		}
	}

	public void onStart()
	{
	}

	public void onPaused()
	{
		new AsyncTask<Void, Void, Void>()
		{

			@Override
			protected Void doInBackground(Void... params)
			{
				ClientProvider.Shutdown();
				return null;
			}

		}.execute();
	}

	public void onResume()
	{
		new AsyncTask<Void, Void, Void>()
		{

			@Override
			protected Void doInBackground(Void... params)
			{
				if (UserProvider.isAccountExsisting())
					MessageProvider.init();
				return null;
			}

		}.execute();
	}

	public void onStopped()
	{

	}

	public void onRestart()
	{
	}

	public Context getContext()
	{
		return context;
	}
}
