package work.soho.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.admin.mapper.AdminResourceMapper;
import work.soho.admin.domain.AdminResource;

import work.soho.admin.service.AdminResourceService;

@Service
@RequiredArgsConstructor
public class AdminResourceServiceImpl extends ServiceImpl<AdminResourceMapper, AdminResource> implements AdminResourceService{

    /**
     * AdminResourceMapper
     */
    private final AdminResourceMapper adminResourceMapper;

    /**
     * sync resource to db
     */
    @Override
    public void syncResource2Db() {
//        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
//        Set<RequestMappingInfo> keys = map.keySet();
//        for (RequestMappingInfo info: keys ) {
//            HandlerMethod method = map.get(info);
//            Node node = method.getMethod().getAnnotation(Node.class);
//            if(node == null) continue;
//            for(String path: info.getPatternsCondition().getPatterns()) {
//                AdminResource queryAdminResource = new AdminResource();
//                queryAdminResource.setKey(node.value());
//                AdminResource data = adminResourceMapper.getByKey(queryAdminResource.getKey());
//
//                queryAdminResource.setName(node.name());
//                queryAdminResource.setPath(path);
//                queryAdminResource.setCreatedTime(new Timestamp(System.currentTimeMillis()));
//                queryAdminResource.setVisible(node.visible());
//                queryAdminResource.setOrder(node.order());
//                queryAdminResource.setRemarks(node.describe());
//                if(data == null) {
//                    adminResourceMapper.insert(queryAdminResource);
//                } else {
//                    queryAdminResource.setId(data.getId());
//                    adminResourceMapper.update(queryAdminResource);
//                }
//            }
//        }
    }
}
