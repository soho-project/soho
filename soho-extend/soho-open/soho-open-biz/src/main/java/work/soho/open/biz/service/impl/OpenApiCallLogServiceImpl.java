package work.soho.open.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.open.biz.domain.OpenApiCallLog;
import work.soho.open.biz.mapper.OpenApiCallLogMapper;
import work.soho.open.biz.service.OpenApiCallLogService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    public Map<String, Long> statisticsLast24Hour(List<Long> appIds) {
        Map<String, Long> result = new LinkedHashMap<>();

        // 初始化24小时，全部为0
        for (int i = 0; i < 24; i++) {
            result.put(String.format("%02d", i), 0L);
        }

        if (CollectionUtils.isEmpty(appIds)) {
            return result;
        }

        // 计算时间范围
        long endTime = System.currentTimeMillis();
        long startTime = endTime - 24 * 60 * 60 * 1000;

        // 使用MyBatis-Plus的SQL builder构建查询
        QueryWrapper<OpenApiCallLog> queryWrapper = new QueryWrapper<>();

        // 注意：这里使用了MySQL的日期函数，如果使用其他数据库需要调整
        queryWrapper.select(
                "DATE_FORMAT(created_time, '%H') as hour",
                "COUNT(*) as count"
        );

        queryWrapper.in("api_id", appIds);
        queryWrapper.between("created_time", LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault()),
                LocalDateTime.ofInstant(Instant.ofEpochMilli(endTime), ZoneId.systemDefault()));
        queryWrapper.groupBy("DATE_FORMAT(created_time, '%H')");

        // 使用MyBatis-Plus的selectMaps方法获取结果
        List<Map<String, Object>> list = this.listMaps(queryWrapper);

        // 处理查询结果
        for (Map<String, Object> map : list) {
            String hour = (String) map.get("hour");
            Long count = ((Number) map.get("count")).longValue();

            result.put(hour, count);
        }

        return result;
    }
}