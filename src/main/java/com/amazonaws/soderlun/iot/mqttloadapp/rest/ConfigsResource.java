package com.amazonaws.soderlun.iot.mqttloadapp.rest;

import com.amazonaws.soderlun.iot.mqttloadapp.model.LoadConfigsRegistry;
import com.amazonaws.soderlun.iot.mqttloadapp.rest.ConfigResource;
import com.amazonaws.soderlun.iot.mqttloadapp.model.LoadConfig;
import com.amazonaws.soderlun.iot.mqttloadapp.runtime.LoadConfigurationRuntimeRegistry;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author soderlun
 */
@Path("/config")
public class ConfigsResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of ConfigsResource
     */
    public ConfigsResource() {
    }

    /**
     * Retrieves representation of an instance of
     * com.amazonaws.soderlun.iot.mqttloadapp.ConfigsResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {
        List<LoadConfig> configs = LoadConfigsRegistry.getAllConfigs();

        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (LoadConfig c : configs) {
            builder.add(Json.createObjectBuilder()
                    .add("id", c.getId())
                    .add("running", LoadConfigurationRuntimeRegistry.getInstance().isRunning(c.getId()))
            );
        }
        StringWriter writer = new StringWriter();

        try (JsonWriter jw = Json.createWriter(writer)) {
            jw.writeArray(builder.build());
        }

        return writer.toString();
    }

    /**
     * POST method for creating an instance of ConfigResource
     *
     * @param content representation for the new resource
     * @return an HTTP response with content of the created resource
     */
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response postJson(String c) {

        JsonObject content = Json.createReader(new StringReader(c))
                .readObject();

        LoadConfig cfg = LoadConfig.newInstance(content);
        LoadConfigsRegistry.createConfig(cfg);
        Response resp = Response.created(context.getAbsolutePathBuilder().path(cfg.getId()).build())
                .build();

        return resp;
    }

    /**
     * Sub-resource locator method for {id}
     */
    @Path("{id}")
    public ConfigResource getConfigResource(@PathParam("id") String id) {
        return ConfigResource.getInstance(id, context);
    }
}
