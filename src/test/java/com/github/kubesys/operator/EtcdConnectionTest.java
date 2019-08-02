/**
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.kubesys.operator;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import com.github.kubesys.operator.utils.ClientUtils;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslProvider;

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
		KubernetesClient client = ClientUtils.getKubeClient(new File("conf/admin.conf"));
		Collection<URI> urls = new ArrayList<URI>();
		for (Pod pod : client.pods().inNamespace("kube-system")
				.withLabel("app", "etcd").list().getItems()) {
			System.out.println(pod.getStatus().getPodIP());
			urls.add(new URI("https://" + pod.getStatus().getPodIP() + ":2379"));
		}
		
		SslContext sslContext = SslContext.newServerContext(
				SslProvider.JDK, 
				new File("conf/ca.crt"), 
				new File("conf/server.key"));
		Client etcdcli = Client.builder()
				.sslContext(sslContext  )
				.endpoints(urls).build();
		ByteSequence key = ByteSequence.from("v1".getBytes());
		ByteSequence value = ByteSequence.from("v1".getBytes());
		System.out.println(etcdcli.getKVClient().put(key , value).get());
	}


}
