package work.soho.longlink.cloud.bridge.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import work.soho.longlink.api.enums.OnlineEnum;
import work.soho.longlink.api.sender.QueryLongLink;
import work.soho.longlink.cloud.bridge.feign.CloudQueryLongLinkFeign;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueryLongLinkServiceImpl implements QueryLongLink {
    private static final String SERVICE_ID = "soho-long-link-coud-biz";
    private static final String PATH_GET_ONLINE_STATUS = "/cloud/queryLongLink/getOnlineStatus";

    private final CloudQueryLongLinkFeign cloudQueryLongLinkFeign;
    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Map<String, Integer> getOnlineStatus(List<String> uids) {
        List<ServiceInstance> instances = discoveryClient.getInstances(SERVICE_ID);
        if (instances == null || instances.isEmpty()) {
            return cloudQueryLongLinkFeign.getOnlineStatus(uids);
        }
        if (instances.size() == 1) {
            return cloudQueryLongLinkFeign.getOnlineStatus(uids);
        }

        if (uids == null || uids.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Integer> result = new HashMap<>();
        for (String uid : uids) {
            result.put(uid, OnlineEnum.NOT_ONLINE.getId());
        }

        for (ServiceInstance instance : instances) {
            try {
                HttpEntity<List<String>> entity = new HttpEntity<>(uids);
                ResponseEntity<Map<String, Integer>> response = restTemplate.exchange(
                        instance.getUri().resolve(PATH_GET_ONLINE_STATUS),
                        HttpMethod.POST,
                        entity,
                        new ParameterizedTypeReference<Map<String, Integer>>() {}
                );
                Map<String, Integer> data = response.getBody();
                if (data == null) {
                    continue;
                }
                for (Map.Entry<String, Integer> entry : data.entrySet()) {
                    Integer current = result.get(entry.getKey());
                    Integer incoming = entry.getValue();
                    if (incoming != null && (current == null || incoming > current)) {
                        result.put(entry.getKey(), incoming);
                    }
                }
            } catch (Exception e) {
                log.warn("longlink online status broadcast failed: {}", instance.getUri(), e);
            }
        }

        return result;
    }
}
