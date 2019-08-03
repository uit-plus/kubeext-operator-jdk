/**
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.kubeext.operator.models;

import io.fabric8.kubernetes.api.builder.Function;
import io.fabric8.kubernetes.client.CustomResourceDoneable;


/**
 * @author wuheng@otcaix.iscas.ac.cn
 * @author xuyuanjia2017@otcaix.iscas.ac.cn
 * @author xianghao16@otcaix.iscas.ac.cn
 * @author yangchen18@otcaix.iscas.ac.cn
 * @since Thu Jun 13 21:36:39 CST 2019
 **/
public class DoneableVirtualMachine extends CustomResourceDoneable<VirtualMachine> {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DoneableVirtualMachine(VirtualMachine resource, Function function) {
		super(resource, function);
	}
}

