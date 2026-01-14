package work.soho.open.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.open.biz.domain.OpenApiCallLog;

import java.util.List;
import java.util.Map;

public interface OpenApiCallLogService extends IService<OpenApiCallLog> {
    Long statisticsToday(List<Long> appIds);
    Long statisticsTotal(List<Long> appIds);
    // 统计最近24小时没个小时的调用次数
    Map<String, Long> statisticsLast24Hour(List<Long> appIds);
}