/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.engine.data;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.emuneee.superb.engine.Episode;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

/**
 * Handles data interaction for episode data
 * 
 * @author Evan
 * 
 */
public class EpisodeDataSource extends DataSource {
	public enum OrderBy {
		TITLE_ASC("title ASC"), 
		TITLE_DSC("title DESC"), 
		PUBLISHED_ASC("publishDatetime ASC"), 
		PUBLISHED_DSC("publishDatetime DESC");

		private String mOrderBy;

		OrderBy(String orderBy) {
			mOrderBy = orderBy;
		}

		String getOrderBy() {
			return mOrderBy;
		}
	}

	private static final String sTag = "EpisodeDataSource";
	private static final String EPISODE_COLUMNS = "e._id, e.title, e.url, e.description, e.publishDatetime, "
			+ "e.remoteContentUrl, e.contentSize, e.contentDuration, e.mimeType, e.localContentUrl, "
			+ "e.guid, e.fk_channelId, e.fk_statusId, c.imageUrl, c.title AS channelTitle";

	public EpisodeDataSource(SuperbDBHelper dbHelper) {
		super(dbHelper);
	}

	/**
	 * Inserts a list of episodes into the database
	 * 
	 * @param episode
	 * @return success of insert op, if false, transaction will be rolled back
	 */
	public int insertEpisodes(Collection<Episode> episodes, long id) {
		Log.d(sTag,
				"Number of episodes inserting into database: "
						+ episodes.size());
		int episodesInserted = 0;
		for (Episode episode : episodes) {
			episode.setChannelId(id);
			if (insertEpisode(episode) > -1) {
				episodesInserted++;
			}
		}
		return episodesInserted;
	}

	/**
	 * Inserts a new episode into the database
	 * 
	 * @param episode
	 * @return
	 */
	public long insertEpisode(Episode episode) {
		Log.v(sTag, "Inserting episode into database: " + episode);
		ContentValues values = episodeToContentValues(episode);
		long id = -1;
		try {
			id = mDatabase.insert("episode", null, values);
		} catch (SQLException e) {
			Log.w(sTag, "Error inserting episode");
			Log.w(sTag, e.getMessage());
		}
		return id;
	}

	/**
	 * Updates an episode
	 * 
	 * @param episode
	 * @return
	 */
	public boolean updateEpisode(Episode episode) {
		Log.v(sTag, "Updating channel in database: " + episode);
		ContentValues values = episodeToContentValues(episode);
		long rowsAffected = mDatabase.update("episode", values, "_id = "
				+ episode.getId(), null);
		return rowsAffected == 1;
	}

	/**
	 * Deletes a database
	 * 
	 * @param episode
	 * @return
	 */
	public boolean deleteEpisode(Episode episode) {
		Log.v(sTag, "Deleting channel from database: " + episode);
		long rowsAffected = mDatabase.delete("episode",
				"_id = " + episode.getId(), null);
		return rowsAffected == 1;
	}

	/**
	 * Returns the episode with the ID
	 * 
	 * @param id
	 * @return
	 */
	public Episode getEpisode(long id) {
		Episode episode = null;
		String query = "SELECT " + EPISODE_COLUMNS + " " + "FROM episode e "
				+ "JOIN channel c ON c._id = e.fk_channelId "
				+ "WHERE e._id = " + id;
		Cursor cursor = mDatabase.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			episode = cursorToEpisode(cursor);
		}
		cursor.close();
		return episode;
	}

	/**
	 * Gets a list of all episodes
	 * 
	 * @param orderBy
	 * @return
	 */
	public Map<String, Episode> getAllEpisodes(OrderBy orderBy) {
		Map<String, Episode> episodes = new LinkedHashMap<String, Episode>();
		String query = "SELECT " + EPISODE_COLUMNS + " " + "FROM episode e "
				+ "JOIN channel c ON c._id = e.fk_channelId";
		if (orderBy != null) {
			query += " ORDER BY " + orderBy.getOrderBy();
		}
		Cursor cursor = mDatabase.rawQuery(query, null);
		while (cursor.moveToNext()) {
			Episode episode = cursorToEpisode(cursor);
			episodes.put(episode.getGuid(), episode);
		}
		cursor.close();
		return episodes;
	}

	/**
	 * Gets a map of episodes by channel
	 * 
	 * @param channelId
	 * @param orderBy
	 * @return
	 */
	public Map<String, Episode> getDownloadedEpisodes(long channelId,
			OrderBy orderBy) {
		Map<String, Episode> episodes = new LinkedHashMap<String, Episode>();
		Cursor cursor = mDatabase.query("episode", null, "isDownloaded = 1",
				null, null, null, orderBy.getOrderBy());
		while (cursor.moveToNext()) {
			Episode episode = cursorToEpisode(cursor);
			episodes.put(episode.getGuid(), episode);
		}
		cursor.close();
		return episodes;
	}

	/**
	 * Gets a map of episodes by channel
	 * 
	 * @param channelId
	 * @param orderBy
	 * @return
	 */
	public Map<String, Episode> getEpisodes(long channelId, OrderBy orderBy) {
		Map<String, Episode> episodes = new LinkedHashMap<String, Episode>();
		Cursor cursor = mDatabase.query("episode", null, "fk_channelId = "
				+ channelId, null, null, null, orderBy.getOrderBy());
		while (cursor.moveToNext()) {
			Episode episode = cursorToEpisode(cursor);
			episodes.put(episode.getGuid(), episode);
		}
		cursor.close();
		return episodes;
	}

	/**
	 * Converts an episode to a content values object
	 * 
	 * @param episode
	 * @return
	 */
	private static ContentValues episodeToContentValues(Episode episode) {
		ContentValues values = new ContentValues();
		values.put("title", episode.getTitle());
		values.put("url", episode.getUrl());
		values.put("description", episode.getDescription());
		values.put("publishDatetime", episode.getPublishDatetime());
		values.put("remoteContentUrl", episode.getRemoteContentUrl());
		values.put("localContentUrl", episode.getLocalContentUrl());
		values.put("contentSize", episode.getContentSize());
		values.put("contentDuration", episode.getContentDuration());
		values.put("fk_channelId", episode.getChannelId());
		values.put("mimeType", episode.getMimeType());
		values.put("guid", episode.getGuid());
		values.put("fk_statusId", episode.getStatusId());
		return values;
	}

	/**
	 * Retrieves an episode from a cursor object
	 * 
	 * @param cursor
	 * @return
	 */
	public static Episode cursorToEpisode(Cursor cursor) {
		Episode episode = new Episode(cursor.getLong(cursor
				.getColumnIndex("_id")), cursor.getString(cursor
				.getColumnIndex("title")), cursor.getString(cursor
				.getColumnIndex("url")), cursor.getString(cursor
				.getColumnIndex("description")), cursor.getLong(cursor
				.getColumnIndex("publishDatetime")), cursor.getString(cursor
				.getColumnIndex("remoteContentUrl")), cursor.getLong(cursor
				.getColumnIndex("contentSize")), cursor.getInt(cursor
				.getColumnIndex("contentDuration")), cursor.getLong(cursor
				.getColumnIndex("fk_channelId")), cursor.getString(cursor
				.getColumnIndex("mimeType")), cursor.getString(cursor
				.getColumnIndex("guid")));
		int imageUrlIdx = cursor.getColumnIndex("imageUrl");
		if (imageUrlIdx > -1) {
			episode.setImageUrl(cursor.getString(imageUrlIdx));
		}
		int channelTitleIdx = cursor.getColumnIndex("channelTitle");
		if (channelTitleIdx > -1) {
			episode.setChannelTitle(cursor.getString(channelTitleIdx));
		}
		episode.setLocalContentUrl(cursor.getString(cursor
				.getColumnIndex("localContentUrl")));
		episode.setStatusId(cursor.getInt(cursor.getColumnIndex("fk_statusId")));
		Log.v(sTag, "Episode retrieved from database: " + episode);
		return episode;
	}
}
