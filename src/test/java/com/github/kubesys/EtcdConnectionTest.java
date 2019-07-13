/**
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.kubesys;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.grpc.netty.GrpcSslContexts;
import io.netty.handler.ssl.SslContext;

/**
 * @author wuheng@otcaix.iscas.ac.cn
 * @since Wed July 11 17:26:22 CST 2019
 * openssl pkcs8 -topk8 -inform PEM -in ca.key PEM -nocrypt
 * openssl req -new -x509 -key ca.key -out ca.pem -days 1095
 * 
 * openssl x509 -in cert.csr -out cert.pem -req -signkey key.pem -days 1001
 **/
public class EtcdConnectionTest {

	public static void main(String[] args) throws Exception {
		KubernetesClient client = getClient();
		Collection<URI> urls = new ArrayList<URI>();
		for (Pod pod : client.pods().inNamespace("kube-system")
				.withLabel("app", "etcd").list().getItems()) {
			System.out.println(pod.getStatus().getPodIP());
			urls.add(new URI("http://" + pod.getStatus().getPodIP() + ":32379"));
		}
		Client etcdcli = Client.builder().endpoints(urls).build();
		ByteSequence key = ByteSequence.from("v1".getBytes());
		ByteSequence value = ByteSequence.from("v1".getBytes());
		System.out.println(etcdcli.getKVClient().put(key , value).get());
	}

	public static KubernetesClient getClient() throws Exception {
		String TOKEN = "conf/admin.conf";
		Map<String, Object> map = new Yaml().load(new FileInputStream(new File(TOKEN)));
		@SuppressWarnings("unchecked")
		Map<String, Map<String, Object>> clusdata = (Map<String, Map<String, Object>>) ((List) map.get("clusters"))
				.get(0);
		@SuppressWarnings("unchecked")
		Map<String, Map<String, Object>> userdata = (Map<String, Map<String, Object>>) ((List) map.get("users")).get(0);
		Config config = new ConfigBuilder().withApiVersion("v1")
				.withCaCertData((String) clusdata.get("cluster").get("certificate-authority-data"))
				.withClientCertData((String) userdata.get("user").get("client-certificate-data"))
				.withClientKeyData((String) userdata.get("user").get("client-key-data"))
				.withMasterUrl((String) clusdata.get("cluster").get("server")).build();
		return new DefaultKubernetesClient(config);
	}

}
