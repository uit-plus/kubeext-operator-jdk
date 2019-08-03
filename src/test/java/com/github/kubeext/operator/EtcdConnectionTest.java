/**
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.kubeext.operator;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import com.github.kubeext.operator.utils.ClientUtils;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
/**
 * @author wuheng@otcaix.iscas.ac.cn
 * @since Wed July 20 17:26:22 CST 2019 openssl pkcs8 -topk8 -inform PEM -in
 *        
 * openssl pkcs8 -topk8 -nocrypt -in server.key -out pkcs8-key.pem
 * 
 * yum install openssl *apr*
 **/
public class EtcdConnectionTest {

	
	public static void main(String[] args) throws Exception {

		Collection<URI> urls = new ArrayList<URI>();
		urls.add(new URI("https://10.25.0.145:2379"));
		
		Client etcdcli = ClientUtils.getEtcdClient(urls, new File("conf/ca.crt")
								, new File("conf/server.crt"), new File("conf/server.key"));
		ByteSequence key = ByteSequence.from("v1".getBytes());
		ByteSequence value = ByteSequence.from("v1".getBytes());
		System.out.println(etcdcli.getKVClient().put(key, value).get());
	}

}
