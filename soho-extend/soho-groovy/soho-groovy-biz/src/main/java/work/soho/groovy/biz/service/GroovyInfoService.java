package work.soho.groovy.biz.service;

import work.soho.groovy.biz.domain.GroovyInfo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface GroovyInfoService extends IService<GroovyInfo> {
    Object executor(String name);
}
