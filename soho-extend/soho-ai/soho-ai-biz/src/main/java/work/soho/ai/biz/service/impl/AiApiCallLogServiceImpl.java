package work.soho.ai.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.util.CollectionUtils;
import org.springframework.stereotype.Service;
import work.soho.ai.biz.domain.AiApiCallLog;
import work.soho.ai.biz.dto.AiApiCallLogHourTokenDTO;
import work.soho.ai.biz.dto.AiApiCallLogModelTokenDTO;
import work.soho.ai.biz.dto.AiApiCallLogTokenOverviewDTO;
import work.soho.ai.biz.mapper.AiApiCallLogMapper;
import work.soho.ai.biz.service.AiApiCallLogService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiApiCallLogServiceImpl extends ServiceImpl<AiApiCallLogMapper, AiApiCallLog>
        implements AiApiCallLogService {

    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
    private static final String EMPTY_MODEL_NAME = "unknown";

    @Override
    public AiApiCallLogTokenOverviewDTO statisticsTodayTokens() {
        LocalDateTime startTime = LocalDate.now().atStartOfDay();
        LocalDateTime endTime = LocalDateTime.now();
        return sumTokens(startTime, endTime);
    }

    @Override
    public List<AiApiCallLogHourTokenDTO> statisticsLast12HoursTokens() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startHour = now.truncatedTo(ChronoUnit.HOURS).minusHours(11);
        Map<String, AiApiCallLogHourTokenDTO> hourTokenMap = initHourTokenMap(startHour, 12);

        QueryWrapper<AiApiCallLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(
                "DATE_FORMAT(created_time, '%Y-%m-%d %H') AS hour",
                "SUM(prompt_tokens) AS prompt_tokens",
                "SUM(completion_tokens) AS completion_tokens",
                "SUM(total_tokens) AS total_tokens"
        );
        queryWrapper.between("created_time", startHour, now);
        queryWrapper.groupBy("hour");
        queryWrapper.orderByAsc("hour");

        List<Map<String, Object>> records = this.listMaps(queryWrapper);
        for (Map<String, Object> row : records) {
            String hour = stringValue(row.get("hour"));
            AiApiCallLogHourTokenDTO item = hourTokenMap.get(hour);
            if (item == null) {
                continue;
            }
            item.setPromptTokens(longValue(row.get("prompt_tokens")));
            item.setCompletionTokens(longValue(row.get("completion_tokens")));
            item.setTotalTokens(longValue(row.get("total_tokens")));
        }

        return new ArrayList<>(hourTokenMap.values());
    }

    @Override
    public List<AiApiCallLogModelTokenDTO> statisticsLast12HoursTokensByModel() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startHour = now.truncatedTo(ChronoUnit.HOURS).minusHours(11);

        QueryWrapper<AiApiCallLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(
                "model",
                "SUM(prompt_tokens) AS prompt_tokens",
                "SUM(completion_tokens) AS completion_tokens",
                "SUM(total_tokens) AS total_tokens"
        );
        queryWrapper.between("created_time", startHour, now);
        queryWrapper.groupBy("model");
        queryWrapper.orderByDesc("total_tokens");

        List<Map<String, Object>> records = this.listMaps(queryWrapper);
        List<AiApiCallLogModelTokenDTO> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(records)) {
            return result;
        }

        for (Map<String, Object> row : records) {
            result.add(new AiApiCallLogModelTokenDTO(
                    normalizeModelName(stringValue(row.get("model"))),
                    longValue(row.get("prompt_tokens")),
                    longValue(row.get("completion_tokens")),
                    longValue(row.get("total_tokens"))
            ));
        }
        return result;
    }

    private AiApiCallLogTokenOverviewDTO sumTokens(LocalDateTime startTime, LocalDateTime endTime) {
        QueryWrapper<AiApiCallLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(
                "SUM(prompt_tokens) AS prompt_tokens",
                "SUM(completion_tokens) AS completion_tokens",
                "SUM(total_tokens) AS total_tokens"
        );
        queryWrapper.between("created_time", startTime, endTime);

        Map<String, Object> row = this.getMap(queryWrapper);
        if (row == null || row.isEmpty()) {
            return new AiApiCallLogTokenOverviewDTO(0L, 0L, 0L);
        }

        return new AiApiCallLogTokenOverviewDTO(
                longValue(row.get("prompt_tokens")),
                longValue(row.get("completion_tokens")),
                longValue(row.get("total_tokens"))
        );
    }

    private Map<String, AiApiCallLogHourTokenDTO> initHourTokenMap(LocalDateTime startHour, int size) {
        Map<String, AiApiCallLogHourTokenDTO> result = new LinkedHashMap<>();
        for (int i = 0; i < size; i++) {
            String hour = startHour.plusHours(i).format(HOUR_FORMATTER);
            result.put(hour, new AiApiCallLogHourTokenDTO(hour, 0L, 0L, 0L));
        }
        return result;
    }

    private Long longValue(Object value) {
        return value instanceof Number ? ((Number) value).longValue() : 0L;
    }

    private String stringValue(Object value) {
        return value == null ? null : value.toString();
    }

    private String normalizeModelName(String model) {
        if (model == null || model.trim().isEmpty()) {
            return EMPTY_MODEL_NAME;
        }
        return model;
    }
}
