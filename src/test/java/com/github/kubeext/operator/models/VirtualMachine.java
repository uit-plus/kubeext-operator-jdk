/**
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.kubeext.operator.models;

import io.fabric8.kubernetes.client.CustomResource;


/**
 * @author wuheng@otcaix.iscas.ac.cn
 * @author xuyuanjia2017@otcaix.iscas.ac.cn
 * @author xianghao16@otcaix.iscas.ac.cn
 * @author yangchen18@otcaix.iscas.ac.cn
 * @since Thu Jun 13 21:39:55 CST 2019
 **/
public class VirtualMachine extends CustomResource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4669199871773009651L;
	
	private VirtualMachineSpec spec;

	public void setSpec(VirtualMachineSpec spec) {
		this.spec = spec;
	}

	public VirtualMachineSpec getSpec() {
		return this.spec;
	}
}

