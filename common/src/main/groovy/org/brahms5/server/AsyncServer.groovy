package org.brahms5.server

import groovy.util.logging.Slf4j

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler
import org.vertx.java.core.Handler
import org.vertx.java.core.Vertx
import org.vertx.java.core.http.HttpServer
import org.vertx.java.core.http.HttpServerRequest

import com.lightspeedworks.events.EventEmitter


@Slf4j
class AsyncServer {
	final int PORT = 8888;
	Vertx mVertx = null
	EventEmitter mEventEmitter = new EventEmitter();
	HttpServer mHttpServer = null;

	public AsyncServer(Vertx vertx) {
		log.trace "Constructor"
		mVertx = vertx

		mHttpServer = mVertx.createHttpServer();
		mHttpServer.requestHandler(new Handler<HttpServerRequest>() {
					public void handle(HttpServerRequest request) {
						log.info("A request has arrived on the server!");
						request.response().end();
					}
				})
		mHttpServer.listen(PORT, new AsyncResultHandler<Void>() {
					public void handle(AsyncResult<HttpServer> asyncResult) {
						log.trace "Listening."
					}
				})
	}


	public EventEmitter getEventEmitter() {
		return mEventEmitter;
	}
}
