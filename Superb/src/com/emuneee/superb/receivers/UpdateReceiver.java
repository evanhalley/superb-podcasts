/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.receivers;

import com.emuneee.superb.services.UpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Catches an interval of time then starts the updater service
 * 
 * @author Evan
 * 
 */
public class UpdateReceiver extends BroadcastReceiver {
	private static final String sTag = "UpdateReceiver";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(sTag, "Receiving interval broadcast, starting updater service");
		// start service
		context.startService(new Intent(context, UpdateService.class));
	}
}
