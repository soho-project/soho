package work.soho.admin.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.admin.biz.domain.AdminDict;

import java.util.List;

public interface AdminDictService extends IService<AdminDict> {
    List<AdminDict> getListByCode(String code);
}
