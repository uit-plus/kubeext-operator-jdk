/**
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.kubesys.operator.models;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.kubesys.operator.models.vm.Domain;
import com.github.kubesys.operator.models.vm.Lifecycle;

import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.Status;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinitionSpec;

/**
 * @author wuheng@otcaix.iscas.ac.cn
 * @author xuyuanjia2017@otcaix.iscas.ac.cn
 * @author xianghao16@otcaix.iscas.ac.cn
 * @author yangchen18@otcaix.iscas.ac.cn
 * @since Thu Jun 13 21:44:40 CST 2019
 **/
@SuppressWarnings("rawtypes")
@JsonDeserialize(using = JsonDeserializer.None.class)
public class VirtualMachineSpec extends CustomResourceDefinitionSpec implements KubernetesResource {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5134652268821098029L;
	

	protected Domain domain;

	protected Lifecycle lifecycle;
	
	protected Status status;
	
	public VirtualMachineSpec() {

	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	public Lifecycle getLifecycle() {
		return lifecycle;
	}

	public void setLifecycle(Lifecycle lifecycle) {
		this.lifecycle = lifecycle;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
