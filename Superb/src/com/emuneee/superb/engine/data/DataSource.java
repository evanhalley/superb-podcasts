/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.engine.data;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Generic data source.  Abstracts logic from database interaction
 * @author Evan
 *
 */
public abstract class DataSource {
	private static final String sTag = "DataSource";
	protected SuperbDBHelper mDBHelper;
	protected SQLiteDatabase mDatabase;
	
	public DataSource(SuperbDBHelper helper) {
		mDBHelper = helper;
	}
	
	public void open() {
		Log.d(sTag, "Opening...");
		mDatabase = mDBHelper.getWritableDatabase();
	}
	
	public void close() {
		Log.d(sTag, "Closing...");
		mDatabase.close();
	}
}