package work.soho.admin.api.service;

import work.soho.admin.api.vo.OptionVo;

import java.util.List;
import java.util.Map;

public interface AdminDictApiService {
    /**
     * 获取枚举字典Map
     *
     * @param code
     * @return
     */
    Map<Integer, String> getMapByCode(String code);

    /**
     * 获取后台配置的指定字典选项数据
     *
     * @param code
     * @return
     */
    List<OptionVo<Integer, String>> getOptionsByCode(String code);
}
