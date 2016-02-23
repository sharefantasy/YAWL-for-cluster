package cluster.hostTester.entity;

import cluster.general.entity.Host;
import cluster.general.entity.Tenant;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by fantasy on 2016/1/31.
 */
public class TestPlanEntity {

    private long id;
    private Host host;
    private int engineNumber;
    //    @DateTimeFormat(pattern = "yyyy/MM/dd hh:mm:ss")
    private Date startTime;

    //    @DateTimeFormat(pattern = "yyyy/MM/dd hh:mm:ss")
    private Date endTime;

    private Tenant testTenant;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public int getEngineNumber() {
        return engineNumber;
    }

    public void setEngineNumber(int engineNumber) {
        this.engineNumber = engineNumber;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    private boolean isFinished;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof TestPlanEntity))
            return false;
        TestPlanEntity other = (TestPlanEntity) obj;
        if (id != other.id)
            return false;
        if (host == null) {
            if (other.host != null)
                return false;
        } else if (!host.equals(other.host))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("TestPlan{id=%d, host=%s, engineNumber=%d}",
                id, host, engineNumber);
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Tenant getTestTenant() {
        return testTenant;
    }

    public void setTestTenant(Tenant testTenant) {
        this.testTenant = testTenant;
    }
}
