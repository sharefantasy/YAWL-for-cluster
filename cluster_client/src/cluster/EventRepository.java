package cluster;

import cluster.data.EngineInfo;
import cluster.event.listener.CListener;

import java.util.EventObject;

/**
 * Created by fantasy on 2015/8/22.
 */
public interface EventRepository {
    boolean addEvent(EventObject e);
    boolean removeEvent(EventObject e);
    boolean addEventListener(EventObject e, CListener listener);
    boolean removeEventListener(EventObject e, CListener listener);

    void fireConnect(EngineInfo engine);
    void fireDisconnect(EngineInfo engine);
    void fireHeartbeatAbnormal(EngineInfo engine);
    void fireRegister(EngineInfo engine);
    void fireUnregister(EngineInfo engine);
    void fireCheckHeartBeatStatus(EngineInfo engine);

}
