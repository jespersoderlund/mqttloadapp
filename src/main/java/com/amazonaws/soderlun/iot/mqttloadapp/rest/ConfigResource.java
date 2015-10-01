package com.amazonaws.soderlun.iot.mqttloadapp.rest;

import com.amazonaws.soderlun.iot.mqttloadapp.model.MetricsConfigsRegistry;
import com.amazonaws.soderlun.iot.mqttloadapp.model.MetricsSeries;
import com.amazonaws.soderlun.iot.mqttloadapp.runtime.MetricsSeriesRuntimeRegistry;
import com.amazonaws.soderlun.iot.mqttloadapp.model.MetricsConfig;
import com.amazonaws.soderlun.iot.mqttloadapp.model.MetricsConfigException;
import java.io.StringReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.DELETE;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

/**
 * REST Web Service
 *
 * @author soderlun
 */
public class ConfigResource {
    private static final Logger LOG = Logger.getLogger(ConfigResource.class.getName());

    static ConfigResource getInstance(String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private UriInfo context;

    private String id;

    /**
     * Creates a new instance of ConfigResource
     */
    private ConfigResource(String id, UriInfo ctx) {
        this.id = id;
        context = ctx;
    }

    /**
     * Get instance of the ConfigResource
     */
    public static ConfigResource getInstance(String id, UriInfo context) {
        // The user may use some kind of persistence mechanism
        // to store and restore instances of ConfigResource class.
        return new ConfigResource(id, context);
    }

    /**
     * Retrieves representation of an instance of
     * com.amazonaws.soderlun.iot.mqttloadapp.ConfigResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {

        MetricsConfig cfg = MetricsConfigsRegistry.getConfig(id);

        if (cfg == null) {
            throw new NotFoundException();
        }

        return cfg.toJson();
    }

    /**
     * PUT method for updating or creating an instance of ConfigResource
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String c) {
        JsonObject content = Json.createReader(new StringReader(c))
                .readObject();

        MetricsConfig cfg = MetricsConfig.newInstance(content);
        cfg.setId(id);
        MetricsConfigsRegistry.updateConfig(id, cfg);               
//        Response resp = Response.created(context.getAbsolutePathBuilder().path(cfg.getId()).build()) .build();              
//        return resp;        
    }

    @POST
    @Consumes("application/json")
    public Response startRun(String content, @Context UriInfo uriInfo) {
        JsonReader reader = Json.createReader(new StringReader(content));
        JsonObject obj = reader.readObject();

        MetricsConfig cfg = MetricsConfigsRegistry.getConfig(id);

        List<MetricsSeries> ms = cfg.getMetricsSeries();
        if (ms.size() > 0) {

            try {
                MetricsSeriesRuntimeRegistry rt = MetricsSeriesRuntimeRegistry.getInstance();
                rt.start(cfg.getId(), obj);
                UriBuilder builder = UriBuilder.fromResource(MetricsSeriesCollectionResource.class);

                UriBuilder baseBuild = uriInfo.getBaseUriBuilder();

                // return Response.created( context.getAbsolutePathBuilder().path(cfg.getId()).build()).build();
                return Response.created(baseBuild.path(builder.build().toString()).path(cfg.getId()).build()).build();
            } catch (Exception ex) {
                LOG.log(Level.SEVERE,"could not start", ex);
                return Response.serverError().build();
            }

        } else {
            LOG.severe("No metrics series defined for config " + cfg.getId());
            return Response.serverError().build();
        }
    }

    /**
     * DELETE method for resource ConfigResource
     */
    @DELETE
    public void delete() {
        try {
            MetricsConfigsRegistry.deleteConfig(id);
        } catch (MetricsConfigException ex) {
            Logger.getLogger(ConfigResource.class.getName()).log(Level.SEVERE, "Could not find config id" + id, ex);
            throw  new javax.ws.rs.NotFoundException();
        }
    }
}
