package com.jsoft.iot.mqttloadapp.model;

import java.util.Properties;

/**
 *
 * @author soderlun
 */
public class SineFunction extends Function {

    int cycleTimeSeconds = 60;
    int magnitude = 1;
    private final int scaleFactorElapsed;

    SineFunction(FunctionType type, String variable, Properties props) {
        super(type, variable, props);

        cycleTimeSeconds = Integer.parseInt(props.getProperty("cycleTime", "60"));
        magnitude = Integer.parseInt(props.getProperty("magnitude", "1"));
        scaleFactorElapsed = Integer.parseInt(props.getProperty("elapsedscalefactor", "1"));
    }

    @Override
    public void value(StringBuilder buff, long millisElapsed, int ticks) {
        int period = cycleTimeSeconds * 1000;

        long scaledElapsed = millisElapsed / scaleFactorElapsed;

        double value = Math.sin((double) (scaledElapsed % period) / (double) period);

        double scaledValue = value * magnitude;

        buff.append(scaledValue);
    }
}
