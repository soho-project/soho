package work.soho.lot;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import work.soho.lot.config.MqttConfig;

@Log4j2
@Component
@RequiredArgsConstructor
public class MqttApplication implements ApplicationRunner {
    final private MqttConfig mqttConfig;

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        Logger logger = LoggerFactory.getLogger(MqttApplication.class);

        MQTT mqtt = new MQTT();
//        mqtt.setHost("192.168.0.7", 1883);
//        mqtt.setClientId("test");
//        mqtt.setUserName("test");
//        mqtt.setPassword("123456");
        mqtt.setHost(mqttConfig.getHost(), mqttConfig.getPort());
        mqtt.setClientId(mqttConfig.getClientId());
        mqtt.setUserName(mqttConfig.getUsername());
        mqtt.setPassword(mqttConfig.getPassword());

        final CallbackConnection connection =  mqtt.callbackConnection();
        connection.listener(new Listener() {
            @Override
            public void onConnected() {
                log.info("mqtt connected");
            }

            @Override
            public void onDisconnected() {
                log.info("mqtt disconnected");
            }

            @Override
            public void onPublish(UTF8Buffer topic, Buffer body, Runnable ack) {
                System.out.println("topic:" + topic.toString() + "  body："+new String(body.toByteArray()));
                ack.run();
            }

            @Override
            public void onFailure(Throwable value) {
                log.info("mqtt failure");
            }
        });
        connection.connect(new Callback<Void>() {
            @Override
            public void onSuccess(Void value) {
                log.info("mqtt conneced");
            }

            @Override
            public void onFailure(Throwable value) {
                log.info("mqtt connect failure");
            }
        });

//        connection.publish("topic", "body".getBytes(), QoS.AT_LEAST_ONCE, false, new Callback<Void>() {
//            @Override
//            public void onSuccess(Void value) {
//            }
//
//            @Override
//            public void onFailure(Throwable value) {
//                logger.info("mqtt publish failure");
//            }
//        });

        Topic topic = new Topic("topic", QoS.AT_LEAST_ONCE);
        connection.subscribe(new Topic[]{topic}, new Callback<byte[]>() {
            @Override
            public void onSuccess(byte[] value) {
            }

            @Override
            public void onFailure(Throwable value) {
            }
        });

    }
}
