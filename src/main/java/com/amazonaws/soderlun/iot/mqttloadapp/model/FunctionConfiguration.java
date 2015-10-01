package com.amazonaws.soderlun.iot.mqttloadapp.model;

import com.amazonaws.soderlun.iot.mqttloadapp.model.Function;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 *
 * @author soderlun
 */
public class MetricsSeries {

    static MetricsSeries parse(JsonObject metric) {
        String func = metric.getString("function");
        String funcvar = metric.getString("variable");
        JsonArray params = metric.getJsonArray("parameters");

        FunctionType ft = FunctionType.valueOf(func);
        Function f = Function.newInstance(ft, funcvar, params);
        
        MetricsSeries ms = new MetricsSeries(f);
        return ms;       
    }

    private Function function;

    public MetricsSeries(Function f) {
        function = f;

    }

    public Function getFunction() {
        return function;
    }

    void toJson(JsonArrayBuilder metricsBuilder) {
        JsonArrayBuilder paramsBuilder = Json.createArrayBuilder();
        getFunction().fillParameters(paramsBuilder);
        metricsBuilder
                .add(Json.createObjectBuilder()
                        .add("function", getFunction().getFunctionType().name())
                        .add("variable", getFunction().getVariable())
                        .add("parameters", paramsBuilder));
    }

}
