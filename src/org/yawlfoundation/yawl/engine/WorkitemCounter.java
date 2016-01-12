package org.yawlfoundation.yawl.engine;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fantasy on 2016/1/3.
 */
public class WorkitemCounter {
    private Integer currentCounter = 0;
    private double reportCounter = 0;
    private Date reportDate;
    private int period;
    private static WorkitemCounter instance;
    static Timer timer = new Timer();
    private WorkitemCounter(int period){
        this.period = period;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (currentCounter){
                    reportCounter = currentCounter / ( period / 1000); // task/min
                    currentCounter = 0;
                    reportDate = new Date();
                }
                System.out.println("count: " + reportCounter);
            }
        }, 0, period);
    }
    public static WorkitemCounter getInstace(){
        if (instance == null){
            instance =  new WorkitemCounter(1000);
        }
        return instance;
    }
    public void increase(){
        currentCounter += 1;
    }

    public int getPeriod() {
        return period;
    }

    public double getReportCounter() {
        return reportCounter;
    }

    public Date getReportDate() {
        return reportDate;
    }
}
