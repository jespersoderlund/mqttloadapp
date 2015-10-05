package com.jsoft.iot.mqttloadapp.rest;

import com.jsoft.iot.mqttloadapp.model.Template;
import com.jsoft.iot.mqttloadapp.model.TemplateRepository;
import java.io.StringReader;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
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
@Path("/template")
public class TemplatesResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of TemplatesResource
     */
    public TemplatesResource() {
    }

    /**
     * Retrieves representation of an instance of
     * com.amazonaws.soderlun.iot.mqttloadapp.rest.TemplatesResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getAllTemplateNames() {
        try {
            List<Template> templates = TemplateRepository.getInstance().getTemplates();
            JsonArrayBuilder arrBuild = Json.createArrayBuilder();
            for (Template t : templates) {
                arrBuild.add(t.getName());
            }
            return arrBuild.build().toString();
        } catch (Exception ex) {
            throw new javax.ws.rs.InternalServerErrorException();
        }
    }

    /**
     * POST method for creating an instance of TemplateResource
     *
     * @param content representation for the new resource
     * @return an HTTP response with content of the created resource
     */
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response createTemplate(String content) {
        Template t = Template.newInstance(
                Json.createReader(new StringReader(content)).readObject());
        TemplateRepository.getInstance().putTemplate(t);
        return Response.created(context.getAbsolutePathBuilder().path(t.getName()).build()).build();
    }

    /**
     * Sub-resource locator method for {id}
     */
    @Path("{id}")
    public TemplateResource getTemplateResource(@PathParam("id") String id
    ) {
        return TemplateResource.getInstance(id);
    }
}
