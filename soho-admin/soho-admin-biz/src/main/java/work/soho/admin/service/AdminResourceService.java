package work.soho.admin.service;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.soho.admin.annotation.Node;
import work.soho.admin.mapper.AdminResourceMapper;
import work.soho.api.admin.po.AdminResource;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class AdminResourceService {
    @Autowired
    private AdminResourceMapper adminResourceMapper;

    /**
     * get all resource node
     *
     * @return
     */
    public ArrayList<Node> getAllResourceNode() {
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
        for (Node node: getAllResourceNode()) {
            AdminResource queryAdminResource = new AdminResource();
            queryAdminResource.setKey(node.value());
            AdminResource data = adminResourceMapper.getByKey(queryAdminResource.getKey());

            queryAdminResource.setCreatedTime(new Timestamp(System.currentTimeMillis()));
            queryAdminResource.setVisible(node.visible());
            queryAdminResource.setOrder(node.order());
            if(data == null) {
                adminResourceMapper.insert(queryAdminResource);
            } else {
                queryAdminResource.setId(data.getId());
                adminResourceMapper.update(queryAdminResource);
            }
        }
    }
}
