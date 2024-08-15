package work.soho.code.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.soho.code.biz.domain.CodeTableTemplate;
import work.soho.code.biz.service.CodeTableTemplateService;
import work.soho.code.biz.mapper.CodeTableTemplateMapper;
import org.springframework.stereotype.Service;

/**
* @author fang
* @description 针对表【code_table_template(代码表模板)】的数据库操作Service实现
* @createDate 2022-12-08 00:30:04
*/
@Service
public class CodeTableTemplateServiceImpl extends ServiceImpl<CodeTableTemplateMapper, CodeTableTemplate>
    implements CodeTableTemplateService{

    @Override
    public CodeTableTemplate getByName(String name) {
        return getOne(new LambdaQueryWrapper<CodeTableTemplate>().eq(CodeTableTemplate::getName, name));
    }

    @Override
    public CodeTableTemplate getOneById(Integer id) {
        return getById(id);
    }
}




