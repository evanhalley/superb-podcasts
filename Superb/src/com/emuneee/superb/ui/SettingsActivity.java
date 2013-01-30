/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.ui;

import com.emuneee.superb.ui.fragments.SettingsFragment;

import android.app.Activity;
import android.os.Bundle;

/**
 * @author Evan
 * 
 */
public class SettingsActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Display the fragment as the main content.
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new SettingsFragment())
				.commit();
		
	}
}
