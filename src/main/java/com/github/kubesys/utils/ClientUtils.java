/**
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.kubesys.utils;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.github.kubesys.KubesysController;

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

	public static KubernetesClient getKubeClient(String token) throws Exception {
		Map<String, Object> map = new Yaml().load(
				new FileInputStream(new File(token)));
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
	
	public static Client getEtcdClient() throws Exception {
		KubernetesClient client = getKubeClient(KubesysController.TOKEN);
		Collection<URI> urls = new ArrayList<URI>();
		for (Pod pod : client.pods().inNamespace("kube-system")
				.withLabel("app", "etcd").list().getItems()) {
			System.out.println(pod.getStatus().getPodIP());
			urls.add(new URI("http://" + pod.getStatus().getPodIP() + ":2379"));
		}
		return Client.builder().endpoints(urls).build();
	}
	
	public static String getHostName() throws Exception {
		return InetAddress.getLocalHost().getHostName();
	}
}
