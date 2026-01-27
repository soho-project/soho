package work.soho.content.biz.wordpress;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import work.soho.content.biz.domain.ContentCategory;
import work.soho.content.biz.domain.ContentComment;
import work.soho.content.biz.domain.ContentInfo;
import work.soho.content.biz.domain.ContentMedia;
import work.soho.content.biz.domain.ContentTag;
import work.soho.content.biz.domain.ContentTagRelation;
import work.soho.content.biz.domain.ContentExternalMapping;
import work.soho.content.biz.service.AdminContentCategoryService;
import work.soho.content.biz.service.AdminContentService;
import work.soho.content.biz.service.ContentCommentService;
import work.soho.content.biz.service.ContentMediaService;
import work.soho.content.biz.service.ContentTagRelationService;
import work.soho.content.biz.service.ContentTagService;
import work.soho.content.biz.service.ContentExternalMappingService;
import work.soho.content.biz.wordpress.dto.WordPressCommentDto;
import work.soho.content.biz.wordpress.dto.WordPressMediaDto;
import work.soho.content.biz.wordpress.dto.WordPressPostDto;
import work.soho.content.biz.wordpress.dto.WordPressPropertiesRequest;
import work.soho.content.biz.wordpress.dto.WordPressSyncRequest;
import work.soho.content.biz.wordpress.dto.WordPressSyncResult;
import work.soho.content.biz.wordpress.dto.WordPressTermDto;
import work.soho.content.biz.wordpress.dto.WordPressUserDto;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.IntFunction;
import java.util.List;
import java.util.Map;

/**
 * WordPress 导入服务
 */
@Service
public class WordPressSyncService {
    private final WordPressClient defaultClient;
    private final WordPressProperties defaultProperties;
    private final AdminContentService contentService;
    private final AdminContentCategoryService categoryService;
    private final ContentTagService tagService;
    private final ContentTagRelationService tagRelationService;
    private final ContentMediaService mediaService;
    private final ContentCommentService commentService;
    private final ContentExternalMappingService mappingService;

    public WordPressSyncService(WordPressClient defaultClient,
                                WordPressProperties defaultProperties,
                                AdminContentService contentService,
                                AdminContentCategoryService categoryService,
                                ContentTagService tagService,
                                ContentTagRelationService tagRelationService,
                                ContentMediaService mediaService,
                                ContentCommentService commentService,
                                ContentExternalMappingService mappingService) {
        this.defaultClient = defaultClient;
        this.defaultProperties = defaultProperties;
        this.contentService = contentService;
        this.categoryService = categoryService;
        this.tagService = tagService;
        this.tagRelationService = tagRelationService;
        this.mediaService = mediaService;
        this.commentService = commentService;
        this.mappingService = mappingService;
    }

    public WordPressSyncResult importFromWordPress(WordPressSyncRequest request) {
        WordPressSyncResult result = new WordPressSyncResult();
        int startPage = Math.max(1, request.getPage());
        int perPage = request.getPerPage() > 0 ? request.getPerPage() : 50;
        WordPressClient client = resolveClient(request);
        Map<Long, Long> categoryMap = new HashMap<>();
        Map<Long, Long> tagMap = new HashMap<>();
        if (request.isCategories()) {
            result.setCategoriesImported(importCategories(client, startPage, perPage, categoryMap));
        }
        if (request.isTags()) {
            result.setTagsImported(importTags(client, startPage, perPage, tagMap));
        }
        if (request.isUsers()) {
            result.setUsersImported(importUsers(client, startPage, perPage));
        }
        if (request.isMedia()) {
            result.setMediaImported(importMedia(client, startPage, perPage));
        }
        if (request.isPosts()) {
            result.setPostsImported(importPosts(client, "post", startPage, perPage, request.isUpsert(), categoryMap, tagMap));
        }
        if (request.isPages()) {
            result.setPagesImported(importPosts(client, "page", startPage, perPage, request.isUpsert(), categoryMap, tagMap));
        }
        if (request.isComments()) {
            result.setCommentsImported(importComments(client, startPage, perPage, request.isUpsert()));
        }
        return result;
    }

    private int importCategories(WordPressClient client, int startPage, int perPage, Map<Long, Long> categoryMap) {
        List<WordPressTermDto> categories = fetchAllPages(page -> client.getCategories(page, perPage), startPage, perPage);
        if (CollectionUtils.isEmpty(categories)) {
            return 0;
        }
        for (WordPressTermDto term : categories) {
            ContentCategory category = upsertCategory(term, 0L);
            if (term.getId() != null && category.getId() != null) {
                categoryMap.put(term.getId(), category.getId());
            }
        }
        for (WordPressTermDto term : categories) {
            if (term.getParent() != null && term.getParent() > 0) {
                Long localId = categoryMap.get(term.getId());
                Long parentId = categoryMap.get(term.getParent());
                if (localId != null && parentId != null) {
                    ContentCategory category = categoryService.getById(localId);
                    category.setParentId(parentId);
                    categoryService.updateById(category);
                }
            }
        }
        return categories.size();
    }

    private ContentCategory upsertCategory(WordPressTermDto term, Long parentId) {
        ContentCategory category = matchCategory(term);
        if (category == null) {
            category = new ContentCategory();
            category.setCreatedTime(LocalDateTime.now());
        }
        category.setName(term.getName());
        category.setDescription(term.getDescription());
        category.setKeyword(term.getSlug());
        category.setParentId(parentId);
        category.setUpdateTime(LocalDateTime.now());
        if (category.getId() == null) {
            categoryService.save(category);
        } else {
            categoryService.updateById(category);
        }
        return category;
    }

    private int importTags(WordPressClient client, int startPage, int perPage, Map<Long, Long> tagMap) {
        List<WordPressTermDto> tags = fetchAllPages(page -> client.getTags(page, perPage), startPage, perPage);
        if (CollectionUtils.isEmpty(tags)) {
            return 0;
        }
        for (WordPressTermDto term : tags) {
            ContentTag tag = upsertTag(term);
            if (term.getId() != null && tag.getId() != null) {
                tagMap.put(term.getId(), tag.getId());
            }
        }
        return tags.size();
    }

    private int importUsers(WordPressClient client, int startPage, int perPage) {
        List<WordPressUserDto> users = fetchAllPages(page -> client.getUsers(page, perPage), startPage, perPage);
        if (CollectionUtils.isEmpty(users)) {
            return 0;
        }
        // 当前模块没有用户创建/写入能力，这里至少确认接口可用并返回数量。
        return users.size();
    }

    private ContentTag upsertTag(WordPressTermDto term) {
        ContentTag tag = matchTag(term);
        if (tag == null) {
            tag = new ContentTag();
            tag.setCreatedTime(LocalDateTime.now());
        }
        tag.setName(term.getName());
        tag.setSlug(term.getSlug());
        tag.setDescription(term.getDescription());
        tag.setUpdatedTime(LocalDateTime.now());
        if (tag.getId() == null) {
            tagService.save(tag);
        } else {
            tagService.updateById(tag);
        }
        return tag;
    }

    private int importMedia(WordPressClient client, int startPage, int perPage) {
        List<WordPressMediaDto> mediaList = fetchAllPages(page -> client.getMedia(page, perPage), startPage, perPage);
        if (CollectionUtils.isEmpty(mediaList)) {
            return 0;
        }
        for (WordPressMediaDto mediaDto : mediaList) {
            ContentMedia media = mediaService.getOne(new LambdaQueryWrapper<ContentMedia>()
                    .eq(ContentMedia::getExternalMediaId, mediaDto.getId())
                    .orderByDesc(ContentMedia::getId), false);
            if (media == null) {
                media = new ContentMedia();
                media.setExternalMediaId(mediaDto.getId());
                media.setCreatedTime(parseDate(mediaDto.getDate()));
            }
            media.setTitle(mediaDto.getTitle() != null ? mediaDto.getTitle().getRendered() : null);
            media.setUrl(mediaDto.getSourceUrl());
            media.setMimeType(mediaDto.getMimeType());
            media.setDescription(mediaDto.getCaption() != null ? mediaDto.getCaption().getRendered() : null);
            media.setAltText(mediaDto.getAltText());
            media.setUserId(null);
            media.setUpdatedTime(parseDate(mediaDto.getDate()));
            if (media.getId() == null) {
                mediaService.save(media);
            } else {
                mediaService.updateById(media);
            }
        }
        return mediaList.size();
    }

    private int importPosts(WordPressClient client, String type, int startPage, int perPage, boolean upsert,
                            Map<Long, Long> categoryMap, Map<Long, Long> tagMap) {
        List<WordPressPostDto> posts = fetchAllPages(page -> client.getPosts(type, page, perPage), startPage, perPage);
        if (CollectionUtils.isEmpty(posts)) {
            return 0;
        }
        for (WordPressPostDto post : posts) {
            ContentExternalMapping mapping = mappingService.getOne(new LambdaQueryWrapper<ContentExternalMapping>()
                    .eq(ContentExternalMapping::getExternalObjectId, post.getId())
                    .eq(ContentExternalMapping::getExternalType, type)
                    .orderByDesc(ContentExternalMapping::getId), false);
            ContentInfo content = null;
            if (mapping != null) {
                content = contentService.getById(mapping.getContentId());
            }
            if (content == null && !upsert) {
                continue;
            }
            if (content == null) {
                content = new ContentInfo();
                content.setCreatedTime(parseDate(post.getDate()));
            }
            content.setTitle(post.getTitle() != null ? post.getTitle().getRendered() : null);
            content.setBody(post.getContent() != null ? post.getContent().getRendered() : null);
            content.setDescription(post.getExcerpt() != null ? post.getExcerpt().getRendered() : null);
            content.setStatus("publish".equalsIgnoreCase(post.getStatus()) ? 1 : 0);
            content.setIsTop(post.isSticky() ? 1 : 0);
            content.setUpdatedTime(parseDate(post.getModified()));
            content.setUserId(null);
            content.setCategoryId(resolveCategoryId(client, post.getCategories(), categoryMap));
            if (content.getId() == null) {
                contentService.save(content);
            } else {
                contentService.updateById(content);
            }
            if (mapping == null) {
                mapping = new ContentExternalMapping();
                mapping.setExternalObjectId(post.getId());
                mapping.setExternalType(type);
                mapping.setCreatedTime(LocalDateTime.now());
            }
            mapping.setContentId(content.getId());
            mapping.setExternalSlug(post.getSlug());
            mapping.setExternalStatus(post.getStatus());
            mapping.setExternalFeaturedMediaId(post.getFeaturedMedia());
            mapping.setExternalCommentStatus(post.getCommentStatus());
            mapping.setExternalPingStatus(post.getPingStatus());
            mapping.setUpdatedTime(LocalDateTime.now());
            if (mapping.getId() == null) {
                mappingService.save(mapping);
            } else {
                mappingService.updateById(mapping);
            }
            updateTags(client, content.getId(), post.getTags(), tagMap);
        }
        return posts.size();
    }

    private int importComments(WordPressClient client, int startPage, int perPage, boolean upsert) {
        List<WordPressCommentDto> comments = fetchAllPages(page -> client.getComments(page, perPage), startPage, perPage);
        if (CollectionUtils.isEmpty(comments)) {
            return 0;
        }
        for (WordPressCommentDto commentDto : comments) {
            ContentComment comment = commentService.getOne(new LambdaQueryWrapper<ContentComment>()
                    .eq(ContentComment::getExternalCommentId, commentDto.getId())
                    .orderByDesc(ContentComment::getId), false);
            if (comment == null && !upsert) {
                continue;
            }
            if (comment == null) {
                comment = new ContentComment();
                comment.setExternalCommentId(commentDto.getId());
                comment.setCreatedTime(parseDate(commentDto.getDate()));
            }
            comment.setContentId(resolveContentId(commentDto.getPost()));
            comment.setParentId(commentDto.getParent());
            comment.setUserId(null);
            comment.setAuthorName(commentDto.getAuthorName());
            comment.setAuthorEmail(commentDto.getAuthorEmail());
            comment.setAuthorUrl(commentDto.getAuthorUrl());
            comment.setContent(commentDto.getContent() != null ? commentDto.getContent().getRendered() : null);
            comment.setStatus(commentDto.getStatus());
            comment.setUpdatedTime(parseDate(commentDto.getDate()));
            if (comment.getId() == null) {
                commentService.save(comment);
            } else {
                commentService.updateById(comment);
            }
        }
        return comments.size();
    }

    private void updateTags(WordPressClient client, Long contentId, List<Long> wpTagIds, Map<Long, Long> tagMap) {
        tagRelationService.remove(new LambdaQueryWrapper<ContentTagRelation>()
                .eq(ContentTagRelation::getContentId, contentId));
        if (CollectionUtils.isEmpty(wpTagIds)) {
            return;
        }
        List<ContentTagRelation> relations = new ArrayList<>();
        for (Long wpTagId : wpTagIds) {
            ContentTag tag = resolveTagByExternalId(client, wpTagId, tagMap);
            if (tag != null && tag.getId() != null) {
                ContentTagRelation relation = new ContentTagRelation();
                relation.setContentId(contentId);
                relation.setTagId(tag.getId());
                relations.add(relation);
            }
        }
        if (!relations.isEmpty()) {
            tagRelationService.saveBatch(relations);
        }
    }

    private Long resolveCategoryId(WordPressClient client, List<Long> externalCategoryIds, Map<Long, Long> categoryMap) {
        if (CollectionUtils.isEmpty(externalCategoryIds)) {
            return null;
        }
        Long externalId = externalCategoryIds.get(0);
        if (externalId != null && categoryMap != null && categoryMap.containsKey(externalId)) {
            return categoryMap.get(externalId);
        }
        ContentCategory category = resolveCategoryByExternalId(client, externalId);
        return category != null ? category.getId() : null;
    }

    private ContentCategory resolveCategoryByExternalId(WordPressClient client, Long externalCategoryId) {
        if (externalCategoryId == null) {
            return null;
        }
        List<WordPressTermDto> terms = fetchAllPages(page -> client.getCategories(page, 100), 1, 100);
        for (WordPressTermDto term : terms) {
            if (externalCategoryId.equals(term.getId())) {
                return matchCategory(term);
            }
        }
        return null;
    }

    private ContentTag resolveTagByExternalId(WordPressClient client, Long externalTagId, Map<Long, Long> tagMap) {
        if (externalTagId == null) {
            return null;
        }
        if (tagMap != null && tagMap.containsKey(externalTagId)) {
            return tagService.getById(tagMap.get(externalTagId));
        }
        List<WordPressTermDto> terms = fetchAllPages(page -> client.getTags(page, 100), 1, 100);
        for (WordPressTermDto term : terms) {
            if (externalTagId.equals(term.getId())) {
                return matchTag(term);
            }
        }
        return null;
    }

    private ContentCategory matchCategory(WordPressTermDto term) {
        if (term == null) {
            return null;
        }
        ContentCategory category = categoryService.getOne(new LambdaQueryWrapper<ContentCategory>()
                .eq(ContentCategory::getName, term.getName())
                .orderByDesc(ContentCategory::getId), false);
        if (category == null && term.getSlug() != null) {
            category = categoryService.getOne(new LambdaQueryWrapper<ContentCategory>()
                    .eq(ContentCategory::getKeyword, term.getSlug())
                    .orderByDesc(ContentCategory::getId), false);
        }
        return category;
    }

    private ContentTag matchTag(WordPressTermDto term) {
        if (term == null) {
            return null;
        }
        ContentTag tag = tagService.getOne(new LambdaQueryWrapper<ContentTag>()
                .eq(ContentTag::getName, term.getName())
                .orderByDesc(ContentTag::getId), false);
        if (tag == null && term.getSlug() != null) {
            tag = tagService.getOne(new LambdaQueryWrapper<ContentTag>()
                    .eq(ContentTag::getSlug, term.getSlug())
                    .orderByDesc(ContentTag::getId), false);
        }
        return tag;
    }

    private Long resolveContentId(Long externalObjectId) {
        if (externalObjectId == null) {
            return null;
        }
        ContentExternalMapping mapping = mappingService.getOne(new LambdaQueryWrapper<ContentExternalMapping>()
                .eq(ContentExternalMapping::getExternalObjectId, externalObjectId)
                .eq(ContentExternalMapping::getExternalType, "post")
                .orderByDesc(ContentExternalMapping::getId), false);
        if (mapping != null) {
            return mapping.getContentId();
        }
        mapping = mappingService.getOne(new LambdaQueryWrapper<ContentExternalMapping>()
                .eq(ContentExternalMapping::getExternalObjectId, externalObjectId)
                .eq(ContentExternalMapping::getExternalType, "page")
                .orderByDesc(ContentExternalMapping::getId), false);
        return mapping != null ? mapping.getContentId() : null;
    }

    private WordPressClient resolveClient(WordPressSyncRequest request) {
        WordPressPropertiesRequest override = request.getWordpress();
        if (override == null || !StringUtils.hasText(override.getBaseUrl())) {
            return defaultClient;
        }
        WordPressProperties props = new WordPressProperties();
        props.setBaseUrl(override.getBaseUrl());
        if (StringUtils.hasText(override.getUsername())) {
            props.setUsername(override.getUsername());
        } else if (defaultProperties != null) {
            props.setUsername(defaultProperties.getUsername());
        }
        if (StringUtils.hasText(override.getAppPassword())) {
            props.setAppPassword(override.getAppPassword());
        } else if (defaultProperties != null) {
            props.setAppPassword(defaultProperties.getAppPassword());
        }
        props.setEnabled(true);
        return new WordPressClient(props);
    }

    private LocalDateTime parseDate(String dateStr) {
        if (dateStr == null) {
            return null;
        }
        try {
            return OffsetDateTime.parse(dateStr).toLocalDateTime();
        } catch (Exception e) {
            return null;
        }
    }

    private <T> List<T> fetchAllPages(IntFunction<List<T>> fetcher, int startPage, int perPage) {
        List<T> all = new ArrayList<>();
        int page = Math.max(1, startPage);
        int safeMaxPages = 1000;
        for (int i = 0; i < safeMaxPages; i++) {
            List<T> items = fetcher.apply(page);
            if (CollectionUtils.isEmpty(items)) {
                break;
            }
            all.addAll(items);
            if (items.size() < perPage) {
                break;
            }
            page++;
        }
        return all;
    }
}
