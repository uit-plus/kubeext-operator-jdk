/**
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.kubesys.operator;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.kubesys.operator.ha.DistributedLock;
import com.github.kubesys.operator.ha.DistributedLock.LockResult;

import io.etcd.jetcd.Client;
import io.fabric8.kubernetes.api.model.Doneable;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watcher;

/**
 * @author wuheng@otcaix.iscas.ac.cn
 * @since Wed July 11 17:26:22 CST 2019
 * 
 **/
public abstract class AbstractWatcher<T> implements Watcher<T> {

	/**
	 * logger
	 */
	protected final static Logger m_logger = Logger.getLogger(AbstractWatcher.class.getName());

	protected final static String LOCK = "/kubesys/extfrk/ha";
	
	protected final Client client;
	
	public AbstractWatcher(Client client) {
		super();
		this.client = client;
	}

	public void eventReceived(Action action, T resource) {

		LockResult lockResult = DistributedLock.getInstance(client).lock(LOCK, 30);
		
		 if (lockResult.isSuccess()) {
			 if (action == Watcher.Action.ADDED) {
					m_logger.log(Level.INFO, "Create: " + resource);
					createResource(resource);
				} else if (action == Watcher.Action.MODIFIED) {
					m_logger.log(Level.INFO, "Update: " + resource);
					updateResource(resource);
				} else if (action == Watcher.Action.DELETED) {
					m_logger.log(Level.INFO, "Remove: " + resource);
					removeResource(resource);
				} else {
					m_logger.log(Level.SEVERE, "Unknown reason.");
				}
		 }
		 
		 DistributedLock.getInstance(client).unLock(LOCK, lockResult);
	}

	public void onClose(KubernetesClientException cause) {
		m_logger.log(Level.SEVERE, cause.toString());
	}
	
	/**
	 * @param resource resource
	 */
	public abstract void createResource(T resource);
	
	/**
	 * @param resource resource
	 */
	public abstract void updateResource(T resource);
	
	/**
	 * @param resource resource
	 */
	public abstract void removeResource(T resource);
	
	/**
	 * @return ResourceKindClass
	 */
	public abstract Class<? extends HasMetadata> getResourceKindClass();
	
	/**
	 * @return ResourceListClass
	 */
	@SuppressWarnings("rawtypes")
	public abstract Class<? extends KubernetesResourceList> getResourceListClass();
	
	/**
	 * @return DoneableResourceClass
	 */
	@SuppressWarnings("rawtypes")
	public abstract Class<? extends Doneable> getDoneableResourceClass();
	
}
