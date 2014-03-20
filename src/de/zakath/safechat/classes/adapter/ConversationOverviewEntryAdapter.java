package de.zakath.safechat.classes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.zakath.safechat.R;
import de.zakath.safechat.classes.entries.OverviewEntry;

public class ConversationOverviewEntryAdapter extends
		ArrayAdapter<OverviewEntry>
{

	OverviewEntry[] entries;

	public ConversationOverviewEntryAdapter(Context context,
			OverviewEntry[] entries)
	{
		super(context, R.layout.conversation_overview_row, entries);
		this.entries = entries;
	}

	public static class ViewHolder
	{
		TextView displayname;
		TextView lastmessage;
		ImageView messagestatus;
		ImageView profilpicture;
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
			v = vi.inflate(R.layout.conversation_overview_row, null);
			holder = new ViewHolder();
			holder.displayname = (TextView) v
					.findViewById(R.id.OverviewDisplaname);
			holder.lastmessage = (TextView) v.findViewById(R.id.LastMessage);

			holder.messagestatus = (ImageView) v
					.findViewById(R.id.MessageStatusOverview);
			holder.profilpicture = (ImageView) v
					.findViewById(R.id.ProfilPictureOverview);

			v.setTag(holder);
		} else
			holder = (ViewHolder) v.getTag();

		final OverviewEntry custom = entries[position];
		if (custom != null)
		{
			holder.displayname.setText(custom.getDisplayName());
			holder.lastmessage.setText(custom.getlastMessage());
			// Profilbild setzen
			holder.profilpicture.setImageDrawable(custom.getProfilPicture());
			// Message status setzen
			holder.messagestatus
					.setImageResource(custom.isYourMessage() ? R.drawable.check
							: R.drawable.arrow);
		}
		return v;

	}
}
