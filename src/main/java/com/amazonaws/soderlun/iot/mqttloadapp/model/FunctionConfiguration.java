package com.amazonaws.soderlun.iot.mqttloadapp.model;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

/**
 *
 * @author soderlun
 */
public class FunctionConfiguration {

    static FunctionConfiguration parse(JsonObject metric) {
        String func = metric.getString("function");
        String funcvar = metric.getString("variable");
        JsonArray params = metric.getJsonArray("parameters");

        FunctionType ft = FunctionType.valueOf(func);
        Function f = Function.newInstance(ft, funcvar, params);
        
        FunctionConfiguration ms = new FunctionConfiguration(f);
        return ms;       
    }

    private Function function;

    public FunctionConfiguration(Function f) {
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
