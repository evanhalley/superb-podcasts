/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.ui.tasks;

/**
 * @author Evan
 *
 */
public interface TaskListener<T> {
	public void onPostExecute(T result);
}
