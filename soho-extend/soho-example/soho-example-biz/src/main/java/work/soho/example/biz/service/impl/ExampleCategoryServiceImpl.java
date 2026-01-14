package work.soho.example.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.example.biz.domain.ExampleCategory;
import work.soho.example.biz.mapper.ExampleCategoryMapper;
import work.soho.example.biz.service.ExampleCategoryService;

@RequiredArgsConstructor
@Service
public class ExampleCategoryServiceImpl extends ServiceImpl<ExampleCategoryMapper, ExampleCategory>
    implements ExampleCategoryService{

}