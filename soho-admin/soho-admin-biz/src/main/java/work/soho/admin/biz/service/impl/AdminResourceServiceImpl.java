package work.soho.admin.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import work.soho.admin.api.service.AdminResourceApiService;
import work.soho.admin.biz.domain.AdminResource;
import work.soho.admin.biz.mapper.AdminResourceMapper;
import work.soho.admin.biz.service.AdminResourceService;
import work.soho.common.core.util.TreeUtils;
import work.soho.common.security.annotation.Node;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j2
@Service("role-user-resource-admin")
@RequiredArgsConstructor
public class AdminResourceServiceImpl extends ServiceImpl<AdminResourceMapper, AdminResource> implements AdminResourceService, AdminResourceApiService {

    /**
     * AdminResourceMapper
     */
    private final AdminResourceMapper adminResourceMapper;

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;


    /**
     * sync resource to db
     */
    @Override
    public void syncResource2Db() {
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        Set<RequestMappingInfo> keys = map.keySet();
        for (RequestMappingInfo info: keys ) {
            log.info("request mapping info----------------{}", info);
            System.out.println(info);
            HandlerMethod method = map.get(info);
//            System.out.println(method.getMethod().getPar);
            Node node = method.getMethod().getAnnotation(Node.class);
            if(node == null) {
                continue;
            }
            String tableName = getTableName(node.value());
            log.info(tableName);
            if(tableName == null) {
                log.info("没找到表明， 跳过节点");
            }
            String operateName = getOperateName(node.value());
            //检查数据库是否存在该节点
            AdminResource currentNode = adminResourceMapper.selectOne(new LambdaQueryWrapper<AdminResource>().eq(AdminResource::getRoute, node.value()));
            if(currentNode != null) {
                log.info("节点存在：" + currentNode.getRoute());
                continue;
            }
            //检查是否存在父节点
            AdminResource parentResource = adminResourceMapper.selectOne(new LambdaQueryWrapper<AdminResource>().eq(AdminResource::getRoute, "/" + getTableName(node.value()))
                    .orderByAsc(AdminResource::getId).last(" limit 1")
            );
            log.info("父节点： {}", parentResource);
            if(parentResource == null) {
                continue;
            }
            //确定名称
            String nodeName = node.name();
            if(nodeName == null || "".equals(nodeName)) {
                if(operateName.equals("list")) {
                    nodeName = "列表";
                }else if(operateName.equals("edit") || "update".equals(operateName)) {
                    nodeName = "编辑";
                }else if(operateName.equals("remove")) {
                    nodeName = "删除";
                }else if("add".equals(operateName)) {
                    nodeName = "添加";
                }else if("options".equals(operateName)) {
                    nodeName = "选项";
                }else if("details".equals(operateName) || "getInfo".equals(operateName)) {
                    nodeName = "详情";
                }else{
                    continue;
                }
            }

            currentNode = new AdminResource();
            currentNode.setSort(100);
            currentNode.setRoute(node.value());
            currentNode.setName(nodeName);
            currentNode.setVisible(0);
            currentNode.setCreatedTime(new Date());
            currentNode.setBreadcrumbParentId(parentResource.getId());
            currentNode.setIconName(null);
            currentNode.setRemarks(node.name());
            currentNode.setType(2); //后端接口节点
            currentNode.setZhName(node.name());
            adminResourceMapper.insert(currentNode);
        }
    }

    private String getTableName(String key) {
        if(key == null) {
            return null;
        }
        String[] parts = key.split("::");
        if(parts.length != 2) {
            return null;
        }
        return camelToUnderline(parts[0]);
    }

    /**
     * 获取节点请求操作名
     *
     * @param key
     * @return
     */
    private String getOperateName(String key) {
        if(key == null) {
            return null;
        }
        String[] parts = key.split("::");
        if(parts.length != 2) {
            return null;
        }
        return parts[1];
    }

    private String camelToUnderline(String line){
        if(line==null||"".equals(line)){
            return "";
        }
        line=String.valueOf(line.charAt(0)).toUpperCase().concat(line.substring(1));
        StringBuffer sb=new StringBuffer();
        Pattern pattern= Pattern.compile("[A-Z]([a-z\\d]+)?");
        Matcher matcher=pattern.matcher(line);
        while(matcher.find()){
            String word=matcher.group();
            sb.append(word.toUpperCase());
            sb.append(matcher.end()==line.length()?"":"_");
        }
        return sb.toString().toLowerCase();
    }

    /**
     * 获取指定角色对应的资源key集合
     *
     * @param roleName
     * @return
     */
    @SneakyThrows
    @Override
    public List<String> getResourceKeyListByRole(String roleName) {
        List<AdminResource> list = list();
        String roleNameLowerCase = roleName.toLowerCase();
        // 获取角色对应的资源id
        Optional<AdminResource> roleFirst = list.stream().filter(resource -> resource.getName().toLowerCase().equals(roleNameLowerCase)).findFirst();
        // 没有找到则返回空集合
        if(!roleFirst.isPresent()) {
            return List.of();
        }

        TreeUtils<Long, AdminResource> treeUtils = new TreeUtils<>();
        Class<?> c = AdminResource.class;
        treeUtils.loadData(list, c.getMethod("getId"), c.getMethod("getBreadcrumbParentId"));
        List<AdminResource> myList = treeUtils.getAllTreeNodeWidthIds(List.of(roleFirst.get().getId()));
        List<String> resourceKeyList = myList.stream().map(AdminResource::getRoute).collect(Collectors.toList());
        return new ArrayList<>(new HashSet<>(resourceKeyList));
    }
}
