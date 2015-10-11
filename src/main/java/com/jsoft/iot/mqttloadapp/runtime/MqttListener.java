package com.jsoft.iot.mqttloadapp.runtime;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 *
 * @author soderlun
 */
interface MqttListener {
    public void notify(MqttMessage message);    
}
