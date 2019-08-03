/**
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.kubesys.operator;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.kubesys.operator.ha.AbstractLock;
import com.github.kubesys.operator.ha.AbstractLock.LockResult;

import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watcher;

/**
 * @author wuheng@otcaix.iscas.ac.cn
 * @since Wed July 28 17:26:22 CST 2019
 * 
 **/
public abstract class AbstractKubeextWatcher<T> implements Watcher<T> {

	/**
	 * logger
	 */
	protected final static Logger m_logger = Logger.getLogger(AbstractKubeextWatcher.class.getName());

	/**
	 * client
	 */
	protected final AbstractLock lock;
	

	public AbstractKubeextWatcher() {
		super();
		this.lock = getLock();
	}

	public void eventReceived(Action action, T resource) {

		LockResult lockResult = lock.lock(getLockName(), 30);
		
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
		 
		 lock.unLock(getLockName(), lockResult);
	}

	public void onClose(KubernetesClientException cause) {
		m_logger.log(Level.SEVERE, cause.toString());
	}
	
	/**
	 * @return  lockname
	 */
	public abstract String getLockName();
	
	/*****************************************************
	 * 
	 *   Resource lifecycle
	 * 
	 ******************************************************/
	
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
	 * @return  lock
	 */
	protected abstract AbstractLock getLock();
	
}
