package org.yawlfoundation.cluster.backend.service.monitor;

import java.io.Serializable;

/**
 * Created by fantasy on 2016/7/18.
 */
public class EngineVO implements Serializable {
	private static final long serialVersionUID = -6970967506712260305L;
	private String url;
	private String engine_id;
	private String engine_role;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getEngine_id() {
		return engine_id;
	}

	public void setEngine_id(String engine_id) {
		this.engine_id = engine_id;
	}

	public String getEngine_role() {
		return engine_role;
	}

	public void setEngine_role(String engine_role) {
		this.engine_role = engine_role;
	}
}
