package work.soho.admin.utils;

import cn.hutool.core.lang.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeUtils<I, N> {

    /**
     * 根据父ID获取的hashmap
     */
    private Map<I, ArrayList<N>> parentMap = new HashMap<>();
    private Map<I, N> map = new HashMap<>();

    private Method idMethod = null;

    private Method parentIdMethod = null;

    /**
     * 导入树基本结构
     *
     * @param list
     * @param idMethod
     * @param parentIdGetMethod
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public void loadData(List<N> list, Method idMethod, Method parentIdGetMethod) throws InvocationTargetException, IllegalAccessException {
        Assert.notNull(idMethod, "获取ID的方法不能为空");
        Assert.notNull(parentIdGetMethod, "获取父ID的方法不能为空");
        this.idMethod =idMethod;
        this.parentIdMethod = parentIdGetMethod;

        for(N node: list) {
            I currentParentId = (I) parentIdGetMethod.invoke(node);
            I id = (I) idMethod.invoke(node);

            //获取父节点list
            ArrayList<N> pList = parentMap.get(currentParentId);
            if(pList == null) {
                pList = new ArrayList<>();
                parentMap.put(currentParentId, pList);
            }
            pList.add(node);

            map.put(id, node);
        }
    }

    /**
     * 获取指定节点以及子节点数据
     *
     * @param id
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public List<N> getAllTreeNodeWidthId(I id) throws InvocationTargetException, IllegalAccessException {
        List<N> l = new ArrayList<>();
        //添加节点本身
        if(map.get(id) != null) {
            l.add(map.get(id));
            //添加子节点
            ArrayList<N> sonList = parentMap.get(id);
            if(sonList != null) {
                for(N node: sonList) {
                    l.addAll(getAllTreeNodeWidthId((I) idMethod.invoke(node)));
                }
            }
        }
        return l;
    }

    /**
     * 获取指定节点以及子节点
     *
     * @param ids
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public List<N> getAllTreeNodeWidthIds(List<I> ids) throws InvocationTargetException, IllegalAccessException {
        List<N> l = new ArrayList<>();
        for(I id: ids) {
            //添加节点本身
            l.addAll(getAllTreeNodeWidthId(id));
        }
        return l;
    }



}
