/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.ui.helpers;

import java.util.ArrayList;
import java.util.Collection;

import com.emuneee.superb.R;
import com.emuneee.superb.engine.Episode;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Binds the channel data to the channel view fragments
 * 
 * @author Evan
 * 
 */
public class EpisodeListAdapter extends ArrayAdapter<Episode> {
	private static final String sTag = "EpisodeListAdapter";
	private LayoutInflater mInflater;
	private int mItemLayout;

	public EpisodeListAdapter(Context context, Collection<Episode> episodes,
			int itemLayout) {
		super(context, 0, new ArrayList<Episode>(episodes));
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItemLayout = itemLayout;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		EpisodeViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(mItemLayout, null);
			holder = new EpisodeViewHolder();
			holder.setTextViewTitle((TextView) convertView
					.findViewById(R.id.text_view_channel_title));
			holder.setTextViewSubtitle((TextView) convertView
				.findViewById(R.id.text_view_channel_subtitle));
			convertView.setTag(holder);
		} else {
			holder = (EpisodeViewHolder) convertView.getTag();
		}
		Episode episode = getItem(position);
		holder.getTextViewTitle().setText(episode.getTitle());
		holder.getTextViewSubtitle().setText(episode.getPublishDatetimeStr());
		return convertView;
	}
}
