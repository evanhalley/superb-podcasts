/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.engine.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.emuneee.superb.engine.Episode;

/**
 * Manages interaction with the playlist table
 * 
 * @author Evan
 * 
 */
public class PlaylistDataSource extends DataSource {
	private static final String sTag = "PlaylistDataSource";

	public PlaylistDataSource(SuperbDBHelper helper) {
		super(helper);
	}

	/**
	 * Populates the playlist table with the episodes in the passed in list
	 * 
	 * @param playlist
	 *            list of episodes in the playlist
	 * @return result of operation
	 */
	public boolean createPlayList(List<Episode> playlist) {
		boolean result = false;

		try {
			mDatabase.beginTransaction();
			// clear the current playlist
			mDatabase.delete("playlist", null, null);

			// iterate through the list adding to the playlist
			for (Episode episode : playlist) {
				ContentValues values = episodeToPlaylistItem(episode);
				mDatabase.insert("playlist", null, values);
			}
			mDatabase.setTransactionSuccessful();
			result = true;
		} catch (SQLException e) {
			Log.w(sTag, "Error creating the playlist");
			Log.w(sTag, e.getMessage());
		} finally {
			mDatabase.endTransaction();
		}

		return result;
	}

	/**
	 * Returns a list of episodes on the playlist
	 * 
	 * @return episodes on the playlist
	 */
	public List<Episode> getPlayList() {
		List<Episode> playlist = new ArrayList<Episode>();

		String query = "SELECT e._id, e.title, e.url, e.description, e.publishDatetime, "
				+ "e.contentUrl, e.contentSize, e.contentDuration, e.mimeType, "
				+ "e.guid, e.fk_channelId, c.imageUrl "
				+ "FROM playlist p "
				+ "JOIN episode e ON e._id = p.fk_episodeId "
				+ "JOIN channel c ON c._id = e.fk_channelId";
		Cursor cursor = mDatabase.rawQuery(query, null);
		while (cursor.moveToNext()) {
			playlist.add(EpisodeDataSource.cursorToEpisode(cursor));
		}
		cursor.close();
		return playlist;
	}

	/**
	 * Creates a playlist object out of an Episode
	 * 
	 * @param episode
	 * @return playlist item
	 */
	public static ContentValues episodeToPlaylistItem(Episode episode) {
		ContentValues values = new ContentValues();
		values.put("fk_episodeId", episode.getId());
		return values;
	}
}