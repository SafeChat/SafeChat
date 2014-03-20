package de.zakath.safechat.classes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.zakath.safechat.R;
import de.zakath.safechat.classes.entries.*;

public class ConversationDetailAdapter extends
		ArrayAdapter<MessageEntry>
{

	private MessageEntry[] _entries;

	public ConversationDetailAdapter(Context context,
			MessageEntry[] entries)
	{
		super(context, R.layout.detail_row, entries);
		_entries = entries;

	}

	public static class ViewHolder
	{
		TextView message;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{

		View v = convertView;
		ViewHolder holder;

		if (v == null)
		{
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.detail_row, null);
			holder = new ViewHolder();

			holder.message = (TextView) v.findViewById(R.id.MessageDetail);

			v.setTag(holder);
		} else
		{
			holder = (ViewHolder) v.getTag();
		}

		final MessageEntry custom = _entries[position];
		if (custom != null)
		{
			holder.message.setText(custom.getMessage());
		}
		return v;

	}

}
