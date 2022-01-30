package work.soho.admin.service.impl;

import lombok.RequiredArgsConstructor;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import work.soho.admin.annotation.Node;
import work.soho.admin.mapper.AdminResourceMapper;
import work.soho.api.admin.po.AdminResource;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import work.soho.admin.service.AdminResourceService;

@Service
@RequiredArgsConstructor
public class AdminResourceServiceImpl implements AdminResourceService{

    /**
     * AdminResourceMapper
     */
    private final AdminResourceMapper adminResourceMapper;

    /**
     * RequestMappingHandlerMapping
     */
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    /**
     * get all resource node
     *
     * @return
     */
    public ArrayList<Node> getAllResourceNode() {

        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        Set<RequestMappingInfo> keys = map.keySet();
        for (RequestMappingInfo info: keys ) {
            HandlerMethod method = map.get(info);

            System.out.println(method.toString() );
            System.out.println("=====>" + info.getPathPatternsCondition());
            System.out.println(info.getPatternsCondition().getPatterns());
            System.out.println(method.getMethod().getAnnotation(Node.class));
        }

        Reflections reflections = new Reflections("work.soho", Scanners.MethodsAnnotated);
        Set<Method> methods = reflections.getMethodsAnnotatedWith(Node.class);
        ArrayList<Node> arrayList = new ArrayList<>();
        for (Method method : methods) {
            Node node = method.getAnnotation(Node.class);
            if (null != node) {
                System.out.println(node.value());
                arrayList.add(node);
            }
        }
        return arrayList;
    }

    /**
     * sync resource to db
     */
    public void syncResource2Db() {
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        Set<RequestMappingInfo> keys = map.keySet();
        for (RequestMappingInfo info: keys ) {
            HandlerMethod method = map.get(info);
            Node node = method.getMethod().getAnnotation(Node.class);
            if(node == null) continue;
            for(String path: info.getPatternsCondition().getPatterns()) {
                AdminResource queryAdminResource = new AdminResource();
                queryAdminResource.setKey(node.value());
                AdminResource data = adminResourceMapper.getByKey(queryAdminResource.getKey());

                queryAdminResource.setName(node.name());
                queryAdminResource.setPath(path);
                queryAdminResource.setCreatedTime(new Timestamp(System.currentTimeMillis()));
                queryAdminResource.setVisible(node.visible());
                queryAdminResource.setOrder(node.order());
                queryAdminResource.setRemarks(node.describe());
                if(data == null) {
                    adminResourceMapper.insert(queryAdminResource);
                } else {
                    queryAdminResource.setId(data.getId());
                    adminResourceMapper.update(queryAdminResource);
                }
            }
        }
    }
}
