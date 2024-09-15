package work.soho.admin.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.admin.biz.domain.AdminDict;
import work.soho.admin.biz.mapper.AdminDictMapper;
import work.soho.admin.biz.service.AdminDictService;
import work.soho.admin.api.service.AdminDictApiService;
import work.soho.admin.api.vo.OptionVo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AdminDictServiceImpl extends ServiceImpl<AdminDictMapper, AdminDict> implements AdminDictService, AdminDictApiService {
    /**
     * 获取
     *
     * @param code
     * @return
     */
    @Override
    public Map<Integer, String> getMapByCode(String code) {
        List<AdminDict> list = getListByCode(code);
        return list.stream().collect(Collectors.toMap(AdminDict::getDictKey, AdminDict::getDictValue));
    }

    @Override
    public List<OptionVo<Integer, String>> getOptionsByCode(String code) {
        return getListByCode(code).stream().map(item->{
            OptionVo<Integer, String> optionVo = new OptionVo<>();
            optionVo.setValue(item.getDictKey());
            optionVo.setLabel(item.getDictValue());
            return optionVo;
        }).collect(Collectors.toList());
    }

    /**
     * 根据code获取列表
     *
     * @param code
     * @return
     */
    @Override
    public List<AdminDict> getListByCode(String code) {
        LambdaQueryWrapper<AdminDict> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AdminDict::getCode, code);
        lambdaQueryWrapper.gt(AdminDict::getDictKey, -1);
        lambdaQueryWrapper.orderByAsc(AdminDict::getSort);
        List<AdminDict> list = list(lambdaQueryWrapper);
        return list;
    }
}
