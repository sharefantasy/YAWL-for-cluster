package cluster.event.events;

import cluster.data.EngineInfo;

import java.util.EventObject;

/**
 * Created by fantasy on 2015/8/22.
 */
public class EngineDisconnectEvent extends EventObject {
    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    private EngineInfo engine;
    public EngineDisconnectEvent(Object source, EngineInfo engine) {
        super(source);
        this.engine = engine;
    }

    public EngineInfo getEngine() {
        return engine;
    }
}
