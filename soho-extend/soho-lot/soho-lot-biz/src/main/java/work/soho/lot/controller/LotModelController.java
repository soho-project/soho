package work.soho.lot.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.api.admin.annotation.Node;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.lot.api.request.LotModelRequest;
import work.soho.lot.domain.LotModel;
import work.soho.lot.domain.LotModelItem;
import work.soho.lot.domain.LotProduct;
import work.soho.lot.service.LotModelItemService;
import work.soho.lot.service.LotModelService;
import work.soho.lot.service.LotProductService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 物联网模型Controller
 *
 * @author i
 * @date 2022-10-24 23:14:17
 */
@Api(tags = "物联网模型API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/lotModel" )
public class LotModelController {

    private final LotModelService lotModelService;
    private final LotProductService lotProductService;
    private final LotModelItemService lotModelItemService;

    /**
     * 查询物联网模型列表
     */
    @GetMapping("/list")
    @Node(value = "lotModel::list", name = "物联网模型列表")
    public R<PageSerializable<LotModel>> list(LotModel lotModel)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<LotModel> lqw = new LambdaQueryWrapper<LotModel>();

        if (lotModel.getId() != null){
            lqw.eq(LotModel::getId ,lotModel.getId());
        }
        if (StringUtils.isNotBlank(lotModel.getName())){
            lqw.like(LotModel::getName ,lotModel.getName());
        }
        if (lotModel.getSupplierId() != null){
            lqw.eq(LotModel::getSupplierId ,lotModel.getSupplierId());
        }
        if (lotModel.getUpdatedTime() != null){
            lqw.eq(LotModel::getUpdatedTime ,lotModel.getUpdatedTime());
        }
        if (lotModel.getCreatedTime() != null){
            lqw.eq(LotModel::getCreatedTime ,lotModel.getCreatedTime());
        }
        List<LotModel> list = lotModelService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    @GetMapping("/options")
    @Node(value = "lotModel::options", name = "物联网模型列表")
    public R<PageSerializable<LotModel>> options(LotModel lotModel)
    {
        LambdaQueryWrapper<LotModel> lqw = new LambdaQueryWrapper<LotModel>();

        if (lotModel.getId() != null){
            lqw.eq(LotModel::getId ,lotModel.getId());
        }
        if (StringUtils.isNotBlank(lotModel.getName())){
            lqw.like(LotModel::getName ,lotModel.getName());
        }
        if (lotModel.getSupplierId() != null){
            lqw.eq(LotModel::getSupplierId ,lotModel.getSupplierId());
        }
        if (lotModel.getUpdatedTime() != null){
            lqw.eq(LotModel::getUpdatedTime ,lotModel.getUpdatedTime());
        }
        if (lotModel.getCreatedTime() != null){
            lqw.eq(LotModel::getCreatedTime ,lotModel.getCreatedTime());
        }
        List<LotModel> list = lotModelService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取物联网模型详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "lotModel::getInfo", name = "物联网模型详细信息")
    public R<LotModelRequest> getInfo(@PathVariable("id" ) Long id) {
        LotModel lotModel = lotModelService.getById(id);
        if(lotModel == null) {
            return R.error("模型不存在");
        }
        LotModelRequest lotModelRequest = new LotModelRequest();
        lotModelRequest.setId(lotModel.getId());
        lotModelRequest.setName(lotModel.getName());
        lotModelRequest.setSupplierId(lotModel.getSupplierId());
        //填充item信息
        List<LotModelItem> itemList = lotModelItemService.list((new LambdaQueryWrapper<LotModelItem>()).eq(LotModelItem::getModelId, lotModel.getId()));
        for(LotModelItem item: itemList) {
            LotModelRequest.Item tmpItem = BeanUtils.copy(item, LotModelRequest.Item.class);
            lotModelRequest.getItemList().add(tmpItem);
        }
        return R.success(lotModelRequest);
    }

    /**
     * 新增物联网模型
     */
    @PostMapping
    @Node(value = "lotModel::add", name = "物联网模型新增")
    public R<Boolean> add(@RequestBody LotModelRequest lotModelRequest) {
        LotModel lotModel = new LotModel();
        lotModel.setUpdatedTime(LocalDateTime.now());
        lotModel.setCreatedTime(LocalDateTime.now());
        lotModel.setName(lotModelRequest.getName());
        lotModel.setSupplierId(lotModelRequest.getSupplierId());
        lotModelService.save(lotModel);
        //更新模型数据
        for(LotModelRequest.Item item: lotModelRequest.getItemList()) {
            LotModelItem lotModelItem = BeanUtils.copy(item, LotModelItem.class);
            lotModelItem.setModelId(lotModel.getId());
            lotModelItem.setCreatedTime(LocalDateTime.now());
            lotModelItemService.save(lotModelItem);
        }

        return R.success();
    }

    /**
     * 修改物联网模型
     */
    @PutMapping
    @Node(value = "lotModel::edit", name = "物联网模型修改")
    public R<Boolean> edit(@RequestBody LotModelRequest lotModelRequest) {
        LotModel lotModel = lotModelService.getById(lotModelRequest.getId());
        if(lotModel == null) {
            return R.error("模型不存在");
        }
        lotModel.setUpdatedTime(LocalDateTime.now());
        lotModel.setName(lotModelRequest.getName());
        lotModel.setSupplierId(lotModelRequest.getSupplierId());
        lotModelService.updateById(lotModel);
        //更新模型数据
        lotModelItemService.remove((new LambdaQueryWrapper<LotModelItem>()).eq(LotModelItem::getModelId, lotModel.getId()));
        for(LotModelRequest.Item item: lotModelRequest.getItemList()) {
            LotModelItem lotModelItem = BeanUtils.copy(item, LotModelItem.class);
            lotModelItem.setModelId(lotModel.getId());
            lotModelItem.setCreatedTime(LocalDateTime.now());
            lotModelItemService.save(lotModelItem);
        }

        return R.success();
    }

    /**
     * 删除物联网模型
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "lotModel::remove", name = "物联网模型删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        //没有相关产品才能删除模型
        LambdaQueryWrapper<LotProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(LotProduct::getModelId, Arrays.asList(ids));
        if(lotProductService.count(lambdaQueryWrapper)>0) {
            return R.error("还有产品关联到该模型，请删除产品后执行");
        }
        //删除关联数据
        LambdaQueryWrapper<LotModelItem> removeLqw = new LambdaQueryWrapper<>();
        removeLqw.in(LotModelItem::getModelId, Arrays.asList(ids));
        lotModelItemService.remove(removeLqw);
        return R.success(lotModelService.removeByIds(Arrays.asList(ids)));
    }
}
