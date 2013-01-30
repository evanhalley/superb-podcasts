/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.ui.tasks;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;

import com.emuneee.superb.engine.Channel;
import com.emuneee.superb.engine.Episode;
import com.emuneee.superb.engine.SuperbDataEngine;
import com.emuneee.superb.engine.data.EpisodeDataSource.OrderBy;

import android.os.AsyncTask;

/**
 * @author Evan
 * 
 */
public class PopulateEpisodeList extends
		AsyncTask<OrderBy, Void, Entry<Channel, Map<String, Episode>>> {
	private TaskListener<Entry<Channel, Map<String, Episode>>> mListener;
	private long mChannelId;
	private SuperbDataEngine mSuperbDataEngine;

	public PopulateEpisodeList(SuperbDataEngine superbDataEngine,
			TaskListener<Entry<Channel, Map<String, Episode>>> listener,
			long channelId) {
		mSuperbDataEngine = superbDataEngine;
		mListener = listener;
		mChannelId = channelId;
	}

	@Override
	protected Entry<Channel, Map<String, Episode>> doInBackground(
			OrderBy... params) {
		Channel channel = mSuperbDataEngine.getChannelDataSource().getChannel(
				mChannelId);
		Map<String, Episode> episodes = mSuperbDataEngine
				.getEpisodeDataSource().getEpisodes(mChannelId, params[0]);
		return new SimpleEntry<Channel, Map<String, Episode>>(channel, episodes);
	}

	@Override
	protected void onPostExecute(Entry<Channel, Map<String, Episode>> entry) {
		mListener.onPostExecute(entry);
	}
}