/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.engine.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Manages HTTP connections for the application
 * 
 * @author ehalley
 * 
 */
public class HttpUtils {
	private final static String sTag = "HttpUtils";
	private static int sTimeout = 10000;

	/**
	 * Returns a URL connection object tailor made for the Google APIs
	 * 
	 * @param urlString
	 *            URL to connect to
	 * @return
	 * @throws IOException
	 */
	public static HttpURLConnection getUrlConnection(String urlString)
			throws IOException {
		Log.v(sTag, "New URL connection: " + urlString);
		URL url = new URL(urlString);
		HttpURLConnection urlConnection = (HttpURLConnection) url
				.openConnection();
		urlConnection.setReadTimeout(sTimeout);
		return urlConnection;
	}

	/**
	 * Executes an HTTP POST where a string is returned from the network
	 * 
	 * @param url
	 *            to be accessed
	 * @param content
	 *            data to send to the remote server
	 * 
	 * @return data returned from the url
	 */
	public static String executeHttpPost(String url, String content) {
		final StringBuilder retVal = new StringBuilder();
		BufferedReader bReader = null;
		InputStreamReader iReader = null;
		HttpURLConnection connection = null;
		try {
			// send the data to the remote server
			byte[] contentBytes = content.getBytes();
			connection = getUrlConnection(url);
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestProperty("Content-Length",
					Integer.toString(contentBytes.length));
			DataOutputStream output = new DataOutputStream(
					connection.getOutputStream());
			output.write(contentBytes);
			output.flush();
			output.close();
			// read the response from the server
			bReader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = bReader.readLine()) != null) {
				retVal.append(line);
			}
			Log.v(sTag, "Content Size: " + retVal.length());
			Log.v(sTag, "Response Code: " + connection.getResponseCode());
		} catch (IOException e) {
			Log.w(sTag, e.getMessage());
			return null;
		} finally {
			try {
				if (iReader != null)
					iReader.close();
				if (bReader != null)
					bReader.close();
				connection.disconnect();
			} catch (Exception e) {
				Log.w(sTag, e.getMessage());
			}
		}

		return retVal.toString();
	}

	/**
	 * Downloads a bitmap from the network
	 * 
	 * @param url
	 *            url that points to the bitmap to be downloaded
	 * @return bitmap downloaded from the network
	 */
	public static Bitmap getBitmapFromNetwork(String url) {
		Bitmap bitmap = null;
		InputStream input = null;
		HttpURLConnection connection = null;
		try {
			connection = getUrlConnection(url);
			input = connection.getInputStream();
			bitmap = BitmapFactory.decodeStream(input);
		} catch (IOException e) {
			Log.w(sTag, "Error downloading bitmap from network");
			Log.w(sTag, "" + e.getMessage());
		} finally {
			try {
				if (connection != null) {
					connection.disconnect();
				}
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
			}
		}
		return bitmap;
	}
}
