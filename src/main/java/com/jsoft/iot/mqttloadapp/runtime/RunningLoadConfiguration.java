package com.jsoft.iot.mqttloadapp.runtime;

import com.jsoft.iot.mqttloadapp.model.FunctionConfiguration;
import com.jsoft.iot.mqttloadapp.SystemConfig;
import com.jsoft.iot.mqttloadapp.model.LoadConfig;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;

/**
 *
 * @author soderlun
 */
public class RunningLoadConfiguration {

    private static final Logger LOG = Logger.getLogger(RunningLoadConfiguration.class.getName());

    FunctionConfiguration ms = null;
    LoadConfig config;
    LoadGeneratorThread thread;
    long start = 0;

    public RunningLoadConfiguration(LoadConfig cfg) {
        if (cfg == null) {
            throw new NullPointerException("Config should not be null");
        }
        config = cfg;
        thread = new LoadGeneratorThread(config, config.getMetricsSeries());
    }

    public boolean start() {
        LOG.log(Level.INFO, "Starting {0}", config.getId());
        MqttConnection con = MqttConnection.getInstance();
        if (con.connect(SystemConfig.getMqttConfigProperties())) {
            LOG.info("Connected successfully");
            thread.start();
            start = System.currentTimeMillis();
            return true;
        } else {
            LOG.warning("Could not connect to MQTT-broker");
            // TBD - Disabled while offline
            // throw new InternalServerErrorException("Could not connect to MQTT-broker");
            return false;
        }
    }

    public void stop(String cfgId) {
        LOG.log(Level.INFO, "Stopping {0}", config.getId());
        if (thread != null) {
            thread.shutdown();
            try {
                thread.join(5000);
                LOG.info("Running thread was terminated");
            } catch (InterruptedException ex) {
                LOG.log(Level.SEVERE, "Could not terminate thread", ex);
            }
            thread = null;
        }
    }

    public String getId() {
        return config.getId();
    }

    public long getStart() {
        return start;
    }

    public JsonObject toJsonObject() {
        return Json.createObjectBuilder()
                .add("id", getId())
                .add("start", getStart()).build();
    }
}
