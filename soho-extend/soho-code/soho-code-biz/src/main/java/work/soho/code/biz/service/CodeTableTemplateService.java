package work.soho.code.biz.service;

import work.soho.code.biz.domain.CodeTableTemplate;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author fang
* @description 针对表【code_table_template(代码表模板)】的数据库操作Service
* @createDate 2022-12-08 00:30:04
*/
public interface CodeTableTemplateService extends IService<CodeTableTemplate> {
    CodeTableTemplate getByName(String name);
    CodeTableTemplate getOneById(Integer id);
}
