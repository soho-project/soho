package work.soho.longlink.api.sender;

import java.util.List;
import java.util.Map;

/**
 * 长链接相关查询接口
 */
public interface QueryLongLink {
    Map<String,Integer> getOnlineStatus(List<String> uids);
}
