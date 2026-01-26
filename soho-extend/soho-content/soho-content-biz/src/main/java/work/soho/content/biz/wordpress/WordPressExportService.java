package work.soho.content.biz.wordpress;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
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
import work.soho.content.biz.wordpress.dto.WordPressExportResult;
import work.soho.content.biz.wordpress.dto.WordPressPostDto;
import work.soho.user.api.dto.UserInfoDto;
import work.soho.user.api.service.UserApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * WordPress 导出服务
 */
@Service
public class WordPressExportService {
    private final AdminContentService contentService;
    private final AdminContentCategoryService categoryService;
    private final ContentTagService tagService;
    private final ContentTagRelationService tagRelationService;
    private final ContentMediaService mediaService;
    private final ContentCommentService commentService;
    private final ContentExternalMappingService mappingService;
    private final UserApiService userApiService;
    private final WordPressMapper mapper = new WordPressMapper();

    public WordPressExportService(AdminContentService contentService,
                                 AdminContentCategoryService categoryService,
                                 ContentTagService tagService,
                                 ContentTagRelationService tagRelationService,
                                 ContentMediaService mediaService,
                                 ContentCommentService commentService,
                                 ContentExternalMappingService mappingService,
                                 UserApiService userApiService) {
        this.contentService = contentService;
        this.categoryService = categoryService;
        this.tagService = tagService;
        this.tagRelationService = tagRelationService;
        this.mediaService = mediaService;
        this.commentService = commentService;
        this.mappingService = mappingService;
        this.userApiService = userApiService;
    }

    public WordPressExportResult exportAll() {
        WordPressExportResult result = new WordPressExportResult();
        List<ContentInfo> contents = contentService.list();
        Map<Long, ContentExternalMapping> mappingMap = loadMappings(contents);
        Map<Long, List<Long>> tagMap = loadTagRelations(contents);

        for (ContentInfo content : contents) {
            ContentExternalMapping mapping = mappingMap.get(content.getId());
            List<Long> categories = new ArrayList<>();
            if (content.getCategoryId() != null) {
                categories.add(content.getCategoryId());
            }
            WordPressPostDto dto = mapper.toPost(content, mapping, categories, tagMap.get(content.getId()));
            if (mapping != null && "page".equalsIgnoreCase(mapping.getExternalType())) {
                result.getPages().add(dto);
            } else {
                result.getPosts().add(dto);
            }
        }

        for (ContentCategory category : categoryService.list()) {
            result.getCategories().add(mapper.toCategory(category));
        }
        for (ContentTag tag : tagService.list()) {
            result.getTags().add(mapper.toTag(tag));
        }
        for (ContentMedia media : mediaService.list()) {
            result.getMedia().add(mapper.toMedia(media));
        }
        for (Long userId : collectUserIds()) {
            UserInfoDto user = userApiService.getUserById(userId);
            if (user != null) {
                result.getUsers().add(mapper.toUser(user));
            }
        }
        for (ContentComment comment : commentService.list()) {
            result.getComments().add(mapper.toComment(comment));
        }
        return result;
    }

    private Map<Long, ContentExternalMapping> loadMappings(List<ContentInfo> contents) {
        if (contents.isEmpty()) {
            return new HashMap<>();
        }
        List<Long> ids = contents.stream().map(ContentInfo::getId).collect(Collectors.toList());
        List<ContentExternalMapping> mappings = mappingService.list(new LambdaQueryWrapper<ContentExternalMapping>()
                .in(ContentExternalMapping::getContentId, ids));
        return mappings.stream().collect(Collectors.toMap(ContentExternalMapping::getContentId, m -> m, (a, b) -> a));
    }

    private Map<Long, List<Long>> loadTagRelations(List<ContentInfo> contents) {
        if (contents.isEmpty()) {
            return new HashMap<>();
        }
        List<Long> ids = contents.stream().map(ContentInfo::getId).collect(Collectors.toList());
        List<ContentTagRelation> relations = tagRelationService.list(new LambdaQueryWrapper<ContentTagRelation>()
                .in(ContentTagRelation::getContentId, ids));
        return relations.stream().collect(Collectors.groupingBy(ContentTagRelation::getContentId,
                Collectors.mapping(ContentTagRelation::getTagId, Collectors.toList())));
    }

    private List<Long> collectUserIds() {
        Map<Long, Boolean> ids = new HashMap<>();
        for (ContentInfo content : contentService.list()) {
            if (content.getUserId() != null) {
                ids.put(content.getUserId(), Boolean.TRUE);
            }
        }
        for (ContentMedia media : mediaService.list()) {
            if (media.getUserId() != null) {
                ids.put(media.getUserId(), Boolean.TRUE);
            }
        }
        for (ContentComment comment : commentService.list()) {
            if (comment.getUserId() != null) {
                ids.put(comment.getUserId(), Boolean.TRUE);
            }
        }
        return new ArrayList<>(ids.keySet());
    }
}
