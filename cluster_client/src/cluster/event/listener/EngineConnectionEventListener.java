package cluster.event.listener;

import cluster.data.EngineInfo;
import cluster.Manager;
import cluster.event.events.EngineConnectedEvent;
import cluster.event.events.EngineDisconnectEvent;
import cluster.event.exceptions.GeneralException;
import org.apache.log4j.Logger;

/**
 * Created by fantasy on 2015/8/22.
 */
public class EngineConnectionEventListener implements CListener {

    private Manager manager = Manager.getInstance();
    private final static Logger _logger = Logger.getLogger(EngineConnectedEvent.class);
    public void fireEngineConnected(EngineConnectedEvent e){

    }

    @Override
    public void handleEvent(Object e) {
        try {
            if (e instanceof EngineConnectedEvent){
                manager.login(((EngineConnectedEvent) e).getEngine());
            }
            else if (e instanceof EngineDisconnectEvent){
                manager.logout((EngineInfo) e);
            }

        } catch (GeneralException e1) {
            e1.printStackTrace();
        }
        _logger.info(((EngineInfo) e).getEngineID() + " login");
    }
}
