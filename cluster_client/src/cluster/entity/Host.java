package cluster.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fantasy on 2016/1/5.
 */


// FIXME: 2016/1/11 this constructor is not suitable for message collection usage. Need a better edition
public class Host {
    public String getName() {
        return name;
    }

    private String name;

    private List<EngineRole> engineList;
    private double capacitySpeed;
    private double currentSpeed;
    private Date recordTime;
    private HashMap<Date, Double> historySpeed;

    public Host(String name, double capacitySpeed){
        this.name = name;
        this.capacitySpeed = capacitySpeed;
        historySpeed = new HashMap<>();
        engineList = new ArrayList<>();
    }
    public Host(String name, double sloSpeed, List<EngineRole> engines){
        this.name = name;
        this.capacitySpeed = sloSpeed;
        this.historySpeed = new HashMap<>();
        this.engineList = engines;
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
}
