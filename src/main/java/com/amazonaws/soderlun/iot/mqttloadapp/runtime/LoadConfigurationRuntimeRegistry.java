package com.amazonaws.soderlun.iot.mqttloadapp.runtime;

import com.amazonaws.soderlun.iot.mqttloadapp.model.LoadConfigsRegistry;
import com.amazonaws.soderlun.iot.mqttloadapp.model.LoadConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonObject;

/**
 *
 * @author soderlun
 */
public class LoadConfigurationRuntimeRegistry {

    private static final Logger LOG = Logger.getLogger(LoadConfigurationRuntimeRegistry.class.getName());

    private static final LoadConfigurationRuntimeRegistry instance = new LoadConfigurationRuntimeRegistry();

    public static LoadConfigurationRuntimeRegistry getInstance() {
        return instance;
    }

    private Map<String, RunningLoadConfiguration> registry = new HashMap<>();

    private LoadConfigurationRuntimeRegistry() {
    }

    public void stop(String cfgId) {
        LOG.log(Level.INFO, "Stopping Running Load configuration {0}", cfgId);

        RunningLoadConfiguration rt = registry.get(cfgId);
        if (rt != null) {
            rt.stop(cfgId);

            registry.remove(cfgId);

        } else {
            throw new RuntimeException("Could not find running load configuration " + cfgId);
        }
    }

    private RunningLoadConfiguration getRunning(String id) {
        return registry.get(id);
    }

    public void start(String cfgId, JsonObject obj) {
        LOG.log(Level.INFO, "Starting Load configuration runtime {0}", cfgId);
        LoadConfig cfg = LoadConfigsRegistry.getConfig(cfgId);
        if (cfg != null) {
            RunningLoadConfiguration rms = new RunningLoadConfiguration(cfg);
            if (rms.start()) {
                registry.put(cfgId, rms);
            }
        } else {
            throw new RuntimeException("Could not start metrics series");
        }
    }

    public List<RunningLoadConfiguration> getAllRunning() {
        List<RunningLoadConfiguration> result = new ArrayList<>();
        result.addAll(registry.values());
        return result;
    }

    public RunningLoadConfiguration get(String id) {
        return registry.get(id);
    }

    public boolean isRunning(String id) {
        return registry.containsKey(id);
    }

}
