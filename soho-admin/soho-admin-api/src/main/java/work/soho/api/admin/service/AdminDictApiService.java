package work.soho.api.admin.service;

import work.soho.api.admin.vo.OptionVo;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface AdminDictApiService {
    Map<Integer, String> getMapByCode(String code);
    List<OptionVo<Integer, String>> getOptionsByCode(String code);
}
