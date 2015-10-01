package com.amazonaws.soderlun.iot.mqttloadapp.model;

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
    
    
    public ExpressionFunction(FunctionType t, String v, Properties props) {
        super(t, v, props);
        
        expressionTemplate = new ExpressionBuilder(props.getProperty("expression", "tick") )
                .variables("tick", "elapsed");
        scaleFactorElapsed = Integer.parseInt(props.getProperty("elapsedscalefactor", "1"));
        scaleFactorTick = Integer.parseInt(props.getProperty("tickscalefactor", "1"));        
    }

    
    @Override
    public void value(StringBuilder buff, long millisElapsed, int ticks) {
        Expression expr = expressionTemplate.build();
        
        double scaledTick = ticks / scaleFactorTick;
        expr.setVariable("tick", scaledTick);
        
        double scaledElapsed = millisElapsed / scaleFactorElapsed;
        
        expr.setVariable("elapsed", scaledElapsed);
        
        buff.append(expr.evaluate());
    }
    
}
