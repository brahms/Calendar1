package com.lightspeedworks.events;

import groovy.lang.Closure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * events.EventEmitter class. (like Node.js)
 *
 * @see http://nodejs.org/api/events.html
 * @see http://nodejs.jp/nodejs.org_ja/api/events.html (Japanese)
 *
 * @see http://yand.info/?p=/docs/events.html#Events
 * @see http://jp.yand.info/?p=/docs/events.html#Events (Japanese)
 *
 * @author LightSpeedC (Kazuaki Nishizawa; 西澤 和晃)
 */
public class EventEmitter {
	/**
	 * max listners count.
	 */
	int maxListeners = 10;
	/**
	 * array list of events and listeners.
	 */
	Map<String, List<EventHandler>> events = new HashMap<String, List<EventHandler>>();

	/**
	 * add listener.
	 *
	 * @param event
	 *            String
	 * @param listener
	 *            Closure
	 */
	public void addListener(String event, Closure<?> listener) {
		addListener(event, listener, false);
	}

	/**
	 * add listener. (internal)
	 *
	 * @param event
	 *            String
	 * @param listener
	 *            Closure
	 * @param once
	 *            boolean
	 */
	void addListener(String event, Closure<?> listener, boolean once) {
		List<EventHandler> handlers = listeners(event);

		if (handlers == null) {
			handlers = new ArrayList<EventHandler>();
			events.put(event, handlers);
		}

		if (maxListeners > 0 && handlers.size() >= maxListeners)
			throw new Error("too many listeners for event '" + event + "'");

		emit("newListener", event, listener);
		handlers.add(new EventHandler(listener, once));
	}

	/**
	 * on. (add listener)
	 *
	 * @param event
	 *            String
	 * @param listener
	 *            Closure
	 */
	public void on(String event, Closure<?> listener) {
		addListener(event, listener, false);
	}

	/**
	 * once.
	 *
	 * @param event
	 *            String
	 * @param listener
	 *            Closure
	 */
	public void once(String event, Closure<?> listener) {
		addListener(event, listener, true);
	}

	/**
	 * remove listener.
	 *
	 * @param event
	 *            String
	 * @param listener
	 *            Closure
	 */
	public void removeListener(String event, Closure<?> listener) {
		List<EventHandler> handlers = listeners(event);

		if (handlers == null)
			return;

		for (int i = 0, n = handlers.size(); i < n; ++i) {
			if (handlers.get(i).callback == listener) {
				handlers.remove(i);
				emit("removeListener", event, listener);
				break;
			}
		}

		if (handlers.size() == 0)
			events.remove(event);
	}

	/**
	 * remove all listeners with event.
	 *
	 * @param event
	 *            String
	 */
	public void removeAllListeners(String event) {
		List<EventHandler> handlers = listeners(event);

		if (handlers == null)
			return;

		events.remove(event);

		for (EventHandler handler : handlers)
			emit("removeListener", event, handler.callback);

		handlers.clear();
	}

	/**
	 * remove all listeners.
	 */
	public void removeAllListeners() {
		String[] events = new String[this.events.size()];
		int i = 0;
		for (String event : this.events.keySet())
			events[i++] = event;
		for (String event : events)
			removeAllListeners(event);
	}

	/**
	 * set max listeners count.
	 *
	 * By default EventEmitters will print a warning if more than 10 listeners
	 * are added for a particular event. This is a useful default which helps
	 * finding memory leaks. Obviously not all Emitters should be limited to 10.
	 * This function allows that to be increased. Set to zero for unlimited.
	 *
	 * @param maxListeners
	 *            int
	 */
	public void setMaxListeners(int maxListeners) {
		if (maxListeners < 0)
			throw new Error("max listeners count must be 0 or more");
		this.maxListeners = maxListeners;
	}

	/**
	 * returns an array list of listeners (event handlers) for specified event.
	 *
	 * @param event
	 *            String
	 * @return List<EventHandler>
	 */
	public List<EventHandler> listeners(String event) {
		return events.get(event);
	}

	/**
	 * emit (fire) event.
	 *
	 * @param event
	 *            String
	 * @param args
	 *            Object...
	 */
	public void emit(String event, Object... args) {
		List<EventHandler> handlers = listeners(event);

		if (handlers == null || handlers.size() == 0) {
			if (!event.equals("error"))
				return;

			StringBuilder sb = new StringBuilder("error");
			if (args.length > 0)
				sb.append(":");
			for (Object arg : args)
				sb.append(" " + arg.toString());
			throw new Error(sb.toString());
		}

		for (int i = 0, n = handlers.size(); i < n;) {
			EventHandler handler = handlers.get(i);

			if (handler.once) {
				handlers.remove(i);
				--n;
			} else
				++i;

			handler.callback(args);
		}

		if (handlers.size() == 0)
			events.remove(event);
	}

	/**
	 * return the number of listeners for a given event.
	 *
	 * @param event
	 *            String
	 * @return listener count
	 */
	public int listenerCount(String event) {
		List<EventHandler> handlers = listeners(event);

		if (handlers == null)
			return 0;

		return handlers.size();
	}

	/**
	 * return the number of listeners for a given event.
	 *
	 * @param emitter
	 *            EventEmitter
	 * @param event
	 *            String
	 * @return listener count
	 */
	public static int listenerCount(EventEmitter emitter, String event) {
		return emitter.listenerCount(event);
	}

	/**
	 * dump.
	 */
	public void dump() {
		for (String event : events.keySet()) {
			System.out.print("event: " + event + ", ");
			System.out.print("listeners:");
			List<EventHandler> handlers = events.get(event);
			for (EventHandler handler : handlers)
				System.out.print(" " + handler.callback + "." + handler.once);
			System.out.println();
		}
	}
}
