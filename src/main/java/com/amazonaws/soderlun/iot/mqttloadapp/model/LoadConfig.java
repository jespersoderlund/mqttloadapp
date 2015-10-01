package com.amazonaws.soderlun.iot.mqttloadapp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

/**
 *
 * @author soderlun
 */
public class MetricsConfig {

    public static MetricsConfig newInstance(JsonObject obj) {
        return parseJson(obj);
    }

    String id;
    List<MetricsSeries> metrics = new ArrayList<>();
    String templateId;
    String topic;
    private int rate;

    public MetricsConfig(String id) {
        this.id = id;
    }

    private MetricsConfig(String tid, String t, List<MetricsSeries> series) {
        templateId = tid;
        id = UUID.randomUUID().toString();
        metrics = Collections.unmodifiableList(series);
        topic = t;
    }

    public String getId() {
        return id;
    }

    public List<MetricsSeries> getMetricsSeries() {
        return metrics;
    }

    public String getTemplateId() {
        return templateId;
    }

    public String getTopic() {
        return topic;
    }

    public String toJson() {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        builder.add("id", id)
                .add("templateid", getTemplateId())
                .add("topic", getTopic())
                .add("rate", getRate());

        JsonArrayBuilder metricsBuilder = Json.createArrayBuilder();

        for (MetricsSeries ms : metrics) {
            ms.toJson(metricsBuilder);
        }

        builder.add("metricsseries", metricsBuilder);
        return builder.build().toString();
    }

    private static MetricsConfig parseJson(JsonObject obj) {

        // Parse MetricsConfig attributes
        String topic = obj.getString("topic");
        String templateId = obj.getString("templateid");
        int rate = obj.getInt("rate");
        String id = obj.getString("id", null);

        // Parse Metrics Series
        JsonArray metrics = obj.getJsonArray("metricsseries");
        List<MetricsSeries> series = new ArrayList<>();
        if (metrics != null) {
            for (JsonValue metric : metrics) {
                series.add(MetricsSeries.parse((JsonObject) metric));
            }
        }

        MetricsConfig cfg = new MetricsConfig(templateId, topic, series);
        cfg.setRate(rate);
        if (id != null) {
            cfg.setId(id);
        }

        return cfg;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * The number of samples to do per minute
     *
     * @return
     */
    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
