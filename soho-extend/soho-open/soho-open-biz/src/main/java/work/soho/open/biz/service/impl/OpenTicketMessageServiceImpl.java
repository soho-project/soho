package work.soho.open.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.open.biz.domain.OpenTicketMessage;
import work.soho.open.biz.mapper.OpenTicketMessageMapper;
import work.soho.open.biz.service.OpenTicketMessageService;

@RequiredArgsConstructor
@Service
public class OpenTicketMessageServiceImpl extends ServiceImpl<OpenTicketMessageMapper, OpenTicketMessage>
    implements OpenTicketMessageService{

}