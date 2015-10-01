package com.amazonaws.soderlun.iot.mqttloadapp.runtime;

import com.amazonaws.soderlun.iot.mqttloadapp.model.MetricsSeries;
import com.amazonaws.soderlun.iot.mqttloadapp.SystemConfig;
import com.amazonaws.soderlun.iot.mqttloadapp.model.MetricsConfig;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;

/**
 *
 * @author soderlun
 */
public class RunningMetricsSeries {

    private static final Logger LOG = Logger.getLogger(RunningMetricsSeries.class.getName());

    MetricsSeries ms = null;
    MetricsConfig config;
    MetricsThread thread;
    long start = 0;

    public RunningMetricsSeries(MetricsConfig cfg) {
        if (cfg == null) {
            throw new NullPointerException("Config should not be null");
        }
        config = cfg;
        thread = new MetricsThread(config, config.getMetricsSeries());
    }

    public void start() {
        LOG.log(Level.INFO, "Starting {0}", config.getId());
        MqttConnection con = MqttConnection.getInstance();
        if (con.connect(SystemConfig.getMqttConfigProperties())) {
            LOG.info("Connected successfully");
            thread.start();
        } else {
            LOG.warning("Could not connect to MQTT-broker");
            // TBD - Disabled while offline
            // throw new InternalServerErrorException("Could not connect to MQTT-broker");
        }

        start = System.currentTimeMillis();
    }

    public void stop(String cfgId) {
        LOG.log(Level.INFO, "Stopping {0}", config.getId());
        if (thread != null) {
            thread.shutdown();
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
