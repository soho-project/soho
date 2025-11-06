package work.soho.shop.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.shop.biz.domain.ShopProductCategory;
import work.soho.shop.biz.enums.ShopProductCategoryEnums;
import work.soho.shop.biz.mapper.ShopProductCategoryMapper;
import work.soho.shop.biz.service.ShopProductCategoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ShopProductCategoryServiceImpl extends ServiceImpl<ShopProductCategoryMapper, ShopProductCategory>
    implements ShopProductCategoryService{

    @Override
    public List<Long> getCategoryIdsById(Long productId) {
        List<ShopProductCategory> list = this.list(new LambdaQueryWrapper<ShopProductCategory>().eq(ShopProductCategory::getStatus, ShopProductCategoryEnums.Status.ENABLE.getId()));
        Map<Long, ShopProductCategory> map = list.stream().collect(Collectors.toMap(ShopProductCategory::getId, v -> v));
        Map<Long, List<ShopProductCategory>> parentMap = list.stream().filter(item -> item.getParentId() != null).collect(Collectors.groupingBy(ShopProductCategory::getParentId));
        List<Long> categoryIds = new ArrayList<>();
        findCategoryIds(productId, categoryIds, parentMap);
        categoryIds.add(productId);
        return categoryIds;
    }

    private void findCategoryIds(Long parentId, List<Long> categoryIds, Map<Long, List<ShopProductCategory>> map) {
        List<ShopProductCategory> list = map.get(parentId);
        if(list != null) {
            for (ShopProductCategory item : list) {
                categoryIds.add(item.getId());
                findCategoryIds(item.getId(), categoryIds, map);
            }
        }
    }
}