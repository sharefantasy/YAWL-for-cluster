package cluster.entity;

import cluster.event.exceptions.GeneralException;

import java.util.*;

/**
 * Created by fantasy on 2016/1/5.
 */


// FIXME: 2016/1/11 this constructor is not suitable for message collection usage. Need a better edition
public class Host {

    private long id;
    private String name;
    private List<EngineRole> engineList;

    private Set<HostCapability> capabilitySet;
    private double capacitySpeed;
    private double currentSpeed;
    private Date recordTime;
    private HashMap<Date, Double> historySpeed;

    public Host() {
    }

    public Host(String name, double capacitySpeed) {
        this.name = name;
        this.capacitySpeed = capacitySpeed;
        historySpeed = new HashMap<>();
        engineList = new ArrayList<>();
        capabilitySet = new HashSet<>();
    }

    public Host(String name, double capacitySpeed, List<EngineRole> engines) {
        this.name = name;
        this.capacitySpeed = capacitySpeed;
        this.historySpeed = new HashMap<>();
        this.engineList = engines;
        capabilitySet = new HashSet<>();
    }
    public void setEngineList(List<EngineRole> engineList) {
        this.engineList = engineList;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


    public double getCapability(int engineNumber){
        Optional<HostCapability> c1 = (capabilitySet.stream().filter((c)->c.geteNum() == engineNumber).findFirst());
        if(c1.isPresent()){
            return c1.get().getCapability();
        }
        capabilitySet.add(new HostCapability(this,engineNumber));
        return 0;
    }
    public void setCapability(int engineNumber, double capability){
        Optional<HostCapability> c1 = (capabilitySet.stream().filter((c)->c.geteNum() == engineNumber).findFirst());
        if(c1.isPresent()){
            c1.get().setCapability(capability);
        }
        capabilitySet.add(new HostCapability(this,engineNumber));
    }

    public void setCapacitySpeed(double capacitySpeed) {
        this.capacitySpeed = capacitySpeed;
    }


    //timely updated by speedchecker
    public void updateSpeed() {
        historySpeed.put(recordTime, currentSpeed);
        currentSpeed = 0;
        for (EngineRole e: engineList){
            currentSpeed+=e.getCurrentSpeed();
        }
        recordTime = new Date();
    }
    public void addEngine(EngineRole e) throws GeneralException{
        if (e != null) {
            if (!engineList.contains(e)){
                engineList.add(e);
            }
            else {
                throw new GeneralException("duplicated engine");
            }
        }

    }
    public void removeEngine(EngineRole e) throws GeneralException{
        if (e != null) {
            if (engineList.contains(e)){
                engineList.remove(e);
            }
            else {
                throw new GeneralException("invalid engine");
            }
        }
    }
    public List<EngineRole> getEngineList() {
        return engineList;
    }

    public double getCapacitySpeed() {
        return capacitySpeed;
    }
    public double getCurrentSpeed() {
        return currentSpeed;
    }

    public boolean equals(Host host){
        return this.name.equals(host.name);
    }

    public Set<HostCapability> getCapabilitySet() {
        return capabilitySet;
    }

    public void setCapabilitySet(Set<HostCapability> capabilitySet) {
        this.capabilitySet = capabilitySet;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Host{" +
                "name='" + name + '\'' +
                '}';
    }



}
