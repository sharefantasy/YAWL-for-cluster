package cluster.util.event;

/**
 * Created by fantasy on 2016/2/20.
 */
@FunctionalInterface
public interface Handler {
	void Run(Object e);
}
