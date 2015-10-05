package com.jsoft.iot.mqttloadapp.rest;

import com.jsoft.iot.mqttloadapp.model.Template;
import com.jsoft.iot.mqttloadapp.model.TemplateRepository;
import java.io.FileNotFoundException;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author soderlun
 */
public class TemplateResource {

    private String id;

    /**
     * Creates a new instance of TemplateResource
     */
    private TemplateResource(String id) {
        this.id = id;
    }

    /**
     * Get instance of the TemplateResource
     */
    public static TemplateResource getInstance(String id) {
        // The user may use some kind of persistence mechanism
        // to store and restore instances of TemplateResource class.
        return new TemplateResource(id);
    }

    /**
     * Retrieves representation of an instance of
     * com.amazonaws.soderlun.iot.mqttloadapp.rest.TemplateResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {
        Template t = TemplateRepository.getInstance().getTemplate(id);

        return t.toJson();
    }


    /**
     * DELETE method for resource TemplateResource
     */
    @DELETE
    public Response delete() {
        try {
            TemplateRepository.getInstance().delete(id);
        } catch (FileNotFoundException ex) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception ex) {
            return Response.serverError().build();
        }
        return Response.ok().build();
    }
}
