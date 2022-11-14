package work.soho.lot;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.*;

import java.net.URISyntaxException;

public class Test {
    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        System.out.println("test by fang");
        MQTT mqtt = new MQTT();
        mqtt.setHost("192.168.0.7", 1883);
        mqtt.setClientId("test");
        mqtt.setUserName("test");
        mqtt.setPassword("123456");

        final CallbackConnection connection =  mqtt.callbackConnection();
        connection.listener(new Listener() {
            @Override
            public void onConnected() {
                System.out.println("connected");
            }

            @Override
            public void onDisconnected() {
                System.out.println("disconnected");
            }

            @Override
            public void onPublish(UTF8Buffer topic, Buffer body, Runnable ack) {
                System.out.println("topic:" + topic.toString() + "  body："+new String(body.toByteArray()));
                ack.run();
            }

            @Override
            public void onFailure(Throwable value) {
                System.out.println("failure");
            }
        });
        connection.connect(new Callback<Void>() {
            @Override
            public void onSuccess(Void value) {
                System.out.println("conneced");
            }

            @Override
            public void onFailure(Throwable value) {
                System.out.println("connect failure");
            }
        });
        connection.publish("topic", "body".getBytes(), QoS.AT_LEAST_ONCE, false, new Callback<Void>() {
            @Override
            public void onSuccess(Void value) {
                System.out.println(value);
            }

            @Override
            public void onFailure(Throwable value) {
                System.out.println("publish failure");
            }
        });

        Topic topic = new Topic("topic", QoS.AT_LEAST_ONCE);
        connection.subscribe(new Topic[]{topic}, new Callback<byte[]>() {
            @Override
            public void onSuccess(byte[] value) {
                System.out.println("new message");
                System.out.println(new String(value));
            }

            @Override
            public void onFailure(Throwable value) {
                System.out.println("failure.......");
            }
        });

        System.out.println("run end");
        Thread.currentThread().join();
    }
}
