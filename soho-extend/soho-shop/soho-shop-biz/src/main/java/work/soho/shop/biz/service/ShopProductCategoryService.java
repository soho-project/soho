package work.soho.shop.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.shop.biz.domain.ShopProductCategory;

import java.util.List;

public interface ShopProductCategoryService extends IService<ShopProductCategory> {
    /**
     * 根据父ID获取所有子ID
     *
     * @param productId
     * @return
     */
    List<Long> getCategoryIdsById(Long productId);
}