/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.engine.utils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.emuneee.superb.R;
import com.emuneee.superb.engine.Channel;
import com.emuneee.superb.engine.Episode;
import com.emuneee.superb.engine.SuperbDataEngine;
import com.emuneee.superb.engine.data.EpisodeDataSource;
import com.emuneee.superb.engine.data.ChannelDataSource.OrderBy;
import com.emuneee.superb.engine.data.Status;
import com.emuneee.superb.ui.MainActivity;
import com.emuneee.superb.ui.tasks.TaskListener;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.webkit.MimeTypeMap;

/**
 * Manages updating and downloading podcasts in the background
 * 
 * @author Evan
 * 
 */
public class DownloadHelper {
	private static final String sTag = "DownloadHelper";
	private static final int ID = 987654321;
	private OnDownloadCompleteReceiver mOnDownloadCompleteReceiver;
	private DownloadManager mDownloadManager;
	private SuperbDataEngine mSuperbDataEngine;
	private SharedPreferences mPreferences;
	private final static Pattern mPattern = Pattern.compile("[^a-zA-z0-9]");
	private Context mContext;

	/**
	 * Instantiates
	 * 
	 * @param context
	 */
	public DownloadHelper(Context context) {
		mContext = context;
		mSuperbDataEngine = new SuperbDataEngine(mContext);
		mOnDownloadCompleteReceiver = new OnDownloadCompleteReceiver();
		mContext.registerReceiver(mOnDownloadCompleteReceiver,
				new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		mOnDownloadCompleteReceiver.setIsRegistered(true);
		mDownloadManager = (DownloadManager) context
				.getSystemService(Context.DOWNLOAD_SERVICE);
		mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
	}

	public void destroy() {
		if (mOnDownloadCompleteReceiver.getIsRegistered()) {
			try {
				mContext.unregisterReceiver(mOnDownloadCompleteReceiver);
			} catch (IllegalStateException e) {
				Log.w(sTag, "Receiver already unregistered");
			} finally {
				mOnDownloadCompleteReceiver.setIsRegistered(false);
			}
		}
		if (mSuperbDataEngine != null) {
			mSuperbDataEngine.close();
		}
	}

	/**
	 * Generates a file name for an episode
	 * 
	 * @param episode
	 * @return
	 */
	private static String generateFilename(Episode episode) {
		String extension = MimeTypeMap.getFileExtensionFromUrl(episode
				.getRemoteContentUrl());
		return mPattern.matcher(episode.getId() + "_" + episode.getTitle())
				.replaceAll("_") + "." + extension;
	}

	/**
	 * Returns the episde ID from a downloaded episode filename
	 * 
	 * @param filename
	 * @return
	 */
	public static long getEpisodeIdFromFilename(String filename) {
		// break up into tokens by slash
		String[] tokens = filename.split("/");
		// break up filename by _
		tokens = tokens[tokens.length - 1].split("_");
		return Long.parseLong(tokens[0]);
	}

	/**
	 * Initiates a download, returns id to the download request record
	 * 
	 * @param episode
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public long downloadEpisode(Episode episode) {
		Log.v(sTag, "Downloading episode " + episode);
		Uri uri = Uri.parse(episode.getRemoteContentUrl());
		Uri destinationUri = Uri.parse("file://"
				+ Environment.getExternalStorageDirectory().getPath() + "/"
				+ Environment.DIRECTORY_PODCASTS + "/"
				+ generateFilename(episode));
		Log.d(sTag, "Episode URI " + uri);
		Log.d(sTag, "Episode destination URI " + destinationUri);
		Request request = new Request(uri);
		request.setTitle(episode.getTitle());
		request.setDestinationUri(destinationUri);
		// configure our download request
		boolean downloadOverCell = !mPreferences.getBoolean(
				mContext.getString(R.string.key_download_wifi_only), true);
		boolean downloadWhileRoaming = mPreferences.getBoolean(
				mContext.getString(R.string.key_download_while_roaming), false);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			request.setAllowedOverMetered(downloadOverCell);
		} else {
			if (downloadOverCell) {
				request.setAllowedNetworkTypes(Request.NETWORK_WIFI
						| Request.NETWORK_MOBILE);
			} else {
				request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
			}
		}
		request.setAllowedOverRoaming(downloadWhileRoaming);
		request.allowScanningByMediaScanner();
		request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		return mDownloadManager.enqueue(request);
	}

	/**
	 * Constructs a new status bar notification
	 */
	public void buildNewEpisodeNotification(List<Episode> newEpisodes) {
		int newEpisodesCount = newEpisodes.size();
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				mContext);
		NotificationManager notificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		builder.setSmallIcon(R.drawable.ic_stat_episode);
		builder.setContentTitle(newEpisodesCount + " "
				+ mContext.getString(R.string.notification_new_episodes_title));
		builder.setTicker(newEpisodesCount + " "
				+ mContext.getString(R.string.notification_new_episodes_title));
		builder.setWhen(Calendar.getInstance().getTimeInMillis());
		builder.setContentInfo(newEpisodesCount + "");
		builder.setLargeIcon(BitmapFactory.decodeResource(
				mContext.getResources(), R.drawable.ic_stat_episode));
		builder.setAutoCancel(true);

		// build bigview notification
		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		// Moves events into the big view
		for (Episode episode : newEpisodes) {
			inboxStyle.addLine(episode.getTitle());
		}
		// Moves the big view style object into the notification object.
		builder.setStyle(inboxStyle);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
		Intent intent = new Intent(mContext, MainActivity.class);
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(intent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(resultPendingIntent);

		notificationManager.notify(ID, builder.build());
	}

	/**
	 * Asynchronously updates a channel
	 * 
	 * @param channelId
	 *            id of the channel to update
	 * @param listener
	 */
	public void startChannelUpdate(Channel channel,
			TaskListener<Boolean> listener) {
		RefreshChannelTask task = new RefreshChannelTask(listener);
		task.execute(channel);
	}

	/**
	 * Asynchronous updates all the channels in our database
	 * 
	 * @param listener
	 */
	public void startChannelUpdates(TaskListener<Void> listener) {
		RefreshChannelsTask task = new RefreshChannelsTask(listener);
		task.execute();
	}

	/**
	 * Updates a channel
	 * 
	 * @param channel
	 *            channel to update
	 */
	public List<Episode> updateChannel(Channel channel) {
		List<Episode> newEpisodes = new ArrayList<Episode>();
		Log.v(sTag, "Updating channel " + channel);
		// get a map of existing episodes
		Map<String, Episode> existingEpisodes = mSuperbDataEngine
				.getEpisodeDataSource().getEpisodes(channel.getId(),
						EpisodeDataSource.OrderBy.PUBLISHED_DSC);
		// parse the podcast url
		AbstractMap.SimpleEntry<Channel, Map<String, Episode>> entries = SuperbDataEngine
				.parseChannel(channel.getUrl());
		if (entries != null && SuperbDataEngine.validateChannel(entries)) {
			Collection<Episode> parsedEpisodes = entries.getValue().values();
			// lets compare existing and parsed episodes
			for (Episode episode : parsedEpisodes) {
				if (!existingEpisodes.containsKey(episode.getGuid())) {
					Log.v(sTag, "Found new episode: " + episode);
					long id = mSuperbDataEngine.getEpisodeDataSource().insertEpisode(
							episode);
					episode.setId(id);
					newEpisodes.add(episode);
				}
			}
			// now lets up the update date
			channel.setUpdateDatetime(Calendar.getInstance().getTimeInMillis());
			mSuperbDataEngine.getChannelDataSource().updateChannel(channel);
		}
		return newEpisodes;
	}

	/**
	 * Updates a channel in our database
	 * 
	 * @author Evan
	 * 
	 */
	public class RefreshChannelTask extends
			AsyncTask<Channel, Void, List<Episode>> {
		private TaskListener<Boolean> mListener;

		public RefreshChannelTask(TaskListener<Boolean> listener) {
			mListener = listener;
		}

		@Override
		protected List<Episode> doInBackground(Channel... params) {
			return updateChannel(params[0]);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(List<Episode> newEpisodes) {
			if (newEpisodes.size() > 0) {
				Log.d(sTag, "New episodes found: " + newEpisodes.size());
				buildNewEpisodeNotification(newEpisodes);
				boolean autoDownload = PreferenceManager
						.getDefaultSharedPreferences(mContext).getBoolean(
								mContext.getString(R.string.key_auto_download),
								true);
				if (autoDownload) {
					// lets start the task to enqueue episodes for download
					QueueEpisodesTask task = new QueueEpisodesTask();
					task.execute(newEpisodes);
				}
			}
			if (mListener != null) {
				mListener.onPostExecute(true);
			}
		}
	}

	/**
	 * Updates our channels in our database
	 * 
	 * @author Evan
	 * 
	 */
	private class RefreshChannelsTask extends
			AsyncTask<Void, Void, List<Episode>> {
		private static final String sTag = "RefreshChannelsTask";
		private TaskListener<Void> mListener;

		public RefreshChannelsTask(TaskListener<Void> listener) {
			mListener = listener;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected List<Episode> doInBackground(Void... params) {
			List<Episode> newEpisodes = new ArrayList<Episode>();
			// get a list of existing podcast channels
			List<Channel> channels = mSuperbDataEngine.getChannelDataSource()
					.getAllChannels(OrderBy.TITLE_ASC);
			for (Channel channel : channels) {
				newEpisodes.addAll(updateChannel(channel));
			}
			return newEpisodes;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(List<Episode> newEpisodes) {
			// if we have new episodes, build and show notification
			if (newEpisodes.size() > 0) {
				Log.d(sTag, "New episodes found: " + newEpisodes.size());
				buildNewEpisodeNotification(newEpisodes);

				boolean autoDownload = PreferenceManager
						.getDefaultSharedPreferences(mContext).getBoolean(
								mContext.getString(R.string.key_auto_download),
								true);
				if (autoDownload) {
					// lets start the task to enqueue episodes for download
					QueueEpisodesTask task = new QueueEpisodesTask();
					task.execute(newEpisodes);
				}
			}
			if (mListener != null) {
				mListener.onPostExecute(null);
			}
		}
	}

	/**
	 * Enqueues a list of episodes for downloading
	 * 
	 * @author Evan
	 * 
	 */
	public class QueueEpisodesTask extends
			AsyncTask<List<Episode>, Void, List<Long>> {
		private static final String sTag = "QueueEpisodesTask";

		@Override
		protected List<Long> doInBackground(List<Episode>... params) {
			List<Episode> episodes = params[0];
			List<Long> requestIds = new ArrayList<Long>(episodes.size());
			for (Episode episode : episodes) {
				Log.v(sTag, "Enqueueing episode for download: " + episode);
				long id = downloadEpisode(episode);
				requestIds.add(id);
				episode.setStatus(com.emuneee.superb.engine.data.Status.DOWNLOADING);
				mSuperbDataEngine.getEpisodeDataSource().updateEpisode(episode);
			}
			return requestIds;
		}

		@Override
		protected void onPostExecute(List<Long> requestIds) {
			mOnDownloadCompleteReceiver.addRequestIds(requestIds);
		}
	}

	/**
	 * Updates an episode in the database
	 * 
	 * params[0] = id of episode to update params[1] = status to set params[2] =
	 * local file path
	 * 
	 * @author Evan
	 * 
	 */
	public class UpdateEpisodeStatusTask extends AsyncTask<Object, Void, Void> {
		private static final String sTag = "UpdateEpisodeTask";

		@Override
		protected Void doInBackground(Object... params) {
			Long episodeId = (Long) params[0];
			com.emuneee.superb.engine.data.Status status = (com.emuneee.superb.engine.data.Status) params[1];
			Episode episode = mSuperbDataEngine.getEpisodeDataSource()
					.getEpisode(episodeId);
			episode.setStatus(status);
			if (params.length >= 3) {
				episode.setLocalContentUrl((String) params[2]);
			}
			Log.v(sTag, "Updating episode: " + episode);
			mSuperbDataEngine.getEpisodeDataSource().updateEpisode(episode);
			return null;
		}
	}

	/**
	 * @author Evan
	 * 
	 */
	public class OnDownloadCompleteReceiver extends BroadcastReceiver {
		private static final String sTag = "OnDownloadCompleteReceiver";
		private List<Long> mRequestIds = new ArrayList<Long>();
		private boolean mIsRegistered = false;

		public void addRequestIds(List<Long> requestIds) {
			Log.d(sTag, "Number of request IDs adding: " + requestIds.size());
			mRequestIds.addAll(requestIds);
		}

		public void setIsRegistered(boolean isRegistered) {
			mIsRegistered = isRegistered;
		}

		public boolean getIsRegistered() {
			return mIsRegistered;
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			DownloadManager downloadManager = (DownloadManager) context
					.getSystemService(Context.DOWNLOAD_SERVICE);
			if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
				// lets update the episode, change its status to downloaded
				// note, the id of the episode is the first token in the file
				// name
				// set the content url to the local downloaded copy
				Cursor cursor = null;
				int length = mRequestIds.size();
				for (int i = 0; i < length; i++) {
					long requestId = mRequestIds.get(i);
					Query query = new Query();
					query.setFilterById(requestId);
					cursor = downloadManager.query(query);
					if (cursor.moveToFirst()) {
						int columnIndex = cursor
								.getColumnIndex(DownloadManager.COLUMN_STATUS);
						if (DownloadManager.STATUS_SUCCESSFUL == cursor
								.getInt(columnIndex)) {
							// remove successful download from the request id
							// list
							mRequestIds.remove(requestId);
							// get file name so we can update its database
							String filename = cursor
									.getString(cursor
											.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
							long id = getEpisodeIdFromFilename(filename);
							UpdateEpisodeStatusTask task = new UpdateEpisodeStatusTask();
							task.execute(id, Status.DOWNLOADED, filename);
							break;
						}
					}
				}
				if (cursor != null) {
					cursor.close();
				}
			}
			if (mRequestIds.size() == 0) {
				Log.d(sTag, "No more pending downloads, deregistering");
				mContext.unregisterReceiver(this);
			}
		}
	}
}