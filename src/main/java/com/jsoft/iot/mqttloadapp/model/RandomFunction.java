package com.jsoft.iot.mqttloadapp.model;

import java.util.Properties;
import java.util.Random;

/**
 *
 * @author soderlun
 */
public class RandomFunction extends Function {

    private enum RandomType {

        INT, FLOAT
    };
    RandomType valueType;
    int maxvalue;
    private final int minvalue;
    Random rnd = new Random();

    RandomFunction(FunctionType type, String variable, Properties props) {
        super(type, variable, props);

        valueType = RandomType.valueOf(props.getProperty("type", "INT"));

        maxvalue = Integer.parseInt(props.getProperty("maxvalue", "100"));
        minvalue = Integer.parseInt(props.getProperty("minvalue", "0"));
    }

    @Override
    public void value(StringBuilder buff, long millisElapsed, int ticks) {
        switch (valueType) {
            case INT:
                buff.append(rnd.nextInt(maxvalue - minvalue) + minvalue);
                break;
            case FLOAT:
                buff.append((double) (rnd.nextDouble() * (maxvalue - minvalue)) + minvalue);
            default:
                throw new RuntimeException("Unknown type");
        }
    }
}
