/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.ui.tasks;

import com.emuneee.superb.R;
import com.emuneee.superb.ui.MainActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Taskt that handles importing a channel into the database
 * 
 * @author Evan
 * 
 */
public class ImportChannelTask extends AsyncTask<Void, Void, Boolean> {
	private static final String sTag = "ImportChannelTask";
	private MainActivity mMainActivity;
	private String mUrl;
	private ProgressDialog mDialog;
	private TaskListener mListener;

	public ImportChannelTask(MainActivity mainActivity, String channelUrl,
			TaskListener listener) {
		mListener = listener;
		mMainActivity = mainActivity;
		mUrl = channelUrl;
	}

	protected void onPreExecute() {
		// show importing dialog
		mDialog = ProgressDialog.show(mMainActivity,
				mMainActivity.getString(R.string.dialog_importing_title),
				mMainActivity.getString(R.string.dialog_importing_message));
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		Log.d(sTag, "Adding podcast url " + mUrl);
		return mMainActivity.getDataEngine().addChannel(mUrl);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		mDialog.dismiss();
		if (mListener != null) {
			mListener.onPostExecute(result);
		}
	}
}