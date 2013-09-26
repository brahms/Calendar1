package org.brahms5.server

import groovy.util.logging.Slf4j

import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import com.lightspeedworks.events.EventEmitter


@Slf4j
class Server {
	HazelcastInstance mHazlecast = null
	public Server() {
		log.trace "Constructor"
		def cfg = new Config();
		mHazlecast = Hazelcast.newHazelcastInstance(cfg);
	}
	
	public shutdown() {
		mHazlecast.getLifecycleService().shutdown();
	}
}
