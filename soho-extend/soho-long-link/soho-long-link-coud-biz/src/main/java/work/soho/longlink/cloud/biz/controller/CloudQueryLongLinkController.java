package work.soho.longlink.cloud.biz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.longlink.api.sender.QueryLongLink;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cloud/queryLongLink")
public class CloudQueryLongLinkController {
    private final QueryLongLink queryLongLink;

    @PostMapping("getOnlineStatus")
    public Map<String, Integer> getOnlineStatus(@RequestBody List<String> uids) {
        return queryLongLink.getOnlineStatus(uids);
    }
}
