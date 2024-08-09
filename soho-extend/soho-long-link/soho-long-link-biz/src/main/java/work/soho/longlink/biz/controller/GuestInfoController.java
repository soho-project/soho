package work.soho.longlink.biz.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.longlink.biz.connect.ConnectManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

@Api(tags = "长链接API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/guest/link/info")
public class GuestInfoController {
    /**
     * 连接管理
     */
    private final ConnectManager connectManager;

    /**
     * 当前服务连接列表
     *
     * @return
     */

    @ApiOperation(value = "当前服务连接列表")
    @GetMapping("/list")
    public Object list() {
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        Iterator<String> uidIterator = connectManager.getAllUid().iterator();
        while(uidIterator.hasNext()) {
            String uid = uidIterator.next();
            Iterator<String> connectIdIterator = connectManager.getConnectIdListByUid(uid).iterator();
            while(connectIdIterator.hasNext()) {
                HashMap<String, Object> item = new HashMap<>();
                item.put("uid", uid);
                item.put("connectId", connectIdIterator.next());
                list.add(item);
            }
        }
        return list;
    }

    /**
     * 获取连接总数
     *
     * @return
     */
    @ApiOperation(value = "获取连接总数")
    @GetMapping(value = "/count")
    public Integer getCount() {
        return connectManager.getAllConnectId().size();
    }
}
