/**
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.kubesys.operator.utils;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.github.kubesys.operator.KubesysController;

import io.etcd.jetcd.Client;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

/**
 * @author wuheng@otcaix.iscas.ac.cn
 * @since Wed July 13 17:26:22 CST 2019
 * 
 **/
public class ClientUtils {

	/**
	 * @param token         default file is /etc/kubernetes/admin.conf
	 * @return              kubernetes client
	 * @throws Exception    exception
	 */
	public static KubernetesClient getKubeClient(File token) throws Exception {
		Map<String, Object> map = new Yaml().load(new FileInputStream(token));
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Map<String, Map<String, Object>> clusdata = 
				(Map<String, Map<String, Object>>)
				((List) map.get("clusters")).get(0);
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Map<String, Map<String, Object>> userdata = 
				(Map<String, Map<String, Object>>)
				((List) map.get("users")).get(0);
		
		Config config = new ConfigBuilder()
				.withApiVersion("v1")
				.withCaCertData((String) clusdata.get("cluster").get("certificate-authority-data"))
				.withClientCertData((String) userdata.get("user").get("client-certificate-data"))
				.withClientKeyData((String) userdata.get("user").get("client-key-data"))
				.withMasterUrl((String) clusdata.get("cluster").get("server"))
				.build();
		
		return new DefaultKubernetesClient(config);
	}
	
}
