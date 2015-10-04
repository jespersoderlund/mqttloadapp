package com.amazonaws.soderlun.iot.mqttloadapp;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author soderlun
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.amazonaws.soderlun.iot.mqttloadapp.rest.ConfigsResource.class);
        resources.add(com.amazonaws.soderlun.iot.mqttloadapp.rest.MetricsSeriesCollectionResource.class);
        resources.add(com.amazonaws.soderlun.iot.mqttloadapp.rest.TemplateResource.class);
        resources.add(com.amazonaws.soderlun.iot.mqttloadapp.rest.TemplatesResource.class);
    }
    
}
