/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.services;

import com.emuneee.superb.engine.utils.DownloadHelper;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * @author Evan
 *
 */
public class UpdateService extends IntentService {
	private static final String sTag = "UpdateService";
	private DownloadHelper mDownloadHelper;
	
	public UpdateService() {
		super(sTag);
		mDownloadHelper = new DownloadHelper(this);
	}

	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent arg0) {
		Log.d(sTag, "OnHandleIntent: Starting channel updates");
		mDownloadHelper.startChannelUpdates(null);
	}
}