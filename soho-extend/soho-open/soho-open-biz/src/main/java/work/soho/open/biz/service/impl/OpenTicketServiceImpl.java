package work.soho.open.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.open.biz.domain.OpenTicket;
import work.soho.open.biz.mapper.OpenTicketMapper;
import work.soho.open.biz.service.OpenTicketService;

@RequiredArgsConstructor
@Service
public class OpenTicketServiceImpl extends ServiceImpl<OpenTicketMapper, OpenTicket>
    implements OpenTicketService{

}