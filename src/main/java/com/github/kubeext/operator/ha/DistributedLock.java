/**
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.kubeext.operator.ha;

import java.net.InetAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.Lease;

/**
 * @author wuheng@otcaix.iscas.ac.cn
 * @since Wed July 12 17:26:22 CST 2019
 * 
 * This code is from Internet
 **/
public class DistributedLock extends AbstractLock {
	
	/**
	 * logger
	 */
	protected final static Logger m_logger = Logger.getLogger(DistributedLock.class.getName()); 
	
	/**
	 * default instance
	 */
	protected final static DistributedLock m_default = null;
	
	/**
	 * lock
	 */
	protected final static Object m_mutex = new Object();
	
	/**
	 * singleton
	 */
	protected static DistributedLock m_singleton = null;
	
	
	/**
	 * hostname
	 */
	protected String hostname;
	
	/**
	 * etcd client
	 */
	protected final Client client;
	
	/**
	 * @param client       etcd client
	 * @param hostname     hostname
	 * @throws Exception   exception
	 */
	private DistributedLock(Client client) throws Exception {
		super();
		// 创建Etcd客户端，本例中Etcd集群只有一个节点
		this.client = client;
		this.hostname = InetAddress
				.getLocalHost().getHostName();
	}

	/**
	 * @param client     etcd client
	 * @param hostname   hostname
	 * @return           DistributedLock object
	 * @throws Exception exception
	 */
	public static DistributedLock getInstance(Client client) {
		synchronized (m_mutex) {
			if (null == m_singleton) {
				try {
					m_singleton = new DistributedLock(client);
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
		/* 1.准备阶段 */
		// 创建一个定时任务作为“心跳”，保证等待锁释放期间，租约不失效；
		// 同时，一旦客户端发生故障，心跳便会停止，锁也会因租约过期而被动释放，避免死锁
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

		// 初始化返回值lockResult
		lockResult.setSuccess(false);
		lockResult.setService(service);

		// 记录租约ID，初始值设为 0L
		Long leaseId = 0L;

		/* 2.创建租约 */
		// 创建一个租约，租约有效期为TTL，实际应用中根据具体业务确定。
		try {
			leaseId = client.getLeaseClient().grant(TTL).get().getID();
			lockResult.setLeaseId(leaseId);

			// 启动定时任务续约，心跳周期和初次启动延时计算公式如下，可根据实际业务制定。
			long period = TTL - TTL / 5;
			service.scheduleAtFixedRate(new KeepAliveTask(client.getLeaseClient(), leaseId), period, period, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException e) {
			m_logger.log(Level.SEVERE, "[error]: Create lease failed:" + e);
			return lockResult;
		}

		m_logger.log(Level.INFO, "[lock]: start to lock." + hostname);

		/* 3.加锁操作 */
		// 执行加锁操作，并为锁对应的key绑定租约
		try {
			client.getLockClient().lock(ByteSequence.from(lockName.getBytes()), leaseId).get();
		} catch (InterruptedException | ExecutionException e1) {
			m_logger.log(Level.SEVERE, "[error]: Create lease failed:" + e1);
			return lockResult;
		}
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
		m_logger.log(Level.INFO, "[unlock]: start to unlock." + hostname);
		try {
			// 释放锁
			client.getLockClient().unlock(
					ByteSequence.from(lockname.getBytes())).get();
			// 关闭定时任务
			lockResult.getService().shutdown();
			// 删除租约
			if (lockResult.getLeaseId() != 0L) {
				client.getLeaseClient()
					.revoke(lockResult.getLeaseId());
			}
		} catch (InterruptedException | ExecutionException e) {
			m_logger.log(Level.SEVERE, "[error]: unlock failed: " + e);
		}

		m_logger.log(Level.INFO, "[unlock]: unlock successfully." + hostname);
	}

	/**
	 * 在等待其它客户端释放锁期间，通过心跳续约，保证自己的锁对应租约不会失效
	 *
	 */
	public static class KeepAliveTask implements Runnable {
		private Lease leaseClient;
		private long leaseId;

		KeepAliveTask(Lease leaseClient, long leaseId) {
			this.leaseClient = leaseClient;
			this.leaseId = leaseId;
		}

		@Override
		public void run() {
			// 续约一次
			leaseClient.keepAliveOnce(leaseId);
		}
	}

}