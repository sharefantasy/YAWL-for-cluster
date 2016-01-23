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
            }
        }, 0, period);
    }
    public void shutdown(){
        timer.purge();
        timer.cancel();
        timer = null;
    }
    public static WorkitemCounter getInstace(){
        if (instance == null){
            instance =  new WorkitemCounter(1000);
        }
        return instance;
    }
    public synchronized void  increase(){
        if (timer != null){
            currentCounter += 1;
        }

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
