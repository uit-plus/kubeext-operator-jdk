/**
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.kubesys;

import com.github.kubesys.utils.ClientUtils;

/**
 * @author wuheng@otcaix.iscas.ac.cn
 * @since Wed July 11 17:26:22 CST 2019
 * 
 **/
public class KubesysControllerTest {

	public static void main(String[] args) throws Exception {
		KubesysController controller = new KubesysController(
				ClientUtils.getKubeClient("conf/admin.conf"));
		controller.start();
	}
	
}
