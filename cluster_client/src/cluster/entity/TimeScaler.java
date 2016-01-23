package cluster.entity;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.concurrent.*;

/**
 * Created by fantasy on 2016/1/20.
 */
public class TimeScaler {
    private static final Logger _logger = Logger.getLogger(TimeScaler.class);
    private static final ScheduledExecutorService _executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    private static TimeScaler instance;
    private TimeScaler(){
        _logger.info("time scaler started at " + new Date().toString());
    }
    public static TimeScaler getInstance(){
        if (instance == null) {
            instance = new TimeScaler();
        }
        return instance;
    }
    public ScheduledExecutorService getExecutor(){
        return _executor;
    }
    public void destroy(){
        if (_executor.isTerminated()) return;
        try {
            _logger.info("attempt to shutdown time scaler");
            _executor.shutdown();
            _executor.awaitTermination(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            _logger.error("tasks interrupted");
        }
        finally {
            if (!_executor.isTerminated()) {
                _logger.error("cancel non-finished tasks");
            }
            _executor.shutdownNow();
            _logger.info("shutdown finished at + " + new Date().toString());
        }
    }
}
