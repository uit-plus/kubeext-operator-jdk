/**
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.kubesys.operator.models;

import com.github.kubesys.operator.AbstractKubeextWatcher;

import io.etcd.jetcd.Client;


/**
 * @author wuheng@otcaix.iscas.ac.cn
 * @author xuyuanjia2017@otcaix.iscas.ac.cn
 * @author xianghao16@otcaix.iscas.ac.cn
 * @author yangchen18@otcaix.iscas.ac.cn
 * @since Thu Jun 13 21:39:55 CST 2019
 **/
public class VirtualMachineWatcher extends AbstractKubeextWatcher<VirtualMachine> {

	public VirtualMachineWatcher(Client client) {
		super(client);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getLockName() {
		return null;
	}

	@Override
	public void createResource(VirtualMachine resource) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateResource(VirtualMachine resource) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeResource(VirtualMachine resource) {
		
	}

}

