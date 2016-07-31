package cluster.util.event;

import cluster.util.exceptions.GeneralException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fantasy on 2016/2/20.
 */
@Component
public class EventCenter {
	private Map<String, List<Handler>> handlers = new ConcurrentHashMap<>();
	private final static Logger _logger = Logger.getLogger(EventCenter.class);
	public EventCenter On(String event, Handler handle) {
		if (!handlers.containsKey(event)) {
			List<Handler> newE = new ArrayList<>();
			newE.add(handle);
			handlers.put(event, newE);
			_logger.info("event: " + event + "added");
		} else {
			handlers.get(event).add(handle);
		}
		return this;
	}

	public EventCenter trigger(String event, Object cause) {
		if (handlers.containsKey(event)) {
			handlers.get(event).stream().forEach(h -> h.Run(cause));
			_logger.info("event: " + event + "triggered");
		}
		return this;
	}
}
