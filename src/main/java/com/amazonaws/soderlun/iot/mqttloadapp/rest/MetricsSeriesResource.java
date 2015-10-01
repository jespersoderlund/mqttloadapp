package com.amazonaws.soderlun.iot.mqttloadapp.rest;

import com.amazonaws.soderlun.iot.mqttloadapp.model.MetricsConfig;
import com.amazonaws.soderlun.iot.mqttloadapp.model.MetricsConfigsRegistry;
import com.amazonaws.soderlun.iot.mqttloadapp.model.MetricsSeries;
import com.amazonaws.soderlun.iot.mqttloadapp.runtime.MetricsSeriesRuntimeRegistry;
import com.amazonaws.soderlun.iot.mqttloadapp.runtime.RunningMetricsSeries;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.DELETE;
import javax.ws.rs.NotFoundException;

/**
 * REST Web Service
 *
 * @author soderlun
 */
public class MetricsSeriesResource {

    private String id;
    private MetricsSeries metricsSeries;
    private boolean running = false;

    /**
     * Creates a new instance of MetricsSeriesResource
     */
    private MetricsSeriesResource(String id) {
        this.id = id;
        MetricsConfig cfg = MetricsConfigsRegistry.getConfig(id);
        if (cfg.getMetricsSeries().size() > 0) {
            metricsSeries = cfg.getMetricsSeries().get(0);
        } else {
            throw new RuntimeException("No metrics series defined");
        }
    }

    /**
     * Get instance of the MetricsSeriesResource
     *
     * @param id
     * @return
     */
    public static MetricsSeriesResource getInstance(String id) {
        // The user may use some kind of persistence mechanism
        // to store and restore instances of MetricsSeriesResource class.
        return new MetricsSeriesResource(id);
    }

    /**
     * Retrieves representation of an instance of
     * com.amazonaws.soderlun.iot.mqttloadapp.MetricsSeriesResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {
        RunningMetricsSeries rms = MetricsSeriesRuntimeRegistry.getInstance().get(id);

        if (rms != null) {
            return rms.toJsonObject().toString();
        }
        else {
            throw new NotFoundException();
        }
    }

    /**
     * PUT method for updating or creating an instance of MetricsSeriesResource
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }

    /**
     * DELETE method for resource MetricsSeriesResource
     */
    @DELETE
    public void delete() {
        stop();
    }

    private void stop() {
        MetricsSeriesRuntimeRegistry.getInstance().stop(id);
        running = false;
    }
}
