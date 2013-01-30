/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.ui.tasks;

import com.emuneee.superb.engine.Episode;
import com.emuneee.superb.engine.data.EpisodeDataSource;

import android.os.AsyncTask;

/**
 * @author Evan
 * 
 */
public class GetEpisodeTask extends AsyncTask<Long, Void, Episode> {
	private TaskListener<Episode> mListener;
	private EpisodeDataSource mEpisodeDataSource;

	public GetEpisodeTask(TaskListener<Episode> listener,
			EpisodeDataSource episodeDataSource) {
		mEpisodeDataSource = episodeDataSource;
		mListener = listener;
	}

	@Override
	protected Episode doInBackground(Long... params) {
		long id = params[0];
		return mEpisodeDataSource.getEpisode(id);
	}

	@Override
	protected void onPostExecute(Episode episode) {
		mListener.onPostExecute(episode);
	}
}