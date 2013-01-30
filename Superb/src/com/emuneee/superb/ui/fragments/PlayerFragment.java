/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.ui.fragments;

import com.emuneee.superb.R;
import com.emuneee.superb.engine.Episode;
import com.emuneee.superb.engine.utils.DateParser;
import com.emuneee.superb.services.PlayerService;
import com.emuneee.superb.services.PlayerServiceListener;
import com.emuneee.superb.services.PlayerService.State;
import com.emuneee.superb.ui.MainActivity;
import com.emuneee.superb.ui.tasks.GetEpisodeTask;
import com.emuneee.superb.ui.tasks.TaskListener;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingActivity;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Evan
 * 
 */
public class PlayerFragment extends Fragment implements TaskListener<Episode>,
		OnClickListener, PlayerServiceListener {
	public static final String TAG = "PlayerFragment";
	private Episode mEpisode;
	private SlidingMenu mSlidingMenu;
	private PlayerService mPlayerService;
	private MainActivity mMainActivity;
	private TextView mTextViewTimeRemaining;
	private TextView mTextViewTimeElapsed;
	private Handler mHandler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		mSlidingMenu = ((SlidingActivity) getActivity()).getSlidingMenu();
		mMainActivity = (MainActivity) getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_player, container, false);
		mTextViewTimeRemaining = (TextView) view
				.findViewById(R.id.text_view_time_remaining);
		mTextViewTimeElapsed = (TextView) view
				.findViewById(R.id.text_view_time_elapsed);
		view.findViewById(R.id.image_button_play_pause)
				.setOnClickListener(this);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void setCurrentEpisode(long id) {
		new GetEpisodeTask(this, mMainActivity.getDataEngine()
				.getEpisodeDataSource()).execute(id);
	}

	/**
	 * Configures the player with album art, titles, progress etc.
	 */
	public void configurePlayer(Episode episode) {
		mEpisode = episode;
		ImageView imageView = (ImageView) getView().findViewById(
				R.id.image_view_channel_art);
		mMainActivity.getImageWorker().loadImage(mEpisode.getImageUrl(),
				imageView);
		togglePlayPauseButton(mPlayerService.getState() == State.Playing);
	}

	@Override
	public void onPostExecute(Episode result) {
		configurePlayer(result);
		mPlayerService.play(mEpisode);
		getView().findViewById(R.id.progress_bar_buffering).setVisibility(
				View.VISIBLE);
		togglePlayPauseButton(true);
	}

	private void togglePlayPauseButton(boolean isPlaying) {
		ImageButton playButton = (ImageButton) getView().findViewById(
				R.id.image_button_play_pause);
		if (isPlaying) {
			playButton.setImageResource(R.drawable.ic_pause);
		} else {
			playButton.setImageResource(R.drawable.ic_play);
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.image_button_play_pause:
			if (mPlayerService.getState() == State.Playing) {
				mPlayerService.pause();
				togglePlayPauseButton(false);
			} else {
				mPlayerService.play();
				togglePlayPauseButton(mPlayerService.getState() == State.Playing);
			}
			break;
		}
	}

	public void registerPlayerService(PlayerService mBoundService) {
		Log.d(TAG, "Registering the player service");
		mPlayerService = mMainActivity.getPlayerService();
		mPlayerService.registerPlayerServiceListener(this);
		if (mBoundService.getState() == State.Playing) {
			configurePlayer(mBoundService.getEpisode());
			mHandler.postDelayed(mUpdateTimeTask, 1000);
		}
	}

	@Override
	public void onPlayStarted() {
		getView().findViewById(R.id.progress_bar_buffering).setVisibility(
				View.INVISIBLE);
		updatePlayerTime();
		mHandler.postDelayed(mUpdateTimeTask, 1000);
	}
	
	private void updatePlayerTime() {
		long totalDuration = mPlayerService.getCurrentDuration();
		long currentDuration = mPlayerService.getCurrentPosition();
		if (totalDuration != -1 && currentDuration != -1) {
			mTextViewTimeElapsed.setText(DateParser
					.convertMsToMinutes(currentDuration));
			mTextViewTimeRemaining.setText(DateParser
					.convertMsToMinutes(totalDuration - currentDuration));
		}
	}

	/**
	 * Background Runnable thread
	 * */
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			updatePlayerTime();
			// Running this thread after 100 milliseconds
			mHandler.postDelayed(this, 1000);
		}
	};
}
