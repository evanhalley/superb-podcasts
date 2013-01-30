/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.ui.tasks;

import java.util.Map;

import com.emuneee.superb.engine.Episode;
import com.emuneee.superb.engine.SuperbDataEngine;
import com.emuneee.superb.engine.data.EpisodeDataSource;
import com.emuneee.superb.engine.data.EpisodeDataSource.OrderBy;
import com.emuneee.superb.ui.MainActivity;

import android.os.AsyncTask;

/**
 * @author Evan
 * 
 */
public class GetAllEpisodes extends
		AsyncTask<OrderBy, Void, Map<String, Episode>> {
	private TaskListener<Map<String, Episode>> mListener;
	private EpisodeDataSource mEpisodeDataSource;

	public GetAllEpisodes(TaskListener<Map<String, Episode>> listener,
			EpisodeDataSource episodeDataSource) {
		mListener = listener;
		mEpisodeDataSource = episodeDataSource;
	}

	@Override
	protected Map<String, Episode> doInBackground(OrderBy... params) {
		Map<String, Episode> episodes = mEpisodeDataSource
				.getAllEpisodes(params[0]);
		return episodes;
	}

	@Override
	protected void onPostExecute(Map<String, Episode> entry) {
		mListener.onPostExecute(entry);
	}
}