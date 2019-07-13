/**
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.kubesys;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.github.kubesys.KubesysController;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

/**
 * @author wuheng@otcaix.iscas.ac.cn
 * @since Wed July 11 17:26:22 CST 2019
 * 
 **/
public class KubesysControllerTest {

	public static void main(String[] args) throws Exception {
		KubesysController controller = new KubesysController(getClient());
		controller.start(getClient());
	}
	
	public static KubernetesClient getClient() throws Exception {
		String TOKEN    = "admin.conf";
		Map<String, Object> map = new Yaml().load(
				new FileInputStream(new File(TOKEN)));
		@SuppressWarnings("unchecked")
		Map<String, Map<String, Object>> clusdata = (Map<String, Map<String, Object>>)
												((List) map.get("clusters")).get(0);
		@SuppressWarnings("unchecked")
		Map<String, Map<String, Object>> userdata = (Map<String, Map<String, Object>>)
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
