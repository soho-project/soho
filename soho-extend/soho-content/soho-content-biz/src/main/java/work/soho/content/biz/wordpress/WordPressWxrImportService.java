package work.soho.content.biz.wordpress;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import work.soho.content.biz.domain.ContentCategory;
import work.soho.content.biz.domain.ContentComment;
import work.soho.content.biz.domain.ContentExternalMapping;
import work.soho.content.biz.domain.ContentInfo;
import work.soho.content.biz.domain.ContentMedia;
import work.soho.content.biz.domain.ContentTag;
import work.soho.content.biz.domain.ContentTagRelation;
import work.soho.content.biz.service.AdminContentCategoryService;
import work.soho.content.biz.service.AdminContentService;
import work.soho.content.biz.service.ContentCommentService;
import work.soho.content.biz.service.ContentExternalMappingService;
import work.soho.content.biz.service.ContentMediaService;
import work.soho.content.biz.service.ContentTagRelationService;
import work.soho.content.biz.service.ContentTagService;
import work.soho.content.biz.wordpress.dto.WordPressWxrImportResult;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WordPress WXR 导入服务
 */
@Service
public class WordPressWxrImportService {
    private static final DateTimeFormatter WP_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AdminContentService contentService;
    private final AdminContentCategoryService categoryService;
    private final ContentTagService tagService;
    private final ContentTagRelationService tagRelationService;
    private final ContentMediaService mediaService;
    private final ContentCommentService commentService;
    private final ContentExternalMappingService externalMappingService;

    public WordPressWxrImportService(AdminContentService contentService,
                                     AdminContentCategoryService categoryService,
                                     ContentTagService tagService,
                                     ContentTagRelationService tagRelationService,
                                     ContentMediaService mediaService,
                                     ContentCommentService commentService,
                                     ContentExternalMappingService externalMappingService) {
        this.contentService = contentService;
        this.categoryService = categoryService;
        this.tagService = tagService;
        this.tagRelationService = tagRelationService;
        this.mediaService = mediaService;
        this.commentService = commentService;
        this.externalMappingService = externalMappingService;
    }

    public WordPressWxrImportResult importWxr(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("WXR 文件不能为空");
        }

        Map<String, ContentCategory> categoryCache = new HashMap<>();
        Map<String, ContentTag> tagCache = new HashMap<>();
        Map<Long, Long> contentIdByExternalId = new HashMap<>();
        WordPressWxrImportResult result = new WordPressWxrImportResult();

        try (InputStream input = file.getInputStream()) {
            WxrResult wxrResult = parseWxr(input);
            for (WxrItem item : wxrResult.items) {
                if ("attachment".equalsIgnoreCase(item.postType)) {
                    if (createMediaIfNeeded(item)) {
                        result.incrementMedia();
                    }
                    continue;
                }

                if (!"post".equalsIgnoreCase(item.postType) && !"page".equalsIgnoreCase(item.postType)) {
                    continue;
                }

                ContentCategory category = null;
                if (!item.categories.isEmpty()) {
                    WxrCategory primary = item.categories.get(0);
                    if ("category".equalsIgnoreCase(primary.domain)) {
                        category = findOrCreateCategory(primary, categoryCache);
                    }
                }

                ContentInfo content = findOrCreateContent(item, category);
                contentIdByExternalId.put(item.postId, content.getId());
                ensureExternalMapping(item, content.getId());

                for (WxrCategory cat : item.categories) {
                    if ("post_tag".equalsIgnoreCase(cat.domain)) {
                        ContentTag tag = findOrCreateTag(cat, tagCache);
                        ensureTagRelation(content.getId(), tag.getId());
                    }
                }

                for (WxrComment comment : item.comments) {
                    Long contentId = contentIdByExternalId.get(item.postId);
                    if (contentId != null && createCommentIfNeeded(comment, contentId)) {
                        result.incrementComments();
                    }
                }

                if ("post".equalsIgnoreCase(item.postType)) {
                    result.incrementPosts();
                } else {
                    result.incrementPages();
                }
            }
        } catch (Exception ex) {
            throw new IllegalStateException("WXR 导入失败: " + ex.getMessage(), ex);
        }

        return result;
    }

    private ContentInfo findOrCreateContent(WxrItem item, ContentCategory category) {
        ContentExternalMapping existingMapping = externalMappingService.lambdaQuery()
                .eq(ContentExternalMapping::getExternalObjectId, item.postId)
                .eq(ContentExternalMapping::getExternalType, item.postType)
                .last("limit 1")
                .one();
        if (existingMapping != null && existingMapping.getContentId() != null) {
            ContentInfo existing = contentService.getById(existingMapping.getContentId());
            if (existing != null) {
                return existing;
            }
        }

        ContentInfo content = new ContentInfo();
        content.setTitle(item.title);
        content.setBody(item.content);
        content.setDescription(item.excerpt);
        content.setStatus(resolveStatus(item.status));
        content.setCreatedTime(parseDate(item.postDate));
        content.setUpdatedTime(parseDate(item.postDate));
        content.setUserId(1L);
        if (category != null) {
            content.setCategoryId(category.getId());
        }
        contentService.save(content);
        return content;
    }

    private void ensureExternalMapping(WxrItem item, Long contentId) {
        ContentExternalMapping mapping = externalMappingService.lambdaQuery()
                .eq(ContentExternalMapping::getExternalObjectId, item.postId)
                .eq(ContentExternalMapping::getExternalType, item.postType)
                .last("limit 1")
                .one();
        if (mapping != null) {
            if (mapping.getContentId() == null) {
                mapping.setContentId(contentId);
                externalMappingService.updateById(mapping);
            }
            return;
        }

        ContentExternalMapping newMapping = new ContentExternalMapping();
        newMapping.setContentId(contentId);
        newMapping.setExternalObjectId(item.postId);
        newMapping.setExternalType(item.postType);
        newMapping.setExternalSlug(item.slug);
        newMapping.setExternalStatus(item.status);
        newMapping.setExternalPassword(item.postPassword);
        newMapping.setExternalCommentStatus(item.commentStatus);
        newMapping.setExternalPingStatus(item.pingStatus);
        newMapping.setExternalMenuOrder(item.menuOrder);
        newMapping.setExternalParentId(item.parentId);
        externalMappingService.save(newMapping);
    }

    private ContentCategory findOrCreateCategory(WxrCategory category, Map<String, ContentCategory> cache) {
        String name = safeText(category.name);
        if (cache.containsKey(name)) {
            return cache.get(name);
        }
        ContentCategory existing = categoryService.lambdaQuery()
                .eq(ContentCategory::getName, name)
                .last("limit 1")
                .one();
        if (existing != null) {
            cache.put(name, existing);
            return existing;
        }
        ContentCategory entity = new ContentCategory();
        entity.setName(name);
        entity.setKeyword(category.slug);
        entity.setParentId(0L);
        entity.setIsDisplay(1);
        categoryService.save(entity);
        cache.put(name, entity);
        return entity;
    }

    private ContentTag findOrCreateTag(WxrCategory tag, Map<String, ContentTag> cache) {
        String key = safeText(tag.slug);
        if (!StringUtils.hasText(key)) {
            key = safeText(tag.name);
        }
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        ContentTag existing = tagService.lambdaQuery()
                .eq(ContentTag::getSlug, tag.slug)
                .or()
                .eq(ContentTag::getName, tag.name)
                .last("limit 1")
                .one();
        if (existing != null) {
            cache.put(key, existing);
            return existing;
        }
        ContentTag entity = new ContentTag();
        entity.setName(tag.name);
        entity.setSlug(tag.slug);
        entity.setDescription(tag.description);
        tagService.save(entity);
        cache.put(key, entity);
        return entity;
    }

    private void ensureTagRelation(Long contentId, Long tagId) {
        if (contentId == null || tagId == null) {
            return;
        }
        boolean exists = tagRelationService.lambdaQuery()
                .eq(ContentTagRelation::getContentId, contentId)
                .eq(ContentTagRelation::getTagId, tagId)
                .count() > 0;
        if (exists) {
            return;
        }
        ContentTagRelation relation = new ContentTagRelation();
        relation.setContentId(contentId);
        relation.setTagId(tagId);
        tagRelationService.save(relation);
    }

    private boolean createMediaIfNeeded(WxrItem item) {
        if (item.postId == null || !StringUtils.hasText(item.attachmentUrl)) {
            return false;
        }
        ContentMedia existing = mediaService.lambdaQuery()
                .eq(ContentMedia::getExternalMediaId, item.postId)
                .last("limit 1")
                .one();
        if (existing != null) {
            return false;
        }
        ContentMedia media = new ContentMedia();
        media.setExternalMediaId(item.postId);
        media.setTitle(item.title);
        media.setUrl(item.attachmentUrl);
        media.setMimeType(item.mimeType);
        media.setUserId(1L);
        mediaService.save(media);
        return true;
    }

    private boolean createCommentIfNeeded(WxrComment comment, Long contentId) {
        if (comment.commentId == null) {
            return false;
        }
        ContentComment existing = commentService.lambdaQuery()
                .eq(ContentComment::getExternalCommentId, comment.commentId)
                .last("limit 1")
                .one();
        if (existing != null) {
            return false;
        }
        ContentComment entity = new ContentComment();
        entity.setExternalCommentId(comment.commentId);
        entity.setContentId(contentId);
        entity.setParentId(comment.parentId == null ? 0L : comment.parentId);
        entity.setAuthorName(comment.authorName);
        entity.setAuthorEmail(comment.authorEmail);
        entity.setAuthorUrl(comment.authorUrl);
        entity.setContent(comment.content);
        entity.setStatus(comment.status);
        entity.setCreatedTime(parseDate(comment.date));
        entity.setUpdatedTime(parseDate(comment.date));
        commentService.save(entity);
        return true;
    }

    private WxrResult parseWxr(InputStream input) throws Exception {
        InputStream sanitized = new FilterInputStream(input) {
            @Override
            public int read() throws java.io.IOException {
                int ch;
                do {
                    ch = super.read();
                } while (ch == 0);
                return ch;
            }

            @Override
            public int read(byte[] b, int off, int len) throws java.io.IOException {
                int count = super.read(b, off, len);
                if (count <= 0) {
                    return count;
                }
                int writePos = off;
                int end = off + count;
                for (int i = off; i < end; i++) {
                    if (b[i] != 0) {
                        b[writePos++] = b[i];
                    }
                }
                return writePos - off;
            }
        };

        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        XMLStreamReader reader = factory.createXMLStreamReader(sanitized);

        WxrResult result = new WxrResult();
        WxrItem currentItem = null;
        WxrComment currentComment = null;

        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                String local = reader.getLocalName();
                String ns = reader.getNamespaceURI();

                if ("item".equals(local)) {
                    currentItem = new WxrItem();
                    currentItem.categories = new ArrayList<>();
                    currentItem.comments = new ArrayList<>();
                    continue;
                }

                if (currentItem != null && isWp(ns) && "comment".equals(local)) {
                    currentComment = new WxrComment();
                    continue;
                }

                if (currentComment != null) {
                    if (isWp(ns)) {
                        handleCommentField(reader, currentComment, local);
                    }
                    continue;
                }

                if (currentItem != null) {
                    if ("category".equals(local)) {
                        WxrCategory category = new WxrCategory();
                        category.domain = reader.getAttributeValue(null, "domain");
                        category.slug = reader.getAttributeValue(null, "nicename");
                        category.name = readText(reader);
                        currentItem.categories.add(category);
                        continue;
                    }
                    handleItemField(reader, currentItem, local, ns);
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                String local = reader.getLocalName();
                String ns = reader.getNamespaceURI();
                if (currentItem != null && currentComment != null && isWp(ns) && "comment".equals(local)) {
                    currentItem.comments.add(currentComment);
                    currentComment = null;
                }
                if ("item".equals(local) && currentItem != null) {
                    result.items.add(currentItem);
                    currentItem = null;
                }
            }
        }
        reader.close();
        return result;
    }

    private void handleItemField(XMLStreamReader reader, WxrItem item, String local, String ns) throws Exception {
        if ("title".equals(local)) {
            item.title = readText(reader);
        } else if ("encoded".equals(local) && isContent(ns)) {
            item.content = readText(reader);
        } else if ("encoded".equals(local) && isExcerpt(ns)) {
            item.excerpt = readText(reader);
        } else if (isWp(ns)) {
            switch (local) {
                case "post_id":
                    item.postId = parseLong(readText(reader));
                    break;
                case "post_date":
                    item.postDate = readText(reader);
                    break;
                case "post_parent":
                    item.parentId = parseLong(readText(reader));
                    break;
                case "post_type":
                    item.postType = readText(reader);
                    break;
                case "status":
                    item.status = readText(reader);
                    break;
                case "post_name":
                    item.slug = readText(reader);
                    break;
                case "comment_status":
                    item.commentStatus = readText(reader);
                    break;
                case "ping_status":
                    item.pingStatus = readText(reader);
                    break;
                case "menu_order":
                    item.menuOrder = parseInt(readText(reader));
                    break;
                case "post_password":
                    item.postPassword = readText(reader);
                    break;
                case "post_mime_type":
                    item.mimeType = readText(reader);
                    break;
                case "attachment_url":
                    item.attachmentUrl = readText(reader);
                    break;
                default:
                    skipElement(reader);
            }
        } else {
            skipElement(reader);
        }
    }

    private void handleCommentField(XMLStreamReader reader, WxrComment comment, String local) throws Exception {
        switch (local) {
            case "comment_id":
                comment.commentId = parseLong(readText(reader));
                break;
            case "comment_parent":
                comment.parentId = parseLong(readText(reader));
                break;
            case "comment_author":
                comment.authorName = readText(reader);
                break;
            case "comment_author_email":
                comment.authorEmail = readText(reader);
                break;
            case "comment_author_url":
                comment.authorUrl = readText(reader);
                break;
            case "comment_date":
                comment.date = readText(reader);
                break;
            case "comment_content":
                comment.content = readText(reader);
                break;
            case "comment_approved":
                comment.status = readText(reader);
                break;
            default:
                skipElement(reader);
        }
    }

    private static boolean isWp(String ns) {
        return "http://wordpress.org/export/1.2/".equals(ns);
    }

    private static boolean isContent(String ns) {
        return "http://purl.org/rss/1.0/modules/content/".equals(ns);
    }

    private static boolean isExcerpt(String ns) {
        return "http://wordpress.org/export/1.2/excerpt/".equals(ns);
    }

    private static String readText(XMLStreamReader reader) throws Exception {
        return reader.getElementText();
    }

    private static void skipElement(XMLStreamReader reader) throws Exception {
        if (!reader.isStartElement()) {
            return;
        }
        int depth = 1;
        while (depth > 0 && reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                depth++;
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                depth--;
            }
        }
    }

    private static Integer resolveStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return 0;
        }
        String lower = status.toLowerCase();
        if ("publish".equals(lower) || "future".equals(lower)) {
            return 1;
        }
        return 0;
    }

    private static LocalDateTime parseDate(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return LocalDateTime.parse(value.trim(), WP_DATE);
    }

    private static Long parseLong(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static Integer parseInt(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static String safeText(String value) {
        return value == null ? "" : value;
    }

    /**
     * WXR 解析结果
     */
    private static class WxrResult {
        private final List<WxrItem> items = new ArrayList<>();
    }

    /**
     * WXR 条目
     */
    private static class WxrItem {
        private Long postId;
        private String postType;
        private String status;
        private String slug;
        private String title;
        private String content;
        private String excerpt;
        private String postDate;
        private Long parentId;
        private Integer menuOrder;
        private String postPassword;
        private String commentStatus;
        private String pingStatus;
        private String attachmentUrl;
        private String mimeType;
        private List<WxrCategory> categories;
        private List<WxrComment> comments;
    }

    /**
     * WXR 分类/标签
     */
    private static class WxrCategory {
        private String domain;
        private String slug;
        private String name;
        private String description;
    }

    /**
     * WXR 评论
     */
    private static class WxrComment {
        private Long commentId;
        private Long parentId;
        private String authorName;
        private String authorEmail;
        private String authorUrl;
        private String date;
        private String content;
        private String status;
    }
}
