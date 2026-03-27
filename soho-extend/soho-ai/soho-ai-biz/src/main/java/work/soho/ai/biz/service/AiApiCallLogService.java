package work.soho.ai.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.ai.biz.domain.AiApiCallLog;
import work.soho.ai.biz.dto.AiApiCallLogHourTokenDTO;
import work.soho.ai.biz.dto.AiApiCallLogModelTokenDTO;
import work.soho.ai.biz.dto.AiApiCallLogTokenOverviewDTO;

import java.util.List;

public interface AiApiCallLogService extends IService<AiApiCallLog> {
    AiApiCallLogTokenOverviewDTO statisticsTodayTokens();

    List<AiApiCallLogHourTokenDTO> statisticsLast12HoursTokens();

    List<AiApiCallLogModelTokenDTO> statisticsLast12HoursTokensByModel();
}
