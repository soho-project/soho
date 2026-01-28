package work.soho.content.biz.controller.guest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.content.biz.domain.ContentInfo;
import work.soho.content.biz.service.AdminContentService;

import javax.servlet.http.HttpServletRequest;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Api(tags = "客户端内容接口")
@RestController
@RequestMapping("/content/guest/feed")
@RequiredArgsConstructor
public class GuestFeedController {
    private final AdminContentService adminContentService;

    @ApiOperation("rss feed")
    @GetMapping(value = "rss", produces = "application/rss+xml;charset=UTF-8")
    public String rss(HttpServletRequest request) {
        LambdaQueryWrapper<ContentInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ContentInfo::getStatus, 1);
        lambdaQueryWrapper.orderByDesc(ContentInfo::getId);
        List<ContentInfo> list = adminContentService.list(lambdaQueryWrapper);

        String baseUrl = buildBaseUrl(request);
        StringBuilder builder = new StringBuilder(4096);
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        builder.append("<rss version=\"2.0\">\n");
        builder.append("<channel>\n");
        builder.append("<title>").append(escapeXml("soho-content")).append("</title>\n");
        builder.append("<link>").append(escapeXml(baseUrl)).append("</link>\n");
        builder.append("<description>").append(escapeXml("内容模块 RSS")).append("</description>\n");
        builder.append("<lastBuildDate>")
                .append(ZonedDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.RFC_1123_DATE_TIME))
                .append("</lastBuildDate>\n");

        for (ContentInfo content : list) {
            String itemLink = baseUrl + "/content/guest/api/content/content?id=" + content.getId();
            String description = content.getDescription();
            if (description == null || description.isEmpty()) {
                description = content.getBody();
            }
            builder.append("<item>\n");
            builder.append("<title>").append(cdata(content.getTitle())).append("</title>\n");
            builder.append("<link>").append(escapeXml(itemLink)).append("</link>\n");
            builder.append("<guid>").append(escapeXml(itemLink)).append("</guid>\n");
            builder.append("<description>").append(cdata(description)).append("</description>\n");
            if (content.getCreatedTime() != null) {
                builder.append("<pubDate>")
                        .append(content.getCreatedTime().atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.RFC_1123_DATE_TIME))
                        .append("</pubDate>\n");
            }
            builder.append("</item>\n");
        }
        builder.append("</channel>\n");
        builder.append("</rss>\n");
        return builder.toString();
    }

    private String buildBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String host = request.getServerName();
        int port = request.getServerPort();
        boolean defaultPort = ("http".equalsIgnoreCase(scheme) && port == 80)
                || ("https".equalsIgnoreCase(scheme) && port == 443);
        return defaultPort ? scheme + "://" + host : scheme + "://" + host + ":" + port;
    }

    private String escapeXml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private String cdata(String value) {
        String safe = value == null ? "" : value;
        return "<![CDATA[" + safe.replace("]]>", "]]]]><![CDATA[>") + "]]>";
    }
}
