package com.lightspeedworks.events;

import groovy.lang.Closure;

/**
 * EventHandler class.
 *
 * @author LightSpeedC (Kazuaki Nishizawa; 西澤 和晃)
 */
public class EventHandler 
{
	/**
	 * once. (one time if true)
	 */
	boolean once;
	Closure<?> callback;

	/**
	 * constractor.
	 *
	 * @param callback
	 *            Closure
	 * @param once
	 *            boolean
	 */
	public EventHandler(Closure<?> callback, boolean once) {
		this.callback = callback;
		this.once = once;
	}

	/**
	 * do callback.
	 *
	 * @param args
	 *            Object...
	 */
	public void callback(Object... args) {
		callback.call(args);
	}

}
