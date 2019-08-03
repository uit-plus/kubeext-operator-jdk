/**
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.kubesys.operator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.kubesys.operator.utils.ClassUtils;
import com.github.kubesys.operator.utils.ClientUtils;

import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.internal.KubernetesDeserializer;

/**
 * @author wuheng@otcaix.iscas.ac.cn
 * @since Wed July 26 17:26:22 CST 2019
 * 
 **/
public abstract class AbstractKubeextController {
	
	/**
	 * logger
	 */
	public final static Logger m_logger = Logger.getLogger(AbstractKubeextController.class.getName());

	/**
	 * Kubernetes client
	 */
	protected final KubernetesClient client;

	/**
	 * init client
	 * @throws Exception 
	 */
	public AbstractKubeextController() throws Exception {
		this.client = ClientUtils.getKubeClient(
						new File(getTokenFile()));
	}
	
	/**
	 * Start controller
	 * 
	 */
	protected void start() {
		try {
			
			Map<String, String> data = client.configMaps()
								.inNamespace(getNamespace())
								.withName(getConfigmap())
								.get().getData();
			
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
					+ "please see the ConfigMap " + getConfigmap());
		}
	}


	/**
	 * @param props loaded props
	 * @throws Exception Java.Null.Exception
	 */
	@SuppressWarnings("rawtypes")
	protected void doStart(Map<String, String> props) {
		try {
			AbstractKubeextWatcher watcher = ClassUtils.getWatcher(props);
			doRegister(props.get("KIND"), getKindVersion(props), 
					ClassUtils.getResourceKindClass(props));
			doListener(watcher, props);
			m_logger.log(Level.INFO, "Start Watcher<" 
					+ props.get("KIND") + "> successfull.");
		} catch (Exception ex) {
			m_logger.log(Level.SEVERE, "Start Watcher<" 
						+ props.get("KIND") +"> failed! Unable to load classes, "
								+ "please check the ConfigMap " + getConfigmap());
		}
	}


	@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
	protected void doListener(AbstractKubeextWatcher watcher, Map<String, String> props) throws Exception {
		CustomResourceDefinition crd = client
						.customResourceDefinitions()
						.withName(getKindName(props)).get();
		MixedOperation listener = (MixedOperation) client.customResource(crd, 
				ClassUtils.getResourceKindClass(props), 
				ClassUtils.getResourceListClass(props),
				ClassUtils.getDoneableResourceClass(props))
						.inAnyNamespace();
		listener.watch(watcher);
	}


	@SuppressWarnings({ "rawtypes", "unchecked", })
	protected void doRegister(String kind, String version, Class<?> clazz) throws Exception {
		KubernetesDeserializer.registerCustomKind(version, kind, 
				(Class<? extends KubernetesResource>) clazz);
	}

	/**
	 * @param props         props
	 * @return              version
	 */
	protected String getKindVersion(Map<String, String> props) {
		return props.get("GROUP") 
				+ "/" + props.get("VERSION");
	}
	
	/**
	 * @param props       props
	 * @return            name
	 */
	protected String getKindName(Map<String, String> props) {
		return props.get("PLURAL") + "." + props.get("GROUP");
	}
	
	/***************************************
	 * 
	 *       Customized methods
	 * 
	 ***************************************/
	/**
	 * @return namespace
	 */
	public abstract String getNamespace();
	
	/**
	 * @return configmap
	 */
	public abstract String getConfigmap();
	
	/**
	 * @return token
	 */
	public abstract String getTokenFile();
}
