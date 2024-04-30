package work.soho.temporal.db.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.temporal.db.biz.domain.TemporalTableCol;
import work.soho.temporal.db.biz.mapper.TemporalTableColMapper;
import work.soho.temporal.db.biz.service.TemporalTableColService;

@RequiredArgsConstructor
@Service
public class TemporalTableColServiceImpl extends ServiceImpl<TemporalTableColMapper, TemporalTableCol>
    implements TemporalTableColService{

}