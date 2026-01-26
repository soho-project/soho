package work.soho.content.biz.wordpress;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
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
import work.soho.content.biz.wordpress.dto.WordPressSyncRequest;
import work.soho.content.biz.wordpress.dto.WordPressSyncResult;
import work.soho.content.biz.wordpress.dto.WordPressTermDto;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WordPress 导入服务
 */
@Service
public class WordPressSyncService {
    private final WordPressClient client;
    private final AdminContentService contentService;
    private final AdminContentCategoryService categoryService;
    private final ContentTagService tagService;
    private final ContentTagRelationService tagRelationService;
    private final ContentMediaService mediaService;
    private final ContentCommentService commentService;
    private final ContentExternalMappingService mappingService;

    public WordPressSyncService(WordPressClient client,
                               AdminContentService contentService,
                               AdminContentCategoryService categoryService,
                               ContentTagService tagService,
                               ContentTagRelationService tagRelationService,
                               ContentMediaService mediaService,
                               ContentCommentService commentService,
                               ContentExternalMappingService mappingService) {
        this.client = client;
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
        if (request.isCategories()) {
            result.setCategoriesImported(importCategories(request.getPage(), request.getPerPage()));
        }
        if (request.isTags()) {
            result.setTagsImported(importTags(request.getPage(), request.getPerPage()));
        }
        if (request.isUsers()) {
            result.setUsersImported(0);
        }
        if (request.isMedia()) {
            result.setMediaImported(importMedia(request.getPage(), request.getPerPage()));
        }
        if (request.isPosts()) {
            result.setPostsImported(importPosts("post", request.getPage(), request.getPerPage(), request.isUpsert()));
        }
        if (request.isPages()) {
            result.setPagesImported(importPosts("page", request.getPage(), request.getPerPage(), request.isUpsert()));
        }
        if (request.isComments()) {
            result.setCommentsImported(importComments(request.getPage(), request.getPerPage(), request.isUpsert()));
        }
        return result;
    }

    private int importCategories(int page, int perPage) {
        List<WordPressTermDto> categories = client.getCategories(page, perPage);
        if (CollectionUtils.isEmpty(categories)) {
            return 0;
        }
        Map<Long, Long> wpToLocal = new HashMap<>();
        for (WordPressTermDto term : categories) {
            ContentCategory category = upsertCategory(term, 0L);
            wpToLocal.put(term.getId(), category.getId());
        }
        for (WordPressTermDto term : categories) {
            if (term.getParent() != null && term.getParent() > 0) {
                Long localId = wpToLocal.get(term.getId());
                Long parentId = wpToLocal.get(term.getParent());
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

    private int importTags(int page, int perPage) {
        List<WordPressTermDto> tags = client.getTags(page, perPage);
        if (CollectionUtils.isEmpty(tags)) {
            return 0;
        }
        for (WordPressTermDto term : tags) {
            upsertTag(term);
        }
        return tags.size();
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

    private int importMedia(int page, int perPage) {
        List<WordPressMediaDto> mediaList = client.getMedia(page, perPage);
        if (CollectionUtils.isEmpty(mediaList)) {
            return 0;
        }
        for (WordPressMediaDto mediaDto : mediaList) {
            ContentMedia media = mediaService.getOne(new LambdaQueryWrapper<ContentMedia>()
                    .eq(ContentMedia::getExternalMediaId, mediaDto.getId()));
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

    private int importPosts(String type, int page, int perPage, boolean upsert) {
        List<WordPressPostDto> posts = client.getPosts(type, page, perPage);
        if (CollectionUtils.isEmpty(posts)) {
            return 0;
        }
        for (WordPressPostDto post : posts) {
            ContentExternalMapping mapping = mappingService.getOne(new LambdaQueryWrapper<ContentExternalMapping>()
                    .eq(ContentExternalMapping::getExternalObjectId, post.getId())
                    .eq(ContentExternalMapping::getExternalType, type));
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
            content.setCategoryId(resolveCategoryId(post.getCategories()));
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
            updateTags(content.getId(), post.getTags());
        }
        return posts.size();
    }

    private int importComments(int page, int perPage, boolean upsert) {
        List<WordPressCommentDto> comments = client.getComments(page, perPage);
        if (CollectionUtils.isEmpty(comments)) {
            return 0;
        }
        for (WordPressCommentDto commentDto : comments) {
            ContentComment comment = commentService.getOne(new LambdaQueryWrapper<ContentComment>()
                    .eq(ContentComment::getExternalCommentId, commentDto.getId()));
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

    private void updateTags(Long contentId, List<Long> wpTagIds) {
        tagRelationService.remove(new LambdaQueryWrapper<ContentTagRelation>()
                .eq(ContentTagRelation::getContentId, contentId));
        if (CollectionUtils.isEmpty(wpTagIds)) {
            return;
        }
        List<ContentTagRelation> relations = new ArrayList<>();
        for (Long wpTagId : wpTagIds) {
            ContentTag tag = resolveTagByExternalId(wpTagId);
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

    private Long resolveCategoryId(List<Long> externalCategoryIds) {
        if (CollectionUtils.isEmpty(externalCategoryIds)) {
            return null;
        }
        ContentCategory category = resolveCategoryByExternalId(externalCategoryIds.get(0));
        return category != null ? category.getId() : null;
    }

    private ContentCategory resolveCategoryByExternalId(Long externalCategoryId) {
        if (externalCategoryId == null) {
            return null;
        }
        for (WordPressTermDto term : client.getCategories(1, 100)) {
            if (externalCategoryId.equals(term.getId())) {
                return matchCategory(term);
            }
        }
        return null;
    }

    private ContentTag resolveTagByExternalId(Long externalTagId) {
        if (externalTagId == null) {
            return null;
        }
        for (WordPressTermDto term : client.getTags(1, 100)) {
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
                .eq(ContentCategory::getName, term.getName()));
        if (category == null && term.getSlug() != null) {
            category = categoryService.getOne(new LambdaQueryWrapper<ContentCategory>()
                    .eq(ContentCategory::getKeyword, term.getSlug()));
        }
        return category;
    }

    private ContentTag matchTag(WordPressTermDto term) {
        if (term == null) {
            return null;
        }
        ContentTag tag = tagService.getOne(new LambdaQueryWrapper<ContentTag>()
                .eq(ContentTag::getName, term.getName()));
        if (tag == null && term.getSlug() != null) {
            tag = tagService.getOne(new LambdaQueryWrapper<ContentTag>()
                    .eq(ContentTag::getSlug, term.getSlug()));
        }
        return tag;
    }

    private Long resolveContentId(Long externalObjectId) {
        if (externalObjectId == null) {
            return null;
        }
        ContentExternalMapping mapping = mappingService.getOne(new LambdaQueryWrapper<ContentExternalMapping>()
                .eq(ContentExternalMapping::getExternalObjectId, externalObjectId)
                .eq(ContentExternalMapping::getExternalType, "post"));
        if (mapping != null) {
            return mapping.getContentId();
        }
        mapping = mappingService.getOne(new LambdaQueryWrapper<ContentExternalMapping>()
                .eq(ContentExternalMapping::getExternalObjectId, externalObjectId)
                .eq(ContentExternalMapping::getExternalType, "page"));
        return mapping != null ? mapping.getContentId() : null;
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
}
