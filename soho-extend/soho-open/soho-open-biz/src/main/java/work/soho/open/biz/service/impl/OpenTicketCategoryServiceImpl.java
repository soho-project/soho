package work.soho.open.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.open.biz.domain.OpenTicketCategory;
import work.soho.open.biz.mapper.OpenTicketCategoryMapper;
import work.soho.open.biz.service.OpenTicketCategoryService;

@RequiredArgsConstructor
@Service
public class OpenTicketCategoryServiceImpl extends ServiceImpl<OpenTicketCategoryMapper, OpenTicketCategory>
    implements OpenTicketCategoryService{

}