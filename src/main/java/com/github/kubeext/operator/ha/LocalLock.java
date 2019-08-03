/**
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.kubeext.operator.ha;

import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.etcd.jetcd.Client;

/**
 * @author wuheng@otcaix.iscas.ac.cn
 * @since Wed Aug 3 17:26:22 CST 2019
 * 
 * This code is from Internet
 **/
public class LocalLock extends AbstractLock {
	
	/**
	 * logger
	 */
	protected final static Logger m_logger = Logger.getLogger(LocalLock.class.getName()); 
	
	/**
	 * default instance
	 */
	protected final static LocalLock m_default = null;
	
	/**
	 * lock
	 */
	protected final static Object m_mutex = new Object();
	
	/**
	 * singleton
	 */
	protected static LocalLock m_singleton = null;
	
	
	/**
	 * hostname
	 */
	protected String hostname;
	
	/**
	 * @param client       etcd client
	 * @param hostname     hostname
	 * @throws Exception   exception
	 */
	private LocalLock() throws Exception {
		super();
		this.hostname = InetAddress
				.getLocalHost().getHostName();
	}

	/**
	 * @param client     etcd client
	 * @param hostname   hostname
	 * @return           DistributedLock object
	 * @throws Exception exception
	 */
	public static LocalLock getInstance() {
		synchronized (m_mutex) {
			if (null == m_singleton) {
				try {
					m_singleton = new LocalLock();
				} catch (Exception e) {
					m_logger.log(Level.SEVERE, "cannot instantiate "
							+ "this object: " + e);
					return m_default;
				}
			}
		}
		return m_singleton;
	}

	/**
	 * 加锁操作，需要注意的是，本例中没有加入重试机制，加锁失败将直接返回。
	 * 
	 * @param lockName: 针对某一共享资源(数据、文件等)制定的锁名
	 * @param TTL       : Time To Live，租约有效期，一旦客户端崩溃，可在租约到期后自动释放锁
	 * @return LockResult LockResult
	 */
	public LockResult lock(String lockName,  long TTL) {
		LockResult lockResult = new LockResult();
		m_logger.log(Level.INFO, "[lock]: lock successfully." + hostname);
		lockResult.setSuccess(true);
		return lockResult;
	}

	/**
	 * 解锁操作，释放锁、关闭定时任务、解除租约
	 * 
	 * @param lockName:锁名
	 * @param lockResult:加锁操作返回的结果
	 */
	public void unLock(String lockname, LockResult lockResult) {
		m_logger.log(Level.INFO, "[unlock]: unlock successfully." + hostname);
	}


}