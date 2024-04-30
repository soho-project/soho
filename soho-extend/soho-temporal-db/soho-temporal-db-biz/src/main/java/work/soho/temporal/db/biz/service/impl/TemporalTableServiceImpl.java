package work.soho.temporal.db.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.temporal.db.biz.domain.TemporalTable;
import work.soho.temporal.db.biz.mapper.TemporalTableMapper;
import work.soho.temporal.db.biz.service.TemporalTableService;

@RequiredArgsConstructor
@Service
public class TemporalTableServiceImpl extends ServiceImpl<TemporalTableMapper, TemporalTable>
    implements TemporalTableService{

}