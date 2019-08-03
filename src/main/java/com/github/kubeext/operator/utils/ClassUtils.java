/**
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.kubeext.operator.utils;

import java.util.Map;

import com.github.kubeext.operator.AbstractKubeextWatcher;

import io.fabric8.kubernetes.api.model.Doneable;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;

/**
 * @author wuheng@otcaix.iscas.ac.cn
 * @since Wed July 13 17:26:22 CST 2019
 * 
 **/
public class ClassUtils {

	@SuppressWarnings("rawtypes")
	public static AbstractKubeextWatcher getWatcher(Map<String, String> props) throws Exception {
		return (AbstractKubeextWatcher) Class.forName(props.get("PACKAGE") + "." + props.get("KIND") + "Watcher").newInstance();
	}

	public static Class<? extends HasMetadata> getResourceKindClass(Map<String, String> props) throws Exception {
		return Class.forName(props.get("PACKAGE") + "." + props.get("KIND")).asSubclass(HasMetadata.class);
	}
	
	@SuppressWarnings("rawtypes")
	public static Class<? extends KubernetesResourceList> getResourceListClass(Map<String, String> props) throws Exception {
		return Class.forName(props.get("PACKAGE") + "." + props.get("KIND") + "List").asSubclass(KubernetesResourceList.class);
	}
	
	@SuppressWarnings("rawtypes")
	public static Class<? extends Doneable> getDoneableResourceClass(Map<String, String> props) throws Exception {
		return Class.forName(props.get("PACKAGE") + ".Doneable" + props.get("KIND")).asSubclass(Doneable.class);
	}
}
