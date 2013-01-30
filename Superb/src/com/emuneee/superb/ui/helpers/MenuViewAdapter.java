/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.ui.helpers;

import java.util.ArrayList;
import java.util.Collection;

import com.emuneee.superb.R;
import com.emuneee.superb.engine.Channel;
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
public class MenuViewAdapter extends ArrayAdapter<Channel> {
	public static final int OFFSET = 2;
	public static final int PLAYLIST_IDX = 0;
	public static final int ALL_EPISODES_IDX = 1;
	private static final String sTag = "MenuViewAdapter";
	private LayoutInflater mInflater;
	private int mItemLayout;
	private ImageWorker mImageWorker;
	private MainActivity mMainActivity;

	public MenuViewAdapter(MainActivity mainActivity,
			Collection<Channel> channels, int itemLayout) {
		super(mainActivity, 0, new ArrayList<Channel>(channels));
		mMainActivity = mainActivity;
		mInflater = (LayoutInflater) mMainActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItemLayout = itemLayout;
		mImageWorker = mMainActivity.getImageWorker();
	}

	@Override
	public int getCount() {
		return super.getCount() + OFFSET;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChannelViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(mItemLayout, null);
			holder = new ChannelViewHolder();
			holder.setImageView((ImageView) convertView
					.findViewById(R.id.image_view_channel_art));
			holder.setTextView((TextView) convertView
					.findViewById(R.id.text_view_channel_title));
			convertView.setTag(holder);
		} else {
			holder = (ChannelViewHolder) convertView.getTag();
		}

		if (position == PLAYLIST_IDX) {
			// add playlist item
			holder.getTextView().setText(R.string.menu_show_playlist);
			holder.getImageView().setImageResource(
					R.drawable.ic_action_playlist);
		} else if (position == ALL_EPISODES_IDX) {
			// add all episodes item
			holder.getTextView().setText(R.string.menu_all_episodes);
			holder.getImageView().setImageResource(
					R.drawable.ic_action_playlist);
		} else {
			// offset by two so we can add to items
			Channel channel = getItem(position - OFFSET);
			holder.getTextView().setText(channel.getTitle());
			mImageWorker
					.loadImage(channel.getImageUrl(), holder.getImageView());
		}
		return convertView;
	}
}
