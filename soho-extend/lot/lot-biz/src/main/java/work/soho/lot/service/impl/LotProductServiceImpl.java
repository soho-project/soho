package work.soho.lot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.core.util.JsonUtils;
import work.soho.common.core.util.JacksonUtils;
import work.soho.lot.domain.LotModel;
import work.soho.lot.domain.LotModelItem;
import work.soho.lot.domain.LotProduct;
import work.soho.lot.domain.LotProductValue;
import work.soho.lot.mapper.LotModelItemMapper;
import work.soho.lot.mapper.LotModelMapper;
import work.soho.lot.mapper.LotProductValueMapper;
import work.soho.lot.service.LotProductService;
import work.soho.lot.mapper.LotProductMapper;
import org.springframework.stereotype.Service;
import work.soho.lot.service.LotProductValueService;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
* @author i
* @description 针对表【lot_product(产品)】的数据库操作Service实现
* @createDate 2022-10-15 21:42:29
*/
@RequiredArgsConstructor
@Service
public class LotProductServiceImpl extends ServiceImpl<LotProductMapper, LotProduct>
    implements LotProductService{
    private final LotProductMapper lotProductMapper;
    private final LotModelMapper lotModelMapper;
    private final LotModelItemMapper lotModelItemMapper;
    private final LotProductValueService lotProductValueService;
    private final LotProductValueMapper lotProductValueMapper;

    /**
     * 更新产品信息
     *
     * @param msg
     */
    public void updateProduct(String msg) {
        HashMap data = JacksonUtils.toBean(msg, HashMap.class);
        String mac = (String) data.get("mac");
        LambdaQueryWrapper<LotProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(LotProduct::getMac, mac);
        LotProduct lotProduct = lotProductMapper.selectOne(lambdaQueryWrapper);
        if(lotProduct == null) {
            return;
        }

        //获取产品模型取值key
        LambdaQueryWrapper<LotModelItem> lotModelItemLambdaQueryWrapper = new LambdaQueryWrapper<>();
        lotModelItemLambdaQueryWrapper.eq(LotModelItem::getModelId, lotProduct.getModelId());
        List<LotModelItem> itemList = lotModelItemMapper.selectList(lotModelItemLambdaQueryWrapper);
        for (LotModelItem item:itemList) {
            String paramsName = item.getParamsName();
            LambdaQueryWrapper<LotProductValue> lotProductValueLambdaQueryWrapper = new LambdaQueryWrapper<>();
            lotProductValueLambdaQueryWrapper.eq(LotProductValue::getParamsName, item.getId());
            lotProductValueLambdaQueryWrapper.eq(LotProductValue::getProductId, lotProduct.getId());
            LotProductValue productValue = lotProductValueMapper.selectOne(lotProductValueLambdaQueryWrapper);
            //检查更新key
            if(data.containsKey(paramsName)) {
                productValue.setValue((String) data.get(paramsName));
                productValue.setUpdatedTime(LocalDateTime.now());
                lotProductValueMapper.updateById(productValue);
            }
        }
    }
}




