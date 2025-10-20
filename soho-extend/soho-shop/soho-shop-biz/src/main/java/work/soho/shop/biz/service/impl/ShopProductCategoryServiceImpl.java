package work.soho.shop.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.shop.biz.domain.ShopProductCategory;
import work.soho.shop.biz.mapper.ShopProductCategoryMapper;
import work.soho.shop.biz.service.ShopProductCategoryService;

@RequiredArgsConstructor
@Service
public class ShopProductCategoryServiceImpl extends ServiceImpl<ShopProductCategoryMapper, ShopProductCategory>
    implements ShopProductCategoryService{

}