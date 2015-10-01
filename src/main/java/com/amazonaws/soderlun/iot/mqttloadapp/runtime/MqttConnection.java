package com.amazonaws.soderlun.iot.mqttloadapp.runtime;

import com.amazonaws.soderlun.iot.mqttloadapp.SslUtil;
import java.io.File;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

/**
 *
 * @author soderlun
 */
public class MqttConnection implements MqttCallback {
    private static final Logger LOG = Logger.getLogger(MqttConnection.class.getName());

    private static final MqttConnection instance = new MqttConnection();
    private static int TEMP_DIR_ATTEMPTS = 5;

    public static final MqttConnection getInstance() {
        return instance;
    }

    private MqttClient client;
    private MqttConnectOptions conOpt;
    private String brokerUrl;
    private String clientId;
    private boolean connected;

    public boolean connect(Properties props) {

        brokerUrl = props.getProperty("brokerurl", null);
        if (brokerUrl == null) {
            throw new RuntimeException("Could not find parameter brokerur");
        }

        Random rnd = new Random();
        clientId = props.getProperty("clientid", System.getenv("mqtt.clientid") != null ? System.getenv("mqtt.clientid") : "mqtt.loadgen." + rnd.nextInt(100000));

        //This sample stores in a temporary directory... where messages temporarily
        // stored until the message has been delivered to the server.
        //..a real application ought to store them somewhere
        // where they are not likely to get deleted or tampered with
        File tmpDir = createTempDir();
        MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir.getAbsolutePath());
        LOG.log(Level.INFO, "Using directory {0} for temporary message storage", tmpDir.getAbsolutePath());

        try {
            // Construct the connection options object that contains connection parameters
            // such as cleanSession and LWT
            conOpt = new MqttConnectOptions();
            conOpt.setCleanSession(Boolean.parseBoolean(props.getProperty("cleansession", "true")));
            conOpt.setConnectionTimeout(Integer.parseInt(props.getProperty("connectiontimeout", "100")));
            conOpt.setKeepAliveInterval(Integer.parseInt(props.getProperty("keepaliveinterval", "100")));
            conOpt.setSocketFactory(SslUtil.getSocketFactory(props.getProperty("cafile"), props.getProperty("cert"), props.getProperty("privkey"), props.getProperty("password", "dummy")));

            // Construct an MQTT blocking mode client
            client = new MqttClient(this.brokerUrl, clientId, dataStore);

            // Set this wrapper as the callback handler
            client.setCallback(this);

            // Connect to the MQTT server
            LOG.log(Level.INFO, "Connecting to {0} with client ID {1}", new Object[]{brokerUrl, client.getClientId()});
            client.connect(conOpt);
            LOG.info("Connected");

            connected = true;

            return connected;

        } catch (MqttException e) {
            LOG.log(Level.WARNING, "Unable to set up client: " + e.toString(), e);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Could not create connection: " + ex.getLocalizedMessage(), ex);
        }
        return false;
    }

    @Override
    public void connectionLost(Throwable cause) {
        LOG.log(Level.SEVERE, "Connection lost: " + cause.getLocalizedMessage(), cause);
        connected = false;

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        LOG.log(Level.INFO, "Message arrived for topic: {0}, QoS: {1}, message: {2}", new Object[]{topic, message.getQos(), new String(message.getPayload())});
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        LOG.log(Level.INFO, "Delivery complete of message id:{0}", token.getMessageId());
    }

    /**
     * Publish / send a message to an MQTT server
     *
     * @param topicName the name of the topic to publish to
     * @param qos the quality of service to delivery the message at (0,1,2)
     * @param payload the set of bytes to send to the MQTT server
     * @throws MqttException
     */
    public void publish(String topicName, int qos, byte[] payload) throws MqttException {

        String time = new Timestamp(System.currentTimeMillis()).toString();
        LOG.log(Level.INFO, "Publishing at: {0} to topic \"{1}\" qos {2}", new Object[]{time, topicName, qos});

        // Create and configure a message
        MqttMessage message = new MqttMessage(payload);
        message.setQos(qos);

        // Send the message to the server, control is not returned until
        // it has been delivered to the server meeting the specified
        // quality of service.
        client.publish(topicName, message);
    }

    public void disconnect() {
        try {
            // Disconnect the client
            client.disconnect();
            LOG.info("Disconnected");
        } catch (MqttException ex) {
            LOG.log(Level.SEVERE, "Error while disconnected", ex);
        } finally {
            connected = false;
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public static File createTempDir() {
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        String baseName = System.currentTimeMillis() + "-";

        for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
            File tempDir = new File(baseDir, baseName + counter);
            if (tempDir.mkdir()) {
                return tempDir;
            }
        }
        throw new IllegalStateException("Failed to create directory within "
                + TEMP_DIR_ATTEMPTS + " attempts (tried "
                + baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
    }
}
