/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.engine.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

/**
 * Parses dates encountered in RSS XML
 * 
 * @author Evan
 * 
 */
public class DateParser {
	private static final String sTag = "DateParser";
	// Fri, 09 Nov 2012 04:54:32 +0000 - android central
	// Fri, 16 Nov 2012 08:46:03 PST - verge
	// Sat, 17 Nov 2012 22:57:00 EDT - engadget
	private static final String[] sDatePatterns = new String[] {
			"yyyy-MM-dd'T'HH:mm:ssZ", "EEE, d MMM yyyy HH:mm:ss Z",
			"EEE, d MMM yyyy HH:mm:ss zzz" };
	private SimpleDateFormat mMatchingFormat;

	/**
	 * Initializes the date parser Using the sample date, the date parser will
	 * create a compatible date formatter
	 * 
	 * @param sampleDate
	 */
	public DateParser(String sampleDate) {
		for (int i = 0; i < sDatePatterns.length; i++) {
			try {
				mMatchingFormat = new SimpleDateFormat(sDatePatterns[i],
						Locale.US);
				mMatchingFormat.parse(sampleDate);
				break;
			} catch (IllegalArgumentException e) {
				Log.d(sTag, "Pattern doesn't match pattern: "
						+ sDatePatterns[i]);
				mMatchingFormat = null;
			} catch (ParseException e) {
				Log.d(sTag, "Pattern doesn't match pattern: "
						+ sDatePatterns[i]);
				mMatchingFormat = null;
			}
		}
	}

	/**
	 * Converts a time expressed in milliseconds to HH:mm::SS
	 * 
	 * @param millis
	 * @return
	 */
	public static String convertMsToMinutes(long millis) {
		int hours = (int) (millis / (1000 * 60 * 60));
		int minutes = (int) (millis / (1000 * 60));
		int seconds = (int) ((millis / 1000) % 60);
		if (hours > 0) {
			minutes = minutes - (hours * 60);
			return String.format(Locale.getDefault(), "%d:%02d:%02d", hours,
					minutes, seconds);
		} else {
			return String.format(Locale.getDefault(), "%d:%02d", minutes,
					seconds);
		}
	}

	public boolean canParse() {
		return mMatchingFormat != null;
	}

	public long parseDate(String dateStr) {
		long dateInMs = 0;
		if (mMatchingFormat != null) {
			try {
				Date date = mMatchingFormat.parse(dateStr);
				dateInMs = date.getTime();
			} catch (ParseException e) {
				Log.w(sTag, "Error parsing date");
				Log.w(sTag, e.getMessage());
			}
		}
		return dateInMs;
	}
}
