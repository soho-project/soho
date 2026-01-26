package work.soho.content.biz.wordpress;

import org.springframework.util.StringUtils;
import work.soho.content.biz.domain.ContentCategory;
import work.soho.content.biz.domain.ContentComment;
import work.soho.content.biz.domain.ContentInfo;
import work.soho.content.biz.domain.ContentMedia;
import work.soho.content.biz.domain.ContentTag;
import work.soho.content.biz.domain.ContentExternalMapping;
import work.soho.content.biz.wordpress.dto.WordPressCommentDto;
import work.soho.content.biz.wordpress.dto.WordPressMediaDto;
import work.soho.content.biz.wordpress.dto.WordPressPostDto;
import work.soho.content.biz.wordpress.dto.WordPressRendered;
import work.soho.content.biz.wordpress.dto.WordPressTermDto;
import work.soho.content.biz.wordpress.dto.WordPressUserDto;
import work.soho.user.api.dto.UserInfoDto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * WordPress 数据映射工具
 */
public class WordPressMapper {
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public WordPressPostDto toPost(ContentInfo content, ContentExternalMapping mapping,
                                  List<Long> categoryIds, List<Long> tagIds) {
        WordPressPostDto dto = new WordPressPostDto();
        dto.setId(content.getId());
        LocalDateTime created = content.getCreatedTime();
        LocalDateTime updated = content.getUpdatedTime();
        dto.setDate(formatDate(created, ZoneId.systemDefault()));
        dto.setDateGmt(formatDateGmt(created));
        dto.setModified(formatDate(updated, ZoneId.systemDefault()));
        dto.setModifiedGmt(formatDateGmt(updated));
        dto.setSlug(resolveSlug(content.getTitle(), mapping));
        dto.setStatus(resolveStatus(content.getStatus(), mapping));
        dto.setType(resolveType(mapping));
        dto.setLink("/content/" + content.getId());
        dto.setAuthor(content.getUserId());
        dto.setFeaturedMedia(mapping != null && mapping.getExternalFeaturedMediaId() != null
                ? mapping.getExternalFeaturedMediaId() : Long.valueOf(0));
        dto.setCommentStatus(mapping != null && StringUtils.hasText(mapping.getExternalCommentStatus())
                ? mapping.getExternalCommentStatus() : "open");
        dto.setPingStatus(mapping != null && StringUtils.hasText(mapping.getExternalPingStatus())
                ? mapping.getExternalPingStatus() : "open");
        dto.setSticky(content.getIsTop() != null && content.getIsTop() == 1);
        dto.setTemplate("");
        dto.setFormat("standard");
        dto.getTitle().setRendered(safeText(content.getTitle()));
        dto.getContent().setRendered(safeText(content.getBody()));
        dto.getExcerpt().setRendered(safeText(content.getDescription()));
        if (categoryIds != null) {
            dto.getCategories().addAll(categoryIds);
        }
        if (tagIds != null) {
            dto.getTags().addAll(tagIds);
        }
        return dto;
    }

    public WordPressTermDto toCategory(ContentCategory category) {
        WordPressTermDto dto = new WordPressTermDto();
        dto.setId(category.getId());
        dto.setCount(0);
        dto.setDescription(category.getDescription());
        dto.setLink("/content/category/" + category.getId());
        dto.setName(category.getName());
        dto.setSlug(slugify(category.getName()));
        dto.setTaxonomy("category");
        dto.setParent(category.getParentId());
        return dto;
    }

    public WordPressTermDto toTag(ContentTag tag) {
        WordPressTermDto dto = new WordPressTermDto();
        dto.setId(tag.getId());
        dto.setCount(tag.getCount() != null ? tag.getCount() : 0);
        dto.setDescription(tag.getDescription());
        dto.setLink("/content/tag/" + tag.getId());
        dto.setName(tag.getName());
        dto.setSlug(tag.getSlug());
        dto.setTaxonomy("post_tag");
        dto.setParent(0L);
        return dto;
    }

    public WordPressMediaDto toMedia(ContentMedia media) {
        WordPressMediaDto dto = new WordPressMediaDto();
        dto.setId(media.getId());
        dto.setDate(formatDate(media.getCreatedTime(), ZoneId.systemDefault()));
        dto.setDateGmt(formatDateGmt(media.getCreatedTime()));
        dto.setSlug(slugify(media.getTitle()));
        dto.setType("attachment");
        dto.setLink(media.getUrl());
        dto.getTitle().setRendered(safeText(media.getTitle()));
        dto.getCaption().setRendered(safeText(media.getDescription()));
        dto.setAltText(media.getAltText());
        dto.setMediaType(resolveMediaType(media.getMimeType()));
        dto.setMimeType(media.getMimeType());
        dto.setSourceUrl(media.getUrl());
        dto.setAuthor(media.getUserId());
        return dto;
    }

    public WordPressUserDto toUser(UserInfoDto user) {
        if (user == null) {
            return null;
        }
        WordPressUserDto dto = new WordPressUserDto();
        dto.setId(user.getId());
        dto.setName(user.getUsername());
        dto.setSlug(slugify(user.getUsername()));
        dto.setDescription("");
        dto.setUrl("");
        dto.setLink("/user/" + user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public WordPressCommentDto toComment(ContentComment comment) {
        WordPressCommentDto dto = new WordPressCommentDto();
        dto.setId(comment.getId());
        dto.setPost(comment.getContentId());
        dto.setParent(comment.getParentId());
        dto.setAuthor(comment.getUserId());
        dto.setAuthorName(comment.getAuthorName());
        dto.setAuthorEmail(comment.getAuthorEmail());
        dto.setAuthorUrl(comment.getAuthorUrl());
        dto.setDate(formatDate(comment.getCreatedTime(), ZoneId.systemDefault()));
        dto.setDateGmt(formatDateGmt(comment.getCreatedTime()));
        dto.setStatus(comment.getStatus());
        dto.getContent().setRendered(safeText(comment.getContent()));
        return dto;
    }

    private String resolveSlug(String title, ContentExternalMapping mapping) {
        if (mapping != null && StringUtils.hasText(mapping.getExternalSlug())) {
            return mapping.getExternalSlug();
        }
        return slugify(title);
    }

    private String resolveStatus(Integer status, ContentExternalMapping mapping) {
        if (mapping != null && StringUtils.hasText(mapping.getExternalStatus())) {
            return mapping.getExternalStatus();
        }
        if (status == null || status == 0) {
            return "draft";
        }
        return "publish";
    }

    private String resolveType(ContentExternalMapping mapping) {
        if (mapping != null && StringUtils.hasText(mapping.getExternalType())) {
            return mapping.getExternalType();
        }
        return "post";
    }

    private String formatDate(LocalDateTime time, ZoneId zoneId) {
        if (time == null) {
            return null;
        }
        return time.atZone(zoneId).format(ISO);
    }

    private String formatDateGmt(LocalDateTime time) {
        if (time == null) {
            return null;
        }
        return time.atZone(ZoneOffset.UTC).format(ISO);
    }

    private String safeText(String value) {
        return value == null ? "" : value;
    }

    private String resolveMediaType(String mimeType) {
        if (!StringUtils.hasText(mimeType)) {
            return "file";
        }
        if (mimeType.startsWith("image/")) {
            return "image";
        }
        if (mimeType.startsWith("video/")) {
            return "video";
        }
        if (mimeType.startsWith("audio/")) {
            return "audio";
        }
        return "file";
    }

    private String slugify(String text) {
        if (!StringUtils.hasText(text)) {
            return "";
        }
        String slug = text.trim().toLowerCase();
        slug = slug.replaceAll("[^a-z0-9\\s-]", "");
        slug = slug.replaceAll("\\s+", "-");
        slug = slug.replaceAll("-+", "-");
        return slug;
    }
}
