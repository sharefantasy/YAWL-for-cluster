package cluster.event.listener;

import java.util.EventListener;

/**
 * Created by fantasy on 2015/8/22.
 */
public interface CListener extends EventListener {
    void handleEvent(Object e);
}
