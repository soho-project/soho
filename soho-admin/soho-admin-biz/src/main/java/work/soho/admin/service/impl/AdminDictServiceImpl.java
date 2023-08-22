package work.soho.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.admin.domain.AdminDict;
import work.soho.admin.mapper.AdminDictMapper;
import work.soho.admin.service.AdminDictService;

@RequiredArgsConstructor
@Service
public class AdminDictServiceImpl extends ServiceImpl<AdminDictMapper, AdminDict> implements AdminDictService {

}