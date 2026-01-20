package work.soho.longlink.cloud.bridge.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;
import work.soho.longlink.api.sender.Sender;
import work.soho.longlink.cloud.api.message.LongLinkBroadcastChannels;
import work.soho.longlink.cloud.api.message.LongLinkBroadcastMessage;
import work.soho.longlink.cloud.api.request.SenderBindUidRequest;
import work.soho.longlink.cloud.api.request.SenderSendToConnectIdRequest;
import work.soho.longlink.cloud.api.request.SenderSendToUidRequest;
import work.soho.longlink.cloud.bridge.feign.SenderServiceFeign;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SenderServiceImpl implements Sender {
    private static final String SERVICE_ID = "soho-long-link-coud-biz";
    private static final String META_LONGLINK_PORT = "longlinkPort";
    private static final String PATH_SEND_TO_UID = "/cloud/sender/sendToUid";
    private static final String PATH_SEND_TO_CONNECT_ID = "/cloud/sender/sendToConnectId";
    private static final String PATH_SEND_TO_ALL_UID = "/cloud/sender/sendToAllUid";
    private static final String PATH_SEND_TO_ALL_CONNECT = "/cloud/sender/sendToAllConnect";
    private static final String PATH_BIND_UID = "/cloud/sender/bindUid";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final SenderServiceFeign senderServiceFeign;
    private final DiscoveryClient discoveryClient;
    private final ObjectProvider<StringRedisTemplate> stringRedisTemplateProvider;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void sendToUid(String uid, String msg) {
        if (publishBroadcast(new LongLinkBroadcastMessage(LongLinkBroadcastMessage.Action.SEND_TO_UID, uid, null, msg))) {
            return;
        }
        SenderSendToUidRequest request = new SenderSendToUidRequest();
        request.setUid(uid);
        request.setMsg(msg);
        if (!broadcastPost(PATH_SEND_TO_UID, request)) {
            senderServiceFeign.sendToUid(request);
        }
    }

    @Override
    public void sendToConnectId(String connectId, String msg) {
        if (publishBroadcast(new LongLinkBroadcastMessage(LongLinkBroadcastMessage.Action.SEND_TO_CONNECT_ID, null, connectId, msg))) {
            return;
        }
        SenderSendToConnectIdRequest request = new SenderSendToConnectIdRequest();
        request.setConnectId(connectId);
        request.setMsg(msg);
        ServiceInstance instance = resolveInstanceByConnectId(connectId);
        if (instance != null) {
            if (postToInstance(instance.getUri(), PATH_SEND_TO_CONNECT_ID, request)) {
                return;
            }
        }
        if (!broadcastPost(PATH_SEND_TO_CONNECT_ID, request)) {
            senderServiceFeign.sendToConnectId(request);
        }
    }

    @Override
    public void sendToAllUid(String msg) {
        if (publishBroadcast(new LongLinkBroadcastMessage(LongLinkBroadcastMessage.Action.SEND_TO_ALL_UID, null, null, msg))) {
            return;
        }
        if (!broadcastPost(PATH_SEND_TO_ALL_UID, msg)) {
            senderServiceFeign.sendToAllUid(msg);
        }
    }

    @Override
    public void sendToAllConnect(String msg) {
        if (publishBroadcast(new LongLinkBroadcastMessage(LongLinkBroadcastMessage.Action.SEND_TO_ALL_CONNECT, null, null, msg))) {
            return;
        }
        if (!broadcastPost(PATH_SEND_TO_ALL_CONNECT, msg)) {
            senderServiceFeign.sendToAllConnect(msg);
        }
    }

    @Override
    public void bindUid(String connectId, String uid) {
        if (publishBroadcast(new LongLinkBroadcastMessage(LongLinkBroadcastMessage.Action.BIND_UID, uid, connectId, null))) {
            return;
        }
        SenderBindUidRequest request = new SenderBindUidRequest();
        request.setConnectId(connectId);
        request.setUid(uid);
        ServiceInstance instance = resolveInstanceByConnectId(connectId);
        if (instance != null) {
            if (postToInstance(instance.getUri(), PATH_BIND_UID, request)) {
                return;
            }
        }
        if (!broadcastPost(PATH_BIND_UID, request)) {
            senderServiceFeign.bindUid(request);
        }
    }

    private boolean broadcastPost(String path, Object body) {
        List<ServiceInstance> instances = discoveryClient.getInstances(SERVICE_ID);
        if (instances == null || instances.isEmpty()) {
            return false;
        }
        if (instances.size() == 1) {
            return postToInstance(instances.get(0).getUri(), path, body);
        }
        for (ServiceInstance instance : instances) {
            postToInstance(instance.getUri(), path, body);
        }
        return true;
    }

    private boolean postToInstance(URI baseUri, String path, Object body) {
        try {
            restTemplate.postForLocation(baseUri.resolve(path), body);
            return true;
        } catch (Exception e) {
            log.warn("longlink post failed: {}{}", baseUri, path, e);
            return false;
        }
    }

    private ServiceInstance resolveInstanceByConnectId(String connectId) {
        if (connectId == null || connectId.isEmpty()) {
            return null;
        }
        String[] parts = connectId.split("/");
        if (parts.length < 4) {
            return null;
        }
        String serverIp = parts[2];
        String serverPort = parts[3];
        List<ServiceInstance> instances = discoveryClient.getInstances(SERVICE_ID);
        if (instances == null || instances.isEmpty()) {
            return null;
        }
        for (ServiceInstance instance : instances) {
            if (!serverIp.equals(instance.getHost())) {
                continue;
            }
            Map<String, String> metadata = instance.getMetadata();
            if (metadata != null && serverPort.equals(metadata.get(META_LONGLINK_PORT))) {
                return instance;
            }
            if (metadata == null || !metadata.containsKey(META_LONGLINK_PORT)) {
                if (Integer.toString(instance.getPort()).equals(serverPort)) {
                    return instance;
                }
            }
        }
        return null;
    }

    private boolean publishBroadcast(LongLinkBroadcastMessage message) {
        StringRedisTemplate redisTemplate = stringRedisTemplateProvider.getIfAvailable();
        if (redisTemplate == null) {
            return false;
        }
        try {
            String payload = OBJECT_MAPPER.writeValueAsString(message);
            redisTemplate.convertAndSend(LongLinkBroadcastChannels.SENDER_CHANNEL, payload);
            return true;
        } catch (Exception e) {
            log.warn("longlink redis broadcast failed", e);
            return false;
        }
    }
}
