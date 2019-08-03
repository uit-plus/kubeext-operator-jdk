/**
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.kubeext.operator;

import com.github.kubeext.operator.AbstractKubeextController;

/**
 * @author wuheng@otcaix.iscas.ac.cn
 * @since Wed July 31 17:26:22 CST 2019 
 *        
 **/
public class DefaultKubeextControllerTest extends AbstractKubeextController {

	public static void main(String[] args) throws Exception {
		DefaultKubeextControllerTest kct = new DefaultKubeextControllerTest();
		kct.start();
	}
	
	public DefaultKubeextControllerTest() throws Exception {
		super();
	}

	@Override
	public String getNamespace() {
		return "kube-system";
	}

	@Override
	public String getConfigmap() {
		return "kubeext-config";
	}

	@Override
	public String getTokenFile() {
		return "conf/admin.conf";
	}

}
