package work.soho.open.biz.controller.guest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.admin.api.vo.TreeNodeVo;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.open.biz.domain.OpenDoc;
import work.soho.open.biz.service.OpenDocService;

import java.util.*;
import java.util.stream.Collectors;

;
/**
 * 开放平台文档Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/guest/openDoc" )
public class GuestOpenDocController {

    private final OpenDocService openDocService;

    /**
     * 查询开放平台文档列表
     */
    @GetMapping("/list")
    @Node(value = "guest::openDoc::list", name = "获取 开放平台文档 列表")
    public R<PageSerializable<OpenDoc>> list(OpenDoc openDoc, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<OpenDoc> lqw = new LambdaQueryWrapper<OpenDoc>();
        lqw.like(StringUtils.isNotBlank(openDoc.getCategoryKey()),OpenDoc::getCategoryKey ,openDoc.getCategoryKey());
        lqw.like(StringUtils.isNotBlank(openDoc.getTitle()),OpenDoc::getTitle ,openDoc.getTitle());
        lqw.like(StringUtils.isNotBlank(openDoc.getContent()),OpenDoc::getContent ,openDoc.getContent());
        lqw.eq(openDoc.getContentFormat() != null, OpenDoc::getContentFormat ,openDoc.getContentFormat());
        lqw.eq(openDoc.getSortNo() != null, OpenDoc::getSortNo ,openDoc.getSortNo());
        lqw.eq(openDoc.getStatus() != null, OpenDoc::getStatus ,openDoc.getStatus());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, OpenDoc::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, OpenDoc::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.eq(openDoc.getUpdatedTime() != null, OpenDoc::getUpdatedTime ,openDoc.getUpdatedTime());
        lqw.eq(openDoc.getId() != null, OpenDoc::getId ,openDoc.getId());
        lqw.eq(openDoc.getParentId() != null, OpenDoc::getParentId ,openDoc.getParentId());
        lqw.like(StringUtils.isNotBlank(openDoc.getCategoryType()),OpenDoc::getCategoryType ,openDoc.getCategoryType());
        List<OpenDoc> list = openDocService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取开放平台文档详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "guest::openDoc::getInfo", name = "获取 开放平台文档 详细信息")
    public R<OpenDoc> getInfo(@PathVariable("id" ) Long id) {
        return R.success(openDocService.getById(id));
    }

    /**
     * 新增开放平台文档
     */
    @PostMapping
    @Node(value = "guest::openDoc::add", name = "新增 开放平台文档")
    public R<Boolean> add(@RequestBody OpenDoc openDoc) {
        return R.success(openDocService.save(openDoc));
    }

    /**
     * 修改开放平台文档
     */
    @PutMapping
    @Node(value = "guest::openDoc::edit", name = "修改 开放平台文档")
    public R<Boolean> edit(@RequestBody OpenDoc openDoc) {
        return R.success(openDocService.updateById(openDoc));
    }

    /**
     * 删除开放平台文档
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "guest::openDoc::remove", name = "删除 开放平台文档")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(openDocService.removeByIds(Arrays.asList(ids)));
    }

    @GetMapping("tree")
    public R<List<TreeNodeVo>> tree() {
        List<OpenDoc> list = openDocService.list();
        List<TreeNodeVo<Long, Long, Long, String>> listVo = list.stream().map(item->{
            return new TreeNodeVo<>(item.getId(), item.getId(), item.getParentId(), item.getTitle());
        }).collect(Collectors.toList());

        Map<Long, List<TreeNodeVo>> mapVo = new HashMap<>();
        listVo.stream().forEach(item -> {
            if(mapVo.get(item.getParentId()) == null) {
                mapVo.put(item.getParentId(), new ArrayList<>());
            }
            mapVo.get(item.getParentId()).add(item);
        });

        listVo.forEach(item -> {
            if(mapVo.containsKey(item.getKey())) {
                item.setChildren(mapVo.get(item.getKey()));
            }
        });
        System.out.println(mapVo);
        return R.success(mapVo.get(0L));
    }
}