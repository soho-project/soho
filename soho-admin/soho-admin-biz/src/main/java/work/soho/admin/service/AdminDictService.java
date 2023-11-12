package work.soho.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.admin.domain.AdminDict;

import java.util.List;
import java.util.Map;

public interface AdminDictService extends IService<AdminDict> {
    List<AdminDict> getListByCode(String code);
}
