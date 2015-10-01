package com.amazonaws.soderlun.iot.mqttloadapp.runtime;

import com.amazonaws.soderlun.iot.mqttloadapp.model.MetricsConfigsRegistry;
import com.amazonaws.soderlun.iot.mqttloadapp.model.MetricsConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.json.JsonObject;

/**
 *
 * @author soderlun
 */
public class MetricsSeriesRuntimeRegistry {

    private static final Logger LOG = Logger.getLogger(MetricsSeriesRuntimeRegistry.class.getName());

    private static final MetricsSeriesRuntimeRegistry instance = new MetricsSeriesRuntimeRegistry();

    public static MetricsSeriesRuntimeRegistry getInstance() {
        return instance;
    }

    private Map<String, RunningMetricsSeries> registry = new HashMap<>();

    private MetricsSeriesRuntimeRegistry() {
    }

    public void stop(String cfgId) {
        LOG.info("Stopping Metrics instance runtime " + cfgId);

        RunningMetricsSeries rt = registry.get(cfgId);
        if (rt != null) {
            rt.stop(cfgId);

            registry.remove(cfgId);

        }
        else {
            throw new RuntimeException("Could not find metrics series " + cfgId);
        }
    }

    private RunningMetricsSeries getRunning(String id) {
        return registry.get(id);
    }

    public void start(String cfgId, JsonObject obj) {
        LOG.info("Starting Metrics instance runtime " + cfgId);
        MetricsConfig cfg = MetricsConfigsRegistry.getConfig(cfgId);
        if (cfg != null) {
            RunningMetricsSeries rms = new RunningMetricsSeries(cfg);
            registry.put(cfgId, rms);
            rms.start();
        }
    }

    public List<RunningMetricsSeries> getAllRunning() {
        List<RunningMetricsSeries> result = new ArrayList<>();
        result.addAll(registry.values());
        return result;
    }

    public RunningMetricsSeries get(String id) {
        return registry.get(id);
    }

    public boolean isRunning(String id) {
        return registry.containsKey(id);
    }

    
}
