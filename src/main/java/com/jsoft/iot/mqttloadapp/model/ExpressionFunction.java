package com.jsoft.iot.mqttloadapp.model;

import java.util.Properties;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

/**
 *
 * @author soderlun
 */
public class ExpressionFunction extends Function {

    ExpressionBuilder expressionTemplate;
    private final int scaleFactorElapsed;
    private final int scaleFactorTick;
    private final int tickOffset;
    private final int elapsedOffset;

    public ExpressionFunction(FunctionType t, String v, Properties props) {
        super(t, v, props);

        expressionTemplate = new ExpressionBuilder(props.getProperty("expression", "tick"))
                .variables("tick", "elapsed");
        scaleFactorElapsed = Integer.parseInt(props.getProperty("elapsedscalefactor", "1"));
        scaleFactorTick = Integer.parseInt(props.getProperty("tickscalefactor", "1"));
        tickOffset = Integer.parseInt(props.getProperty("tickoffset", "0"));
        elapsedOffset = Integer.parseInt(props.getProperty("elapsedoffset", "0"));
    }

    @Override
    public void value(StringBuilder buff, long millisElapsed, int ticks) {
        Expression expr = expressionTemplate.build();

        int adjustedTicks = ticks + tickOffset;
        double scaledTick = adjustedTicks / (double) scaleFactorTick;
        expr.setVariable("tick", scaledTick);

        long adjustedElapsed = millisElapsed + elapsedOffset;
        double scaledElapsed = adjustedElapsed / (double) scaleFactorElapsed;

        expr.setVariable("elapsed", scaledElapsed);

        buff.append(expr.evaluate());
    }

}
