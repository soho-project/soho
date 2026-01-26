package work.soho.content;

import junit.framework.TestCase;
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
import work.soho.content.biz.wordpress.WordPressExportService;
import work.soho.content.biz.wordpress.dto.WordPressExportResult;
import work.soho.user.api.dto.UserInfoDto;
import work.soho.user.api.service.UserApiService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordPressExportServiceTest extends TestCase {

    public void testExportAll() {
        InMemoryStore<ContentInfo> contents = new InMemoryStore<>();
        InMemoryStore<ContentCategory> categories = new InMemoryStore<>();
        InMemoryStore<ContentTag> tags = new InMemoryStore<>();
        InMemoryStore<ContentTagRelation> relations = new InMemoryStore<>();
        InMemoryStore<ContentMedia> media = new InMemoryStore<>();
        InMemoryStore<ContentComment> comments = new InMemoryStore<>();
        InMemoryStore<ContentExternalMapping> mappings = new InMemoryStore<>();

        ContentCategory category = new ContentCategory();
        category.setId(10L);
        category.setName("分类A");
        categories.list().add(category);

        ContentTag tag = new ContentTag();
        tag.setId(20L);
        tag.setName("标签A");
        tags.list().add(tag);

        ContentInfo content = new ContentInfo();
        content.setId(100L);
        content.setTitle("标题A");
        content.setDescription("摘要");
        content.setBody("正文");
        content.setCategoryId(10L);
        content.setUserId(200L);
        contents.list().add(content);

        ContentTagRelation relation = new ContentTagRelation();
        relation.setId(1L);
        relation.setContentId(100L);
        relation.setTagId(20L);
        relations.list().add(relation);

        ContentExternalMapping mapping = new ContentExternalMapping();
        mapping.setId(1L);
        mapping.setContentId(100L);
        mapping.setExternalType("page");
        mappings.list().add(mapping);

        ContentMedia mediaItem = new ContentMedia();
        mediaItem.setId(300L);
        mediaItem.setUrl("https://example.com/a.png");
        media.list().add(mediaItem);

        ContentComment comment = new ContentComment();
        comment.setId(400L);
        comment.setContentId(100L);
        comment.setContent("评论");
        comments.list().add(comment);

        AdminContentService contentService = proxy(AdminContentService.class, contents);
        AdminContentCategoryService categoryService = proxy(AdminContentCategoryService.class, categories);
        ContentTagService tagService = proxy(ContentTagService.class, tags);
        ContentTagRelationService tagRelationService = proxy(ContentTagRelationService.class, relations);
        ContentMediaService mediaService = proxy(ContentMediaService.class, media);
        ContentCommentService commentService = proxy(ContentCommentService.class, comments);
        ContentExternalMappingService mappingService = proxy(ContentExternalMappingService.class, mappings);

        UserApiService userApiService = userProxy();

        WordPressExportService exportService = new WordPressExportService(
                contentService, categoryService, tagService, tagRelationService,
                mediaService, commentService, mappingService, userApiService
        );

        WordPressExportResult result = exportService.exportAll();
        assertEquals(1, result.getPages().size());
        assertEquals(0, result.getPosts().size());
        assertEquals(1, result.getCategories().size());
        assertEquals(1, result.getTags().size());
        assertEquals(1, result.getMedia().size());
        assertEquals(1, result.getComments().size());
        assertEquals(1, result.getUsers().size());
    }

    private static class InMemoryStore<T> {
        private final List<T> data = new ArrayList<>();

        List<T> list() {
            return data;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> iface, InMemoryStore<?> store) {
        InvocationHandler handler = new StoreHandler(store);
        return (T) Proxy.newProxyInstance(iface.getClassLoader(), new Class[]{iface}, handler);
    }

    private static UserApiService userProxy() {
        Map<Long, UserInfoDto> users = new HashMap<>();
        UserInfoDto user = new UserInfoDto();
        user.setId(200L);
        user.setUsername("userA");
        user.setEmail("a@example.com");
        users.put(200L, user);

        return (UserApiService) Proxy.newProxyInstance(
                UserApiService.class.getClassLoader(),
                new Class[]{UserApiService.class},
                (proxy, method, args) -> {
                    if ("getUserById".equals(method.getName())) {
                        return users.get(args[0]);
                    }
                    return null;
                }
        );
    }

    private static class StoreHandler implements InvocationHandler {
        private final InMemoryStore<?> store;

        StoreHandler(InMemoryStore<?> store) {
            this.store = store;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            if ("list".equals(name)) {
                return new ArrayList<>(store.list());
            }
            return null;
        }
    }
}
