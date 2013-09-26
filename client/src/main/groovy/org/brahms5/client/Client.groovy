package org.brahms5.client

import groovy.json.JsonOutput
import groovy.util.logging.Slf4j

import org.brahsm5.commons.Constants

import com.hazelcast.client.HazelcastClient
import com.hazelcast.client.config.ClientConfig
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IQueue
import com.lightspeedworks.events.EventEmitter


@Slf4j
public class Client extends EventEmitter
{	
	HazelcastInstance mHazlecast = null;
	UUID mUuid = UUID.randomUUID()
	IQueue mAnswerQueue = null;
	String mAnswerQueueName = mUuid.toString() + ".answer"
	public Client()
	{
		log.trace "Constructor"
	}
	
	public Client connect()
	{
		this.emit("connect", null);
		def cfg = new ClientConfig();
		mHazlecast = HazelcastClient.newHazelcastClient(cfg);
		mAnswerQueue = mHazlecast.getQueue(mAnswerQueueName);
		mAnswerQueue.poll()
		log.info "Answer queue is: $mAnswerQueueName"
		return this;
	}
	
	public shutdown()
	{
		try
		{	
			log.info "Destroying answer queue: ${mAnswerQueueName}"
			mAnswerQueue.destroy()
		}
		catch(ex)
		{
			log.warn ("Error destroying queue.")
		}
		finally
		{
			mHazlecast.getLifecycleService().shutdown()
		}
	}
	
}
