package org.brahms5.client

import groovy.json.JsonOutput
import groovy.util.logging.Slf4j

import org.brahsm5.commons.Constants
import org.vertx.java.core.Vertx
import org.vertx.java.core.http.HttpClient

import com.lightspeedworks.events.EventEmitter


@Slf4j
public class AsyncClient
{
	//final static def sNodes = (1..20).collect { i -> "mnode${i}"}
	HttpClient mHttpClient = null;
	Vertx mVertx = null;
	EventEmitter mEventEmitter = new EventEmitter();
	
	
	public AsyncClient(Vertx vertx)
	{
		log.trace JsonOutput.toJson(Constants.NODES)
		log.trace "Constructor"
		mVertx = vertx;
		mHttpClient = vertx.createHttpClient()
		.setHost(sNodes[0])
		.setPort(Constants.PORT)
		.setKeepAlive(true);
	}
	
	public AsyncClient connect()
	{
		mEventEmitter.emit("connect", null);
		return this;
	}
	
	
	public EventEmitter getEventEmitter()
	{
		return mEventEmitter;
	}
	
}
