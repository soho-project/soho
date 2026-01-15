package work.soho.open.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.open.biz.domain.OpenApiCallLog;
import work.soho.open.biz.dto.HourCountDTO;
import work.soho.open.biz.mapper.OpenApiCallLogMapper;
import work.soho.open.biz.service.OpenApiCallLogService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class OpenApiCallLogServiceImpl extends ServiceImpl<OpenApiCallLogMapper, OpenApiCallLog>
    implements OpenApiCallLogService{

    @Override
    public Long statisticsToday(List<Long> appIds) {
        LambdaQueryWrapper<OpenApiCallLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.gt(OpenApiCallLog::getCreatedTime, LocalDate.now().atStartOfDay());
        queryWrapper.in(OpenApiCallLog::getApiId, appIds);
        return count(queryWrapper);
    }

    @Override
    public Long statisticsTotal(List<Long> appIds) {
        LambdaQueryWrapper<OpenApiCallLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(OpenApiCallLog::getApiId, appIds);
        return count(queryWrapper);
    }

    @Override
    public List<HourCountDTO> statisticsLast24HourList(List<Long> appIds) {
        List<HourCountDTO> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(appIds)) return result;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startHour = now.truncatedTo(ChronoUnit.HOURS).minusHours(23);

        // 初始化 24 桶（有序）
        Map<String, Long> tmp = new LinkedHashMap<>();
        DateTimeFormatter hourFmt = DateTimeFormatter.ofPattern("HH");
        for (int i = 0; i < 24; i++) {
            tmp.put(startHour.plusHours(i).format(hourFmt), 0L);
        }

        QueryWrapper<OpenApiCallLog> qw = new QueryWrapper<>();
        qw.select("DATE_FORMAT(created_time, '%Y-%m-%d %H') AS hour", "COUNT(*) AS cnt");
        qw.in("api_id", appIds);
        qw.between("created_time", startHour, now);
        qw.groupBy("hour");
        qw.orderByAsc("hour");

        List<Map<String, Object>> list = this.listMaps(qw);

        for (Map<String, Object> row : list) {
            String hourStr = (String) row.get("hour"); // yyyy-MM-dd HH
            Long cnt = ((Number) row.get("cnt")).longValue();
            tmp.put(hourStr.substring(11, 13), cnt);
        }

        // 转 list，顺序稳定
        for (Map.Entry<String, Long> e : tmp.entrySet()) {
            result.add(new HourCountDTO(e.getKey(), e.getValue()));
        }
        return result;
    }

}