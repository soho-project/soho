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
import work.soho.content.biz.wordpress.WordPressClient;
import work.soho.content.biz.wordpress.WordPressProperties;
import work.soho.content.biz.wordpress.WordPressSyncService;
import work.soho.content.biz.wordpress.dto.WordPressCommentDto;
import work.soho.content.biz.wordpress.dto.WordPressMediaDto;
import work.soho.content.biz.wordpress.dto.WordPressPostDto;
import work.soho.content.biz.wordpress.dto.WordPressRendered;
import work.soho.content.biz.wordpress.dto.WordPressSyncRequest;
import work.soho.content.biz.wordpress.dto.WordPressSyncResult;
import work.soho.content.biz.wordpress.dto.WordPressTermDto;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class WordPressSyncServiceTest extends TestCase {

    public void testImportFromWordPress() {
        InMemoryStore<ContentInfo> contents = new InMemoryStore<>();
        InMemoryStore<ContentCategory> categories = new InMemoryStore<>();
        InMemoryStore<ContentTag> tags = new InMemoryStore<>();
        InMemoryStore<ContentTagRelation> relations = new InMemoryStore<>();
        InMemoryStore<ContentMedia> media = new InMemoryStore<>();
        InMemoryStore<ContentComment> comments = new InMemoryStore<>();
        InMemoryStore<ContentExternalMapping> mappings = new InMemoryStore<>();

        AdminContentService contentService = proxy(AdminContentService.class, contents);
        AdminContentCategoryService categoryService = proxy(AdminContentCategoryService.class, categories);
        ContentTagService tagService = proxy(ContentTagService.class, tags);
        ContentTagRelationService tagRelationService = proxy(ContentTagRelationService.class, relations);
        ContentMediaService mediaService = proxy(ContentMediaService.class, media);
        ContentCommentService commentService = proxy(ContentCommentService.class, comments);
        ContentExternalMappingService mappingService = proxy(ContentExternalMappingService.class, mappings);

        FakeWordPressClient client = new FakeWordPressClient();
        WordPressSyncService syncService = new WordPressSyncService(
                client, contentService, categoryService, tagService,
                tagRelationService, mediaService, commentService, mappingService
        );

        WordPressSyncRequest request = new WordPressSyncRequest();
        request.setPage(1);
        request.setPerPage(10);
        WordPressSyncResult result = syncService.importFromWordPress(request);

        assertEquals(1, result.getCategoriesImported());
        assertEquals(1, result.getTagsImported());
        assertEquals(1, result.getMediaImported());
        assertEquals(1, result.getPostsImported());
        assertEquals(1, result.getCommentsImported());

        assertEquals(1, categories.list().size());
        assertEquals(1, tags.list().size());
        assertEquals(1, media.list().size());
        assertEquals(1, contents.list().size());
        assertEquals(1, comments.list().size());
        assertEquals(1, mappings.list().size());
    }

    private static class FakeWordPressClient extends WordPressClient {
        FakeWordPressClient() {
            super(new WordPressProperties());
        }

        @Override
        public List<WordPressTermDto> getCategories(int page, int perPage) {
            WordPressTermDto dto = new WordPressTermDto();
            dto.setId(10L);
            dto.setName("分类A");
            dto.setSlug("cat-a");
            dto.setDescription("desc");
            dto.setParent(0L);
            List<WordPressTermDto> list = new ArrayList<>();
            list.add(dto);
            return list;
        }

        @Override
        public List<WordPressTermDto> getTags(int page, int perPage) {
            WordPressTermDto dto = new WordPressTermDto();
            dto.setId(20L);
            dto.setName("标签A");
            dto.setSlug("tag-a");
            List<WordPressTermDto> list = new ArrayList<>();
            list.add(dto);
            return list;
        }

        @Override
        public List<WordPressPostDto> getPosts(String type, int page, int perPage) {
            WordPressPostDto dto = new WordPressPostDto();
            dto.setId(100L);
            dto.setStatus("publish");
            dto.setSlug("post-a");
            WordPressRendered title = new WordPressRendered();
            title.setRendered("标题A");
            dto.setTitle(title);
            WordPressRendered content = new WordPressRendered();
            content.setRendered("正文");
            dto.setContent(content);
            WordPressRendered excerpt = new WordPressRendered();
            excerpt.setRendered("摘要");
            dto.setExcerpt(excerpt);
            dto.getCategories().add(10L);
            dto.getTags().add(20L);
            List<WordPressPostDto> list = new ArrayList<>();
            list.add(dto);
            return list;
        }

        @Override
        public List<WordPressMediaDto> getMedia(int page, int perPage) {
            WordPressMediaDto dto = new WordPressMediaDto();
            dto.setId(300L);
            dto.setSourceUrl("https://example.com/a.png");
            List<WordPressMediaDto> list = new ArrayList<>();
            list.add(dto);
            return list;
        }

        @Override
        public List<WordPressCommentDto> getComments(int page, int perPage) {
            WordPressCommentDto dto = new WordPressCommentDto();
            dto.setId(400L);
            dto.setPost(100L);
            WordPressRendered content = new WordPressRendered();
            content.setRendered("评论");
            dto.setContent(content);
            List<WordPressCommentDto> list = new ArrayList<>();
            list.add(dto);
            return list;
        }
    }

    private static class InMemoryStore<T> {
        private long seq = 1;
        private final List<Object> data = new ArrayList<>();

        List<Object> list() {
            return data;
        }

        Long nextId() {
            return seq++;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> iface, InMemoryStore<?> store) {
        InvocationHandler handler = new StoreHandler(store);
        return (T) Proxy.newProxyInstance(iface.getClassLoader(), new Class[]{iface}, handler);
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
            if ("getById".equals(name)) {
                Long id = (Long) args[0];
                for (Object item : store.list()) {
                    Long itemId = (Long) item.getClass().getMethod("getId").invoke(item);
                    if (id != null && id.equals(itemId)) {
                        return item;
                    }
                }
                return null;
            }
            if ("getOne".equals(name)) {
                return store.list().isEmpty() ? null : store.list().get(0);
            }
            if ("save".equals(name)) {
                Object entity = args[0];
                Long id = (Long) entity.getClass().getMethod("getId").invoke(entity);
                if (id == null) {
                    Long nextId = store.nextId();
                    entity.getClass().getMethod("setId", Long.class).invoke(entity, nextId);
                }
                store.list().add(entity);
                return Boolean.TRUE;
            }
            if ("updateById".equals(name)) {
                Object entity = args[0];
                Long id = (Long) entity.getClass().getMethod("getId").invoke(entity);
                if (id != null) {
                    for (int i = 0; i < store.list().size(); i++) {
                        Object item = store.list().get(i);
                        Long itemId = (Long) item.getClass().getMethod("getId").invoke(item);
                        if (id.equals(itemId)) {
                            store.list().set(i, entity);
                            return Boolean.TRUE;
                        }
                    }
                }
                return Boolean.FALSE;
            }
            if ("remove".equals(name)) {
                store.list().clear();
                return Boolean.TRUE;
            }
            if ("saveBatch".equals(name)) {
                List<?> items = (List<?>) args[0];
                for (Object entity : items) {
                    Long id = (Long) entity.getClass().getMethod("getId").invoke(entity);
                    if (id == null) {
                        Long nextId = store.nextId();
                        entity.getClass().getMethod("setId", Long.class).invoke(entity, nextId);
                    }
                    store.list().add(entity);
                }
                return Boolean.TRUE;
            }
            return null;
        }
    }
}
