/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.engine.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Handles all database interaction
 * @author Evan
 * 
 */
public class SuperbDBHelper extends SQLiteOpenHelper {
	private static final String sTag = "SuperbDBHelper";
	private static final String sDatabase = "superb.sqlite";
	private static final int sVersion = 1;
	
	private static final String sRefStatusSchema = "CREATE TABLE ref_status (" +
			"_id INTEGER PRIMARY KEY NOT NULL UNIQUE, " +
			"name TEXT NOT NULL)";
	
	private static final String sChannelSchema = "CREATE TABLE channel (" +
			"_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, " +
			"title TEXT, " +
			"url TEXT NOT NULL UNIQUE, " +
			"updateDatetime INTEGER, " +
			"description TEXT, " +
			"imageUrl TEXT)";
	
	private static final String sEpisodeSchema = "CREATE TABLE episode (" +
			"_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, " +
			"title TEXT NOT NULL, " +
			"url TEXT NOT NULL, " +
			"description TEXT NOT NULL, " + 
			"publishDatetime INTEGER NOT NULL, " +
			"remoteContentUrl TEXT, " +
			"localContentUrl TEXT, " +
			"contentSize INTEGER NOT NULL, " +
			"contentDuration INTEGER NOT NULL, " +
			"mimeType TEXT NOT NULL, " +
			"guid TEXT NOT NULL UNIQUE, " +
			"fk_channelId INTEGER NOT NULL, " +
			"fk_statusId INTEGER NOT NULL DEFAULT 0, " +
			"FOREIGN KEY(fk_statusId) REFERENCES ref_status(_id), " +
			"FOREIGN KEY(fk_channelId) REFERENCES channel(_id))";
	
	private static final String sPlaylistSchema = "CREATE TABLE playlist (" + 
			"_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, " + 
			"fk_episodeId INTEGER NOT NULL, " + 
			"FOREIGN KEY(fk_episodeId) REFERENCES episode(_id))";
	
	public SuperbDBHelper(Context context) {
		super(context, sDatabase, null, sVersion);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase database) {
		Log.d(sTag, "Creating database tables");
		Log.d(sTag, sRefStatusSchema);
		Log.d(sTag, sChannelSchema);
		Log.d(sTag, sEpisodeSchema);
		Log.d(sTag, sPlaylistSchema);
		database.execSQL(sRefStatusSchema);
		database.execSQL(sChannelSchema);
		database.execSQL(sEpisodeSchema);
		database.execSQL(sPlaylistSchema);
		
		// loop through enum and populate ref status table
		Status[] values = Status.values();
		for(int i = 0; i < values.length; i++) {
			ContentValues record = new ContentValues();
			record.put("_id", values[i].ordinal());
			record.put("name", values[i].name());
			database.insert("ref_status", null, record);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

}
