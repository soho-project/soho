package work.soho.shop.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.shop.biz.domain.ShopUserReviews;
import work.soho.shop.biz.mapper.ShopUserReviewsMapper;
import work.soho.shop.biz.service.ShopUserReviewsService;

@RequiredArgsConstructor
@Service
public class ShopUserReviewsServiceImpl extends ServiceImpl<ShopUserReviewsMapper, ShopUserReviews>
    implements ShopUserReviewsService{

}