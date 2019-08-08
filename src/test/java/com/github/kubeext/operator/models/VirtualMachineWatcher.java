/**
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.kubeext.operator.models;

import com.github.kubeext.operator.AbstractKubeextWatcher;
import com.github.kubeext.operator.ha.AbstractLock;
import com.github.kubeext.operator.ha.LocalLock;

/**
 * @author wuheng@otcaix.iscas.ac.cn
 * @author xuyuanjia2017@otcaix.iscas.ac.cn
 * @author xianghao16@otcaix.iscas.ac.cn
 * @author yangchen18@otcaix.iscas.ac.cn
 * @since Thu Jun 13 21:39:55 CST 2019
 **/
public class VirtualMachineWatcher extends AbstractKubeextWatcher<VirtualMachine> {

	public VirtualMachineWatcher(AbstractLock lock) throws Exception {
		super(lock);
		// TODO Auto-generated constructor stub
	}

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

}

