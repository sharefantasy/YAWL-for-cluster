package org.yawlfoundation.yawl.resourcing.datastore.orgdata;

import org.yawlfoundation.yawl.exceptions.YAuthenticationException;
import org.yawlfoundation.yawl.resourcing.resource.*;
import org.yawlfoundation.yawl.util.PasswordEncryptor;

import java.util.HashMap;

/**
 * @author Michael Adams
 * @date 3/05/2014
 */
public class SimpleDataSource extends DataSource {

	public SimpleDataSource() {
		super();
	}

	@Override
	public ResourceDataSet loadResources() {
		ResourceDataSet resourceDataSet = new ResourceDataSet(this);
		resourceDataSet.setCapabilities(new HashMap<String, Capability>(), this);
		resourceDataSet.setOrgGroups(new HashMap<String, OrgGroup>(), this);
		resourceDataSet.setPositions(new HashMap<String, Position>(), this);
		resourceDataSet.setRoles(loadRoles(), this);
		resourceDataSet.setParticipants(loadParticipants(resourceDataSet), this);
		resourceDataSet.setAllowExternalOrgDataMods(false);
		resourceDataSet.setExternalUserAuthentication(true);
		return resourceDataSet;
	}

	public HashMap<String, Role> loadRoles() {
		HashMap<String, Role> hashMap = new HashMap<String, Role>();
		Role r1 = new Role("r1");
		r1.setName("role one");
		Role r2 = new Role("r2");
		r2.setName("role two");
		hashMap.put(r1.getID(), r1);
		hashMap.put(r2.getID(), r2);
		return hashMap;
	}

	public HashMap<String, Participant> loadParticipants(ResourceDataSet resourceDataSet) {
		HashMap<String, Participant> hashMap = new HashMap<String, Participant>();

		Participant p1 = new Participant("First", "Fred", "firstf");
		p1.setID("ff");
		p1.setPassword(PasswordEncryptor.encrypt("apple", ""));
		p1.getUserPrivileges().setCanManageCases(true);
		p1.getUserPrivileges().setCanViewTeamItems(true);
		p1.setAdministrator(true);
		p1.addRole(resourceDataSet.getRole("r1"));
		hashMap.put(p1.getID(), p1);

		Participant p2 = new Participant("Second", "Sam", "seconds");
		p2.setID("ss");
		p2.setPassword(PasswordEncryptor.encrypt("apple", ""));
		p2.getUserPrivileges().setCanReorder(true);
		p2.getUserPrivileges().setCanStartConcurrent(true);
		p2.addRole(resourceDataSet.getRole("r2"));
		hashMap.put(p2.getID(), p2);

		return hashMap;
	}

	@Override
	public void update(Object obj) {
	}

	@Override
	public boolean delete(Object obj) {
		return false;
	}

	@Override
	public String insert(Object obj) {
		return null;
	}

	@Override
	public void importObj(Object obj) {
	}

	@Override
	public int execUpdate(String query) {
		return 0;
	}

	@Override
	public boolean authenticate(String userid, String password) throws YAuthenticationException {
		return false;
	}
}
