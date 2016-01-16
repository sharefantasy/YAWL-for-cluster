package cluster.entity;

import java.util.Date;

/**
 * Created by fantasy on 2015/9/2.
 */
public class SpeedRrd{
    public Date time;
    public double speed;
    public SpeedRrd(Date t, double s){time =t;speed=s;}
}
