/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.ui.fragments;

import java.util.Map;
import com.emuneee.superb.R;
import com.emuneee.superb.engine.Episode;
import com.emuneee.superb.ui.MainActivity;
import com.emuneee.superb.ui.helpers.AllEpisodesListAdapter;
import com.emuneee.superb.ui.tasks.GetAllEpisodes;
import com.emuneee.superb.ui.tasks.TaskListener;
import com.slidingmenu.lib.app.SlidingActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * Shows a list of episodes
 * 
 * @author Evan
 * 
 */
public class AllEpisodesListFragment extends EpisodeFragment implements
		TaskListener<Map<String, Episode>> {
	public static final String TAG = "AllEpisodesListFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_all_episodes_list,
				container, false);
		mSlidingMenu = ((SlidingActivity) getActivity()).getSlidingMenu();
		GetAllEpisodes task = new GetAllEpisodes(this, mMainActivity
				.getDataEngine().getEpisodeDataSource());
		task.execute(mOrderBy);
		return view;
	}

	@Override
	public void onPostExecute(Map<String, Episode> result) {
		View view = getView();
		// populate the list
		mAdapter = new AllEpisodesListAdapter(mMainActivity, result.values(),
				R.layout.all_episode_item);
		mAdapterView = (GridView) view
				.findViewById(R.id.grid_view_all_episodes);
		mAdapterView.setAdapter(mAdapter);
		mAdapterView.setOnItemClickListener(this);
		mAdapterView.setOnItemLongClickListener(this);
	}

	@Override
	public void update() {
		mMainActivity.getDownloadHelper().startChannelUpdates(
				new UpdateTaskListener());
	}

	private class UpdateTaskListener implements TaskListener<Void> {

		@Override
		public void onPostExecute(Void result) {
			GetAllEpisodes task = new GetAllEpisodes(
					AllEpisodesListFragment.this, mMainActivity.getDataEngine()
							.getEpisodeDataSource());
			task.execute(mOrderBy);
		}
	}
}