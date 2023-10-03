package work.soho.longlink.biz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.longlink.biz.connect.ConnectManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
}
