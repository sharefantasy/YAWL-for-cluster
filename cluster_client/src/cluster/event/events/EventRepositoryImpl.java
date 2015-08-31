package cluster.event;

import cluster.EventRepository;
import cluster.Manager;
import cluster.data.EngineInfo;
import cluster.event.events.EngineConnectedEvent;
import cluster.event.events.EngineDisconnectEvent;
import cluster.event.events.EngineRegistrationEvent;
import cluster.event.events.EngineUnregistrationEvent;
import cluster.event.listener.CListener;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fantasy on 2015/8/22.
 */
public class EventRepositoryImpl implements EventRepository {
    private Manager manager = Manager.getInstance();
    private Map<EventObject, Collection<CListener>> repo;
    private static EventRepositoryImpl eventRepoMgt;
    private static final Logger _logger = Logger.getLogger(EventRepositoryImpl.class);
    private EventRepositoryImpl(){
        repo = new ConcurrentHashMap<>();
    }
    public static EventRepositoryImpl getInstance(){
        if (eventRepoMgt == null){
            eventRepoMgt = new EventRepositoryImpl();
        }
        return eventRepoMgt;
    }

    public boolean addEvent(EventObject e){
        if (!repo.containsKey(e)){
            repo.put(e,new ArrayList<CListener>());
            return true;
        }
        return false;
    }

    public boolean removeEvent(EventObject e){
        if (repo.containsKey(e)){
            repo.remove(e, new ArrayList<CListener>());
            return true;
        }
        return false;
    }

    public boolean addEventListener(EventObject e, CListener listener){
        if (repo.containsKey(e)){
            repo.get(e).add(listener);
            return true;
        }
        return false;
    }

    public boolean removeEventListener(EventObject e, CListener listener){
        if (repo.containsKey(e)){
            repo.get(e).remove(listener);
            return true;
        }
        return false;
    }

    @Override
    public void fireConnect(EngineInfo engine) {
        notifyListeners(new EngineConnectedEvent(this, engine));
    }

    @Override
    public void fireDisconnect(EngineInfo engine) {
        notifyListeners(new EngineDisconnectEvent(this, engine));
    }

    @Override
    public void fireHeartbeatAbnormal(EngineInfo engine) {

    }

    @Override
    public void fireRegister(EngineInfo engine) {
        notifyListeners(new EngineRegistrationEvent(this, engine));
    }

    @Override
    public void fireUnregister(EngineInfo engine) {
        notifyListeners(new EngineUnregistrationEvent(this, engine));
    }

    @Override
    public void fireCheckHeartBeatStatus(EngineInfo engine) {

    }

    private void notifyListeners(EventObject e){
        if (repo.containsKey(e)) {
            for (CListener l : repo.get(e)) {
                l.handleEvent(e);
            }
        } else{
            _logger.warn("Unregistered event " + e.getClass().getName());
        }
    }
    public static void main(String[] args){
        EventRepositoryImpl eventRepository = new EventRepositoryImpl();
    }
}
