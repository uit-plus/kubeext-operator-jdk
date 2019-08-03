/**
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.kubesys.operator.models;

import com.github.kubesys.operator.AbstractKubeextWatcher;
import com.github.kubesys.operator.ha.AbstractLock;
import com.github.kubesys.operator.ha.LocalLock;

/**
 * @author wuheng@otcaix.iscas.ac.cn
 * @author xuyuanjia2017@otcaix.iscas.ac.cn
 * @author xianghao16@otcaix.iscas.ac.cn
 * @author yangchen18@otcaix.iscas.ac.cn
 * @since Thu Jun 13 21:39:55 CST 2019
 **/
public class VirtualMachineWatcher extends AbstractKubeextWatcher<VirtualMachine> {

	@Override
	public String getLockName() {
		return "test";
	}

	@Override
	public void createResource(VirtualMachine resource) {
		System.out.println(resource);
		
	}

	@Override
	public void updateResource(VirtualMachine resource) {
		System.out.println(resource);
	}

	@Override
	public void removeResource(VirtualMachine resource) {
		System.out.println(resource);
	}

	@Override
	protected AbstractLock getLock() {
		return LocalLock.getInstance();
	}

}

