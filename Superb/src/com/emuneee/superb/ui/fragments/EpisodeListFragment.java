/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.ui.fragments;

import java.util.Map;
import java.util.Map.Entry;

import com.emuneee.superb.R;
import com.emuneee.superb.engine.Channel;
import com.emuneee.superb.engine.Episode;
import com.emuneee.superb.ui.MainActivity;
import com.emuneee.superb.ui.helpers.Const;
import com.emuneee.superb.ui.helpers.EpisodeListAdapter;
import com.emuneee.superb.ui.tasks.PopulateEpisodeList;
import com.emuneee.superb.ui.tasks.TaskListener;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Shows a list of episodes
 * 
 * @author Evan
 * 
 */
public class EpisodeListFragment extends EpisodeFragment implements
		TaskListener<Entry<Channel, Map<String, Episode>>> {
	public static final String TAG = "EpisodeListFragment";
	private long mChannelId;
	private Channel mChannel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		mChannelId = args.getLong(Const.BUNDLE_CHANNEL_ID);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_episode_list, container,
				false);
		mAdapterView = (ListView) view.findViewById(R.id.list_view_episodes);
		mAdapterView.setOnItemClickListener(this);
		mAdapterView.setOnItemLongClickListener(this);
		PopulateEpisodeList task = new PopulateEpisodeList(
				mMainActivity.getDataEngine(), this, mChannelId);
		task.execute(mOrderBy);
		return view;
	}

	@Override
	public void onPostExecute(Entry<Channel, Map<String, Episode>> result) {
		mChannel = result.getKey();
		View view = getView();
		// fill out the podcast header
		ImageView imageView = (ImageView) view
				.findViewById(R.id.image_view_channel_art);
		TextView textView = (TextView) view
				.findViewById(R.id.text_view_channel_title);
		mMainActivity.getImageWorker().loadImage(mChannel.getImageUrl(),
				imageView);
		textView.setText(mChannel.getTitle());
		// populate the list
		mAdapter = new EpisodeListAdapter(getActivity(), result.getValue()
				.values(), R.layout.episode_list_item);
		mAdapterView.setAdapter(mAdapter);
	}

	@Override
	public void update() {
		mMainActivity.getDownloadHelper().startChannelUpdate(mChannel,
				new UpdateTaskListener());
	}

	private class UpdateTaskListener implements TaskListener<Boolean> {

		@Override
		public void onPostExecute(Boolean result) {
			PopulateEpisodeList task = new PopulateEpisodeList(
					mMainActivity.getDataEngine(), EpisodeListFragment.this,
					mChannelId);
			task.execute(mOrderBy);
		}
	}
}