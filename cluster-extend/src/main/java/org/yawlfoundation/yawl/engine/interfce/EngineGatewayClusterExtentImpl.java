package org.yawlfoundation.yawl.engine.interfce;

import org.yawlfoundation.plugin.persistX.DynamicSourcePersistenceManager;
import org.yawlfoundation.yawl.engine.YEngine;
import org.yawlfoundation.yawl.engine.YEngineClusterExtent;
import org.yawlfoundation.yawl.exceptions.YPersistenceException;

/**
 * Created by fantasy on 2016/7/18.
 */
public class EngineGatewayClusterExtentImpl extends EngineGatewayImpl implements EngineGatewayClusterExtent {
	protected YEngineClusterExtent _engine;
	protected DynamicSourcePersistenceManager _pm;
	public EngineGatewayClusterExtentImpl(boolean persist) throws YPersistenceException {
		super(persist);
		_pm = (DynamicSourcePersistenceManager) YEngineClusterExtent.getPersistenceManager();
	}

	public EngineGatewayClusterExtentImpl(boolean persist, boolean gatherHbnStats) throws YPersistenceException {
		super(persist, gatherHbnStats);
		_pm = (DynamicSourcePersistenceManager) YEngineClusterExtent.getPersistenceManager();
		_engine = (YEngineClusterExtent) YEngine.getInstance(persist, gatherHbnStats);
	}

	@Override
	public void restore(String session) {
		try {
			_engine.restore();
		} catch (YPersistenceException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void shutdown(String session) {
		_engine.shutdown();
	}

	@Override
	public String migrateRole(String session, String role) {
		if (_pm.getEngineRole().equals(role)) {
			return "The same role";
		}
		if (_engine.isMigrating()) {
			return "migrating";
		}
		boolean result = _pm.setMaster(role);
		if (result) {
			_engine.setMigrating(true);
			return "migrate to " + role;
		} else {
			return "migration failed";
		}
	}

	@Override
	public String getMigrationStatus(String session) {
		return _engine.isMigrating() ? "migrating to" + _pm.getEngineRole() : "stable";

	}
}
