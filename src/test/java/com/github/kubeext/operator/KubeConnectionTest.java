/**
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.kubeext.operator;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import com.github.kubeext.operator.utils.ClientUtils;

import io.fabric8.kubernetes.client.KubernetesClient;

/**
 * @author wuheng@otcaix.iscas.ac.cn
 * @since Wed July 20 17:26:22 CST 2019 openssl pkcs8 -topk8 -inform PEM -in
 *        
 * openssl pkcs8 -topk8 -nocrypt -in server.key -out pkcs8-key.pem
 * 
 * yum install openssl *apr*
 **/
public class KubeConnectionTest {

	
	public static void main(String[] args) throws Exception {

		KubernetesClient client = ClientUtils.getKubeClient(
				new File("conf/admin.conf"));
		System.out.println(client.configMaps().inNamespace("kube-system")
				.withName("kubeext-config").get().getData());
	}

}
