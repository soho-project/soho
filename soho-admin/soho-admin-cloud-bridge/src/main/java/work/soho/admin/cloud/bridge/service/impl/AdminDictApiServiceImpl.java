package work.soho.admin.cloud.bridge.service.impl;

import org.springframework.stereotype.Service;
import work.soho.admin.api.service.AdminDictApiService;
import work.soho.admin.api.vo.OptionVo;

import java.util.List;
import java.util.Map;

@Service
public class AdminDictApiServiceImpl implements AdminDictApiService {
    @Override
    public Map<Integer, String> getMapByCode(String code) {
        return Map.of();
    }

    @Override
    public List<OptionVo<Integer, String>> getOptionsByCode(String code) {
        return List.of();
    }
}
