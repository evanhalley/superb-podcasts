/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.services;

import java.io.IOException;

import com.emuneee.superb.R;
import com.emuneee.superb.engine.Episode;
import com.emuneee.superb.ui.bitmap.ImageCache;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Manages audio playback for our podcast app
 * 
 * @author Evan
 * 
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class PlayerService extends Service implements OnPreparedListener,
		OnCompletionListener, OnErrorListener, OnSeekCompleteListener {
	private static final String sTag = "PlayerService";
	private static final int ID = 43100347;

	// This is the object that receives interactions from clients. See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new LocalBinder();

	private PlayerServiceListener mListener;
	private State mState = State.Uninitialized;
	private Episode mCurrentEpisode;
	private ImageCache mImageCache;

	public enum State {
		Playing, Stopped, Preparing, Paused, Initialized, Uninitialized
	}

	private MediaPlayer mPlayer;
	private NotificationManager mNotificationManager;

	public void setImageCache(ImageCache imageCache) {
		mImageCache = imageCache;
	}

	// STATE MACHINE LOGIC

	/**
	 * Transitions the media player from one state to the next
	 * 
	 * @param state
	 *            state to go to
	 * @param episode
	 *            episode to play
	 */
	private void goToState(State state, Episode episode) {
		Log.d(sTag, "Moving to state: " + state.name());
		switch (state) {
		case Uninitialized:
			resetPlayer();
			break;
		case Playing:
			if (mState == State.Playing)
				stopPlayer();
			if (mState == State.Stopped
					|| (mState == State.Paused && episode != null))
				resetPlayer();
			if (mState == State.Uninitialized && episode != null) {
				try {
					initializePlayer(episode);
				} catch (Exception e) {
					Log.w(sTag, "Error initializing player");
					Log.w(sTag, e.getMessage());
				}
			}
			if (mState == State.Initialized) {
				preparePlayer();
				// break because prepare is asynchronous
				break;
			}
			if (mState == State.Paused)
				startPlayer();
			if (mState == State.Preparing)
				startPlayer();
			break;
		case Paused:
			pausePlayer();
			break;
		case Stopped:
			if (mState == State.Playing || mState == State.Paused)
				stopPlayer();
			break;
		default:
			break;
		}
	}

	private void pausePlayer() {
		mPlayer.pause();
		mState = State.Paused;
		showNotification(State.Paused);
	}

	private void startPlayer() {
		mPlayer.start();
		mState = State.Playing;
		showNotification(State.Playing);
		mListener.onPlayStarted();
	}

	private void preparePlayer() {
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mPlayer.prepareAsync();
		mState = State.Preparing;
	}

	private void initializePlayer(Episode episode) throws IOException {
		mCurrentEpisode = episode;
		Uri uri = null;
		String localUriString = episode.getLocalContentUrl();
		if (localUriString != null && localUriString.length() > 0) {
			uri = Uri.parse(localUriString);
		} else {
			uri = Uri.parse(episode.getRemoteContentUrl());
		}
		mPlayer.setDataSource(this, uri);
		mState = State.Initialized;
	}

	private void resetPlayer() {
		mPlayer.reset();
		mState = State.Uninitialized;
	}

	private void stopPlayer() {
		mPlayer.stop();
		mState = State.Stopped;
		mNotificationManager.cancel(ID);
	}

	public State getState() {
		return mState;
	}

	/**
	 * Resumes play of the currently paused track, if it was paused
	 */
	public void play() {
		play(null);
	}

	/**
	 * Plays a new episode
	 * 
	 * @param episode
	 *            episode to play
	 */
	public void play(Episode episode) {
		goToState(State.Playing, episode);
	}

	/**
	 * Pauses the currently playing episode
	 */
	public void pause() {
		goToState(State.Paused, null);
	}

	/**
	 * Stops the player
	 */
	public void stop() {
		goToState(State.Stopped, null);
	}

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		public PlayerService getService() {
			return PlayerService.this;
		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(sTag, "Unbinded from service");
		return super.onUnbind(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(sTag, "Binded to service");
		return mBinder;
	}

	@Override
	public void onCreate() {
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mPlayer = new MediaPlayer();
		mPlayer.setOnPreparedListener(this);
		super.onCreate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		Log.d(sTag, "Destroying media player object");
		mPlayer.release();
		mPlayer = null;
		mImageCache = null;
		super.onDestroy();
	}

	public void registerPlayerServiceListener(PlayerServiceListener listener) {
		mListener = listener;
	}

	private void showNotification(State action) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this);
		Notification notification = builder.build();
		notification.contentView = new RemoteViews(getPackageName(),
				R.layout.notification_normal_view);
		notification.contentView.setTextViewText(R.id.text_view_episode_title,
				mCurrentEpisode.getTitle());
		notification.contentView.setTextViewText(R.id.text_view_channel_title,
				mCurrentEpisode.getChannelTitle());
		// TODO null check
		notification.contentView.setImageViewBitmap(
				R.id.image_view_channel_art,
				mImageCache.getBitmapFromCache(mCurrentEpisode.getImageUrl()));
		switch (action) {
		case Playing:
			notification.icon = R.drawable.ic_stat_play;
			notification.tickerText = "Playing " + mCurrentEpisode.getTitle();
			break;
		case Paused:
			notification.icon = R.drawable.ic_stat_pause;
			notification.tickerText = "Paused " + mCurrentEpisode.getTitle();
			break;
		default:
			break;
		}
		builder.setOngoing(true);
		mNotificationManager.notify(ID, notification);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	// LISTENER CALLBACKS

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		goToState(State.Playing, null);
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
		// TODO Auto-generated method stub

	}

	/**
	 * Returns the position of the current episode in milliseconds
	 * 
	 * @return
	 */
	public long getCurrentPosition() {
		if (mPlayer != null && mPlayer.isPlaying()) {
			return mPlayer.getCurrentPosition();
		}
		return -1;
	}

	/**
	 * Returns the length of the current episode in milliseconds
	 * 
	 * @return
	 */
	public long getCurrentDuration() {
		if (mPlayer != null && mPlayer.isPlaying()) {
			return mPlayer.getDuration();
		}
		return -1;
	}

	public Episode getEpisode() {
		return mCurrentEpisode;
	}
}
