package cluster.entity;

import java.util.*;

/**
 * Created by fantasy on 2016/1/6.
 */
public class Tenant {


    private int id;
    private String name;
    public Tenant(){}
    public List<EngineRole> getEngineList() {
        return engineList;
    }

    public void setEngineList(List<EngineRole> engineList) {
        this.engineList = engineList;
    }

    private List<EngineRole> engineList = new ArrayList<>();

    public double getSLOspeed() {
        return SLOspeed;
    }
    public void setSLOspeed(double SLOspeed) {
        this.SLOspeed = SLOspeed;
    }
    private double SLOspeed;
    private double currentSpeed;
    private Date recordTime;
    private Date createTime = new Date();
    private HashMap<Date, Double> historySpeed;

    public Tenant(String name, double sloSpeed){
        this.name = name;
        this.SLOspeed = sloSpeed;
        historySpeed = new HashMap<>();
        engineList = new ArrayList<>();
    }
    public Tenant(String name, double sloSpeed, List<EngineRole> engines){
        this.name = name;
        this.SLOspeed = sloSpeed;
        this.historySpeed = new HashMap<>();
        this.engineList = engines;
    }
    public long violatedTime(){
        final long PERIOD = 5000;
        final long[] violated = {0};
        historySpeed.forEach((Date d, Double s)->{
            if (s < SLOspeed) violated[0] +=PERIOD;     //magic method .5000 is the period of report.
        });
        return violated[0];
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

    public double getCurrentSpeed() {
        return currentSpeed;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public HashMap<Date, Double> getHistorySpeed() {
        return historySpeed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
