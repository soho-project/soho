package work.soho.longlink.cloud.bridge.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.longlink.api.sender.QueryLongLink;
import work.soho.longlink.cloud.bridge.feign.CloudQueryLongLinkFeign;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QueryLongLinkServiceImpl implements QueryLongLink {
    private final CloudQueryLongLinkFeign cloudQueryLongLinkFeign;

    @Override
    public Map<String, Integer> getOnlineStatus(List<String> uids) {
        return cloudQueryLongLinkFeign.getOnlineStatus(uids);
    }
}
