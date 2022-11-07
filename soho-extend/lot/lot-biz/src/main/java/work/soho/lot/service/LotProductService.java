package work.soho.lot.service;

import work.soho.lot.domain.LotProduct;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author i
* @description 针对表【lot_product(产品)】的数据库操作Service
* @createDate 2022-10-15 21:42:29
*/
public interface LotProductService extends IService<LotProduct> {
    void updateProduct(String msg);
}
