package work.soho.temporal.db.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.temporal.db.biz.domain.TemporalCategory;
import work.soho.temporal.db.biz.mapper.TemporalCategoryMapper;
import work.soho.temporal.db.biz.service.TemporalCategoryService;

@RequiredArgsConstructor
@Service
public class TemporalCategoryServiceImpl extends ServiceImpl<TemporalCategoryMapper, TemporalCategory>
    implements TemporalCategoryService{

}