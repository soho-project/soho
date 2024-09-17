package work.soho.lot.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.common.security.utils.SecurityUtils;
import work.soho.common.security.annotation.Node;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.lot.domain.LotModelItem;
import work.soho.lot.domain.LotProduct;
import work.soho.lot.domain.LotProductValue;
import work.soho.lot.service.LotModelItemService;
import work.soho.lot.service.LotModelService;
import work.soho.lot.service.LotProductService;
import work.soho.lot.service.LotProductValueService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 产品Controller
 *
 * @author i
 * @date 2022-10-24 23:14:17
 */
@Api(tags = "物联网产品API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/lotProduct" )
public class LotProductController {

    private final LotProductService lotProductService;
    private final LotProductValueService lotProductValueService;
    private final LotModelItemService lotModelItemService;
    private final LotModelService lotModelService;

    /**
     * 查询产品列表
     */
    @GetMapping("/list")
    @Node(value = "lotProduct::list", name = "产品列表")
    public R<PageSerializable<LotProduct>> list(LotProduct lotProduct)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<LotProduct> lqw = new LambdaQueryWrapper<LotProduct>();

        if (lotProduct.getId() != null){
            lqw.eq(LotProduct::getId ,lotProduct.getId());
        }
        if (lotProduct.getModelId() != null){
            lqw.eq(LotProduct::getModelId ,lotProduct.getModelId());
        }
        if (StringUtils.isNotBlank(lotProduct.getMac())){
            lqw.like(LotProduct::getMac ,lotProduct.getMac());
        }
        if (lotProduct.getUserId() != null){
            lqw.eq(LotProduct::getUserId ,lotProduct.getUserId());
        }
        if (lotProduct.getUpdateTime() != null){
            lqw.eq(LotProduct::getUpdateTime ,lotProduct.getUpdateTime());
        }
        if (lotProduct.getCreatedTime() != null){
            lqw.eq(LotProduct::getCreatedTime ,lotProduct.getCreatedTime());
        }
        if (lotProduct.getStatus() != null){
            lqw.eq(LotProduct::getStatus ,lotProduct.getStatus());
        }
        List<LotProduct> list = lotProductService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取产品详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "lotProduct::getInfo", name = "产品详细信息")
    public R<LotProduct> getInfo(@PathVariable("id" ) Long id) {
        return R.success(lotProductService.getById(id));
    }

    /**
     * 新增产品
     */
    @PostMapping
    @Node(value = "lotProduct::add", name = "产品新增")
    public R<Boolean> add(@RequestBody LotProduct lotProduct) {
        lotProduct.setUserId(SecurityUtils.getLoginUserId());
        lotProduct.setCreatedTime(LocalDateTime.now());
        lotProduct.setUpdateTime(LocalDateTime.now());
        lotProductService.save(lotProduct);
        //复制模型信息
        List<LotModelItem> itemList = lotModelItemService.list((new LambdaQueryWrapper<LotModelItem>()).eq(LotModelItem::getModelId, lotProduct.getModelId()));
        int order = 1;
        for(LotModelItem item: itemList) {
            try {
                LotProductValue tmpItem = BeanUtils.copy(item, LotProductValue.class);
                tmpItem.setId(null);
                tmpItem.setProductId(lotProduct.getId());
                tmpItem.setUpdatedTime(LocalDateTime.now());
                tmpItem.setCreatedTime(LocalDateTime.now());
                tmpItem.setProductId(lotProduct.getId());
                tmpItem.setOrder(order);
                System.out.println(tmpItem);
                lotProductValueService.save(tmpItem);
                order++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return R.success();
    }

    /**
     * 修改产品
     */
    @PutMapping
    @Node(value = "lotProduct::edit", name = "产品修改")
    public R<Boolean> edit(@RequestBody LotProduct lotProduct) {
        List<LotModelItem> itemList = lotModelItemService.list((new LambdaQueryWrapper<LotModelItem>()).eq(LotModelItem::getModelId, lotProduct.getModelId()));
        int order = 1;
        for(LotModelItem item: itemList) {
            try {
                //检查是否存在， 如果不存在则新增
                LotProductValue tmpItem = lotProductValueService.getOne((new LambdaQueryWrapper<LotProductValue>())
                        .eq(LotProductValue::getProductId, lotProduct.getId())
                        .eq(LotProductValue::getParamsName, item.getParamsName()));
                if(tmpItem == null) {
                    tmpItem = BeanUtils.copy(item, LotProductValue.class);
                    tmpItem.setId(null);
                    tmpItem.setProductId(lotProduct.getId());
                    tmpItem.setUpdatedTime(LocalDateTime.now());
                    tmpItem.setCreatedTime(LocalDateTime.now());
                    tmpItem.setProductId(lotProduct.getId());
                    tmpItem.setOrder(order);
                    System.out.println(tmpItem);
                    lotProductValueService.save(tmpItem);
                }
                order++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //获取所有params name
        List<String> paramsNames = itemList.stream().map(item->item.getParamsName()).collect(Collectors.toList());
        lotProductValueService.remove((new LambdaQueryWrapper<LotProductValue>()).notIn(LotProductValue::getParamsName, paramsNames));

        lotProduct.setUpdateTime(LocalDateTime.now());
        return R.success(lotProductService.updateById(lotProduct));
    }

    /**
     * 删除产品
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "lotProduct::remove", name = "产品删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        lotProductValueService.remove((new LambdaQueryWrapper<LotProductValue>()).in(LotProductValue::getProductId, ids));
        return R.success(lotProductService.removeByIds(Arrays.asList(ids)));
    }
}
