/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.ui.fragments;

import com.emuneee.superb.R;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

/**
 * @author Evan
 * 
 */
public class AppearanceSettingsFragment extends PreferenceFragment implements
		OnPreferenceClickListener {
	public static final String TAG = "AppearanceSettingsFragment";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preference_appearance);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		return true;
	}
}
