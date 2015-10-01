package com.amazonaws.soderlun.iot.mqttloadapp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;

/**
 *
 * @author soderlun
 */
public class TextFunction extends Function {

    private List<String> texts = new ArrayList<>();
    private int[] weights;
    private int totalWeights = 0;
    private Random rnd = new Random();

    public TextFunction(FunctionType t, String v, Properties props) {
        super(t, v, props);
        String rawText = props.getProperty("texts", null);
        if (rawText != null) {
            StringTokenizer tokenizer = new StringTokenizer(rawText, "/");
            while (tokenizer.hasMoreElements()) {
                texts.add(tokenizer.nextToken());
            }
        }

        String rawWeights = props.getProperty("weights", null);
        if (rawWeights != null) {
            StringTokenizer tokenizer = new StringTokenizer(rawWeights, "/");
            List<Integer> ints = new ArrayList<>();
            while (tokenizer.hasMoreElements()) {
                String dv = tokenizer.nextToken();
                int val = Integer.parseInt(dv);
                totalWeights += val;
                ints.add(val);
            }
            // Only use the first weights upto the number of texts, if more
            weights = new int[Math.min(ints.size(), texts.size())];
            for (int i = 0; i < weights.length; i++) {
                weights[i] = ints.get(i);
            }
        }
    }

    @Override
    public void value(StringBuilder buff, long millisElapsed, int ticks) {

        if (weights.length > 0) {
            // We want random weights
            int currSelection = rnd.nextInt(totalWeights);
            int accumulatedWeights = 0;
            for (int i = 0; i < texts.size(); i++) {
                accumulatedWeights += weights[i];
                if (accumulatedWeights >= currSelection) {
                    buff.append(texts.get(i));
                    break;
                }
            }
        } else {
            // Just round-robin the texts
            buff.append(texts.get(ticks % texts.size()));
        }
    }
}
