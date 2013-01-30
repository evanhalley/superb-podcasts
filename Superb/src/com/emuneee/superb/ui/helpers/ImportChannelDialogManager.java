/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.ui.helpers;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.emuneee.superb.R;

/**
 * @author Evan
 * 
 */
public class ImportChannelDialogManager {

	public interface ImportChannelListener {
		public void onChannelImport(String channelUrl);
	}

	/**
	 * Shows the dialog that allows the user to choose where they would like to
	 * import a dialog from
	 */
	public static void showImportChannelDialog(final Context context,
			final ImportChannelListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.import_channel_dialog_title);
		builder.setItems(R.array.import_channel_sources,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							showChannelUrlDialog(context, listener);
							break;
						}
					}
				});
		builder.create().show();
	}

	/**
	 * Opens the dialog to allow the user to enter a url of a channel they would 
	 * like to import
	 * @param context
	 * @param listener
	 */
	public static void showChannelUrlDialog(final Context context,
			final ImportChannelListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_podcast_url, null);
		final EditText editTextChannelUrl = (EditText) view
				.findViewById(R.id.edit_text_podcast_url);
		builder.setView(view);
		builder.setPositiveButton(R.string.button_import,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						String channelUrl = editTextChannelUrl.getText()
								.toString();
						if(!channelUrl.toLowerCase().contains("http://")) {
							channelUrl = "http://" + channelUrl;
						}
						// validate entered text
						String error = validateEntry(context, channelUrl);
						if(error == null) {
							listener.onChannelImport(channelUrl);
						} else {
							Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
						}
					}
				});
		builder.setNegativeButton(R.string.button_cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		builder.create().show();
	}
	
	/**
	 * Tests whether a url is valid or not
	 * @param context
	 * @param urlStr
	 * @return
	 */
	private static String validateEntry(Context context, String urlStr) {
		if(urlStr == null || urlStr.length() == 0) {
			return context.getString(R.string.message_please_enter_url);
		}
		
		try {
			URL url = new URL(urlStr);
			url.toURI();
		} catch (MalformedURLException e) {
			return context.getString(R.string.message_please_enter_url);
		} catch (URISyntaxException e) {
			return context.getString(R.string.message_please_enter_url);
		}
		return null;
	}
}
