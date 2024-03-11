package work.soho.groovy.biz.service.impl;

import cn.hutool.core.lang.Assert;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.groovy.biz.domain.GroovyInfo;
import work.soho.groovy.biz.mapper.GroovyInfoMapper;
import work.soho.groovy.biz.service.GroovyExecutorService;
import work.soho.groovy.biz.service.GroovyInfoService;
import work.soho.groovy.exception.NotFoundException;
import work.soho.groovy.service.GroovyInfoApiService;

@RequiredArgsConstructor
@Service
public class GroovyInfoServiceImpl extends ServiceImpl<GroovyInfoMapper, GroovyInfo>
    implements GroovyInfoService, GroovyInfoApiService {

    private final GroovyExecutorService groovyExecutor;;

    /**
     * 执行指定名称代码
     *
     * @param name
     * @return
     */
    @Override
    public Object executor(String name) {
        GroovyInfo groovyInfo = getByName(name);
        Assert.notNull(groovyInfo, "找不到任务名为：" + name + "的代码");
        return this.groovyExecutor.execute(groovyInfo.getCode());
    }

    /**
     * 获取代码对应的实体对象
     *
     * @param name
     * @return
     */
    public Object loadObjectByName(String name) {
        GroovyInfo groovyInfo = getByName(name);
        if(groovyInfo == null) {
            throw new NotFoundException();
        }
        return this.groovyExecutor.loadObjectFromCode(groovyInfo.getCode());
    }

    /**
     * 根据名称获取
     *
     * @param name
     * @return
     */
    public GroovyInfo getByName(String name) {
        LambdaQueryWrapper<GroovyInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GroovyInfo::getName, name);
        GroovyInfo groovyInfo = this.getOne(lambdaQueryWrapper);
        return groovyInfo;
    }
}
