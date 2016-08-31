package org.yawlfoundation.cluster.backend.service.monitor;

import java.util.List;

/**
 * Created by fantasy on 2016/8/18.
 */
public class ResourceStat {
    private List<MasterStat> masters;
    private List<SlaveStat> slaves;

    public List<MasterStat> getMasters() {
        return masters;
    }

    public void setMasters(List<MasterStat> masters) {
        this.masters = masters;
    }

    public List<SlaveStat> getSlaves() {
        return slaves;
    }

    public void setSlaves(List<SlaveStat> slaves) {
        this.slaves = slaves;
    }

    public static class MasterStat {
        private String engineId;
        private String address;
        private String role;
        private String containerHandler;

        public String getEngineId() {
            return engineId;
        }

        public void setEngineId(String engineId) {
            this.engineId = engineId;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContainerHandler() {
            return containerHandler;
        }

        public void setContainerHandler(String containerHandler) {
            this.containerHandler = containerHandler;
        }
    }

    public static class SlaveStat {
        private String engineId;
        private String address;
        private String slaveId;
        private String containerHandler;

        public String getEngineId() {
            return engineId;
        }

        public void setEngineId(String engineId) {
            this.engineId = engineId;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getSlaveId() {
            return slaveId;
        }

        public void setSlaveId(String slaveId) {
            this.slaveId = slaveId;
        }

        public String getContainerHandler() {
            return containerHandler;
        }

        public void setContainerHandler(String containerHandler) {
            this.containerHandler = containerHandler;
        }
    }
}
