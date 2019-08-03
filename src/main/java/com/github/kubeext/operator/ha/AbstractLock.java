/**
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.kubeext.operator.ha;

import java.util.concurrent.ScheduledExecutorService;

/**
 * @author wuheng@otcaix.iscas.ac.cn
 * @since Wed Aug 3 17:26:22 CST 2019
 * 
 * This code is from Internet
 **/
public abstract class AbstractLock {
	
	/**
	 * 加锁操作，需要注意的是，本例中没有加入重试机制，加锁失败将直接返回。
	 * 
	 * @param lockName: 针对某一共享资源(数据、文件等)制定的锁名
	 * @param TTL       : Time To Live，租约有效期，一旦客户端崩溃，可在租约到期后自动释放锁
	 * @return LockResult LockResult
	 */
	public abstract LockResult lock(String lockName,  long TTL);

	/**
	 * 解锁操作，释放锁、关闭定时任务、解除租约
	 * 
	 * @param lockName:锁名
	 * @param lockResult:加锁操作返回的结果
	 */
	public abstract void unLock(String lockname, LockResult lockResult);

	/**
	 * 该class用于描述加锁的结果，同时携带解锁操作所需参数
	 * 
	 */
	public static class LockResult {
		private boolean success;
		private long leaseId;
		private ScheduledExecutorService service;

		LockResult() {
			super();
		}

		public boolean isSuccess() {
			return success;
		}

		public void setSuccess(boolean success) {
			this.success = success;
		}

		public long getLeaseId() {
			return leaseId;
		}

		public void setLeaseId(long leaseId) {
			this.leaseId = leaseId;
		}

		public ScheduledExecutorService getService() {
			return service;
		}

		public void setService(ScheduledExecutorService service) {
			this.service = service;
		}

	}

}