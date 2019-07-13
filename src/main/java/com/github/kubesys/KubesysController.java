/**
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.kubesys;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.kubesys.utils.ClientUtils;

import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.internal.KubernetesDeserializer;

/**
 * @author wuheng@otcaix.iscas.ac.cn
 * @since Wed July 11 17:26:22 CST 2019
 * 
 **/
public class KubesysController {
	
	/**
	 * logger
	 */
	protected final static Logger m_logger = Logger.getLogger(KubesysController.class.getName());

	/**
	 * namespace
	 */
	protected final String NAMESPACE  = "kube-system";
	
	/**
	 * configuration name
	 */
	protected final String CONFIGNAME = "kubesys-config";

	/**
	 * token
	 */
	public final static String TOKEN   = "/etc/kubernetes/admin.conf";
	
	/**
	 * Kubernetes client
	 */
	protected final KubernetesClient client;

	/**
	 * init client
	 * @throws Exception 
	 */
	public KubesysController() throws Exception {
		this.client = ClientUtils.getKubeClient(TOKEN);
	}
	
	/**
	 * @param client
	 */
	public KubesysController(KubernetesClient client) {
		this.client = client;
	}

	/**
	 * Start controller
	 * 
	 */
	protected void start() {
		try {
			Map<String, String> data = client.configMaps()
				.inNamespace(NAMESPACE).withName(CONFIGNAME).get().getData();
			// load properties
			for (String key : data.keySet()) {
				Map<String, String> props = new HashMap<String, String>();
				String[] values = data.get(key).split("\n");
				for (String value : values) {
					String[] pairwise = value.split("=");
					props.put(pairwise[0], pairwise[1]);
				}
				
				// start controller based on properties
				doStart(props);
				
			}
		} catch (Exception ex) {
			m_logger.log(Level.SEVERE, "Start controller failed! "
					+ "Wrong ConfigMap format or configuration,"
					+ "please see the ConfigMap kubesys-config");
		}
	}


	/**
	 * @param props loaded props
	 * @throws Exception Java.Null.Exception
	 */
	@SuppressWarnings("rawtypes")
	protected void doStart(Map<String, String> props) {
		try {
			AbstractWatcher watcher = (AbstractWatcher) 
					Class.forName(props.get("CLASS")).newInstance();
			doRegister(props.get("KIND"), props.get("GROUP") 
					+ "/" + props.get("VERSION"), watcher.getResourceKindClass());
			doListener(watcher, props.get("PLURAL") + "." + props.get("GROUP"));
			m_logger.log(Level.INFO, "Start Watcher<" 
					+ props.get("KIND") + "> successfull.");
		} catch (Exception ex) {
			m_logger.log(Level.SEVERE, "Start Watcher<" 
						+ props.get("KIND") +"> failed! Unable to load classes, "
								+ "please see the ConfigMap kubesys-config");
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
	protected void doListener(AbstractWatcher watcher, String name) throws Exception {
		CustomResourceDefinition crd = client
						.customResourceDefinitions()
						.withName(name).get();
		MixedOperation listener = (MixedOperation) client.customResource(crd, 
								watcher.getResourceKindClass(), 
								watcher.getResourceListClass(), 
								watcher.getDoneableResourceClass())
						.inAnyNamespace();
		listener.watch(watcher);
	}

	@SuppressWarnings({ "rawtypes", "unchecked", })
	protected void doRegister(String kind, String version, Class<?> clazz) throws Exception {
		KubernetesDeserializer.registerCustomKind(version, kind, 
				(Class<? extends KubernetesResource>) clazz);
	}

}
