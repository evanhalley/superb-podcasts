/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.ui.helpers;

import java.util.ArrayList;
import java.util.Collection;

import com.emuneee.superb.R;
import com.emuneee.superb.engine.Episode;
import com.emuneee.superb.ui.MainActivity;
import com.emuneee.superb.ui.bitmap.ImageWorker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Binds the channel data to the channel view fragments
 * 
 * @author Evan
 * 
 */
public class AllEpisodesListAdapter extends ArrayAdapter<Episode> {
	private static final String sTag = "EpisodeListAdapter";
	private LayoutInflater mInflater;
	private int mItemLayout;
	private ImageWorker mImageWorker;
	private MainActivity mMainActivity;

	public AllEpisodesListAdapter(MainActivity mainActivity,
			Collection<Episode> episodes, int itemLayout) {
		super(mainActivity, 0, new ArrayList<Episode>(episodes));
		mMainActivity = mainActivity;
		mInflater = (LayoutInflater) mMainActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItemLayout = itemLayout;
		mImageWorker = mMainActivity.getImageWorker();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AllEpisodeViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(mItemLayout, null);
			holder = new AllEpisodeViewHolder();
			holder.setTextViewTitle((TextView) convertView
					.findViewById(R.id.text_view_channel_title));
			holder.setImageViewChannelArt((ImageView) convertView
					.findViewById(R.id.image_view_channel_art));
			convertView.setTag(holder);
		} else {
			holder = (AllEpisodeViewHolder) convertView.getTag();
		}
		Episode episode = getItem(position);
		holder.getTextViewTitle().setText(episode.getTitle());
		mImageWorker.loadImage(episode.getImageUrl(),
				holder.getImageViewChannelArt());
		return convertView;
	}
}
