package work.soho.content.biz.controller.guest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.vo.AdminContentCategoryListVo;
import work.soho.admin.api.vo.AdminContentVo;
import work.soho.admin.api.vo.NavVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.PageUtils;
import work.soho.content.biz.domain.ContentInfo;
import work.soho.content.biz.domain.ContentCategory;
import work.soho.content.biz.service.AdminContentCategoryService;
import work.soho.content.biz.service.AdminContentService;
import work.soho.user.api.dto.UserInfoDto;
import work.soho.user.api.service.UserApiService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javax.servlet.http.HttpServletRequest;

@Api(tags = "客户端内容接口")
@RestController
@RequestMapping("/content/guest/contentInfo")
@RequiredArgsConstructor
public class GuestContentController {
    private final AdminContentService adminContentService;
    private final UserApiService userApiService ;
    private final AdminContentCategoryService adminContentCategoryService;

    @GetMapping("list")
    @ApiOperation("获取内容列表")
    public R<List<ContentInfo>> list() {
        LambdaQueryWrapper<ContentInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ContentInfo::getStatus, 1);
        PageUtils.startPage();
        List<ContentInfo> list = adminContentService.list(lambdaQueryWrapper);
        return R.success(list);
    }

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

    @ApiOperation("获取内容分类")
    @GetMapping("category")
    public R<AdminContentCategoryListVo> category(Long id) {
        AdminContentCategoryListVo adminContentCategoryListVo;
        ContentCategory adminContentCategory = adminContentCategoryService.getById(id);
        adminContentCategoryListVo = BeanUtils.copy(adminContentCategory, AdminContentCategoryListVo.class);
        //获取文章导航信息
        List<ContentCategory> navaList = adminContentCategoryService.getCategorysBySonId(adminContentCategory.getParentId());
        for (ContentCategory adminContentCategory1: navaList) {
            AdminContentVo.NavItem nav = new AdminContentVo.NavItem();
            nav.setName(adminContentCategory1.getName());
            nav.setId(adminContentCategory1.getId());
            nav.setType(2);
            adminContentCategoryListVo.getNavs().add(nav);
        }

        return R.success(adminContentCategoryListVo);
    }

    @ApiOperation("获取内容导航")
    @GetMapping("nav")
    public R<List<NavVo>> nav() {
        List<ContentCategory> list = adminContentCategoryService.list();
        Map<Long, List<ContentCategory>> map = list.stream().collect(Collectors.groupingBy(ContentCategory::getParentId));
        NavVo tmpNavVo = new NavVo();
        tmpNavVo.setKey("0");
        loopNav(map, tmpNavVo);
        return R.success(tmpNavVo.getChildren());
    }

    /**
     * 递归导航
     *
     * @param map
     * @param navVo
     * @return
     */
    private void loopNav(Map<Long, List<ContentCategory>> map, NavVo navVo) {
        List<ContentCategory> listTmp = null;
        if((listTmp = map.get(Long.parseLong(navVo.getKey()))) != null) {
            for(ContentCategory item: listTmp) {
                NavVo nav = new NavVo();
                nav.setKey(String.valueOf(item.getId()));
                nav.setLabel(item.getName());
                navVo.getChildren().add(nav);
                loopNav(map, nav);
            }
        }
    }

    @ApiOperation("获取内容")
    @GetMapping("content")
    public R<AdminContentVo> content(Long id) {
        ContentInfo adminContent = adminContentService.getById(id);
        //检查文章状态
        if(adminContent == null || adminContent.getStatus() != 1) {
            return R.error("请传递有效文章ID");
        }
        AdminContentVo adminContentVo = BeanUtils.copy(adminContent, AdminContentVo.class);
        UserInfoDto user = userApiService.getUserById(adminContent.getUserId());
        if(user != null) {
            adminContentVo.setUsername(user.getUsername());
        }
        //获取文章导航信息
        List<ContentCategory> navaList = adminContentCategoryService.getCategorysBySonId(adminContent.getCategoryId());
        for (ContentCategory adminContentCategory: navaList) {
            AdminContentVo.NavItem nav = new AdminContentVo.NavItem();
            nav.setName(adminContentCategory.getName());
            nav.setId(adminContentCategory.getId());
            nav.setType(2);
            adminContentVo.getNavs().add(nav);
        }
        AdminContentVo.NavItem nav = new AdminContentVo.NavItem();
        nav.setName(adminContent.getTitle());
        nav.setId(adminContent.getId());
        nav.setType(3);
        adminContentVo.getNavs().add(nav);

        return R.success(adminContentVo);
    }

    @PostMapping("like")
    public R<Boolean> like(@RequestBody Map<String,Long> map) {
        ContentInfo adminContent = adminContentService.getById(map.get("id"));
        //检查文章状态
        if(adminContent == null || adminContent.getStatus() != 1) {
            return R.error("请传递有效文章ID");
        }
        adminContent.setLikes(adminContent.getLikes()+1);
        adminContentService.updateById(adminContent);
        return R.success();
    }

    @PostMapping("disLike")
    public R<Boolean> dislike(@RequestBody Map<String,Long> map) {
        ContentInfo adminContent = adminContentService.getById(map.get("id"));
        //检查文章状态
        if(adminContent == null || adminContent.getStatus() != 1) {
            return R.error("请传递有效文章ID");
        }
        adminContent.setDisLikes(adminContent.getDisLikes()+1);
        adminContentService.updateById(adminContent);
        return R.success();
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
