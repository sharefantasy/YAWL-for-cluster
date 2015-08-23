package cluster.event.listener;

import cluster.data.EngineInfo;
import cluster.Manager;
import cluster.event.events.EngineRegistrationEvent;
import cluster.event.events.EngineUnregistrationEvent;
import cluster.event.exceptions.GeneralException;
import org.apache.log4j.Logger;

/**
 * Created by fantasy on 2015/8/22.
 */
public class EngineRegistrationEventListener implements CListener {

    private Manager manager = Manager.getInstance();
    private final static Logger _logger = Logger.getLogger(EngineRegistrationEventListener.class);
    public void fireEngineConnected(EngineRegistrationEvent e){

    }

    @Override
    public void handleEvent(Object e) {
        try {
            if (e instanceof EngineRegistrationEvent){
                EngineInfo engine = ((EngineRegistrationEvent) e).getEngine();
                manager.register(engine);
                _logger.info(engine.getEngineID() + " login");
            }
            else if (e instanceof EngineUnregistrationEvent){
                EngineInfo engine = ((EngineUnregistrationEvent) e).getEngine();
                manager.logout(engine);
                _logger.info(engine.getEngineID() + " unlogged");
            }

        } catch (GeneralException e1) {
            e1.printStackTrace();
        }

    }
}
