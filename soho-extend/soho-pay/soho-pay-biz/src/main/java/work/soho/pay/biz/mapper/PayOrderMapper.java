package work.soho.pay.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import work.soho.pay.biz.domain.PayOrder;
import work.soho.pay.biz.dto.PayDashboardHourAmountStatsDTO;
import work.soho.pay.biz.dto.PayDashboardOverviewStatsDTO;
import work.soho.pay.biz.dto.PayDashboardPayMethodHourAmountStatsDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
* @author i
* @description 针对表【pay_order(支付单)】的数据库操作Mapper
* @createDate 2022-11-11 16:31:43
* @Entity work.soho.pay.biz.domain.PayOrder
*/
public interface PayOrderMapper extends BaseMapper<PayOrder> {
    /**
     * 查询支付看板当日概览统计。
     *
     * @param todayStart 今日开始时间
     * @param tomorrowStart 明日开始时间
     * @param successStatus 支付成功状态
     * @param notPayStatus 未支付状态
     * @return 概览统计
     */
    PayDashboardOverviewStatsDTO selectTodayOverview(@Param("todayStart") LocalDateTime todayStart,
                                                     @Param("tomorrowStart") LocalDateTime tomorrowStart,
                                                     @Param("successStatus") Integer successStatus,
                                                     @Param("notPayStatus") Integer notPayStatus);

    /**
     * 查询当日成功支付金额分时统计。
     *
     * @param todayStart 今日开始时间
     * @param tomorrowStart 明日开始时间
     * @param successStatus 支付成功状态
     * @return 分时金额统计
     */
    List<PayDashboardHourAmountStatsDTO> selectTodayPaidAmountByHour(@Param("todayStart") LocalDateTime todayStart,
                                                                     @Param("tomorrowStart") LocalDateTime tomorrowStart,
                                                                     @Param("successStatus") Integer successStatus);

    /**
     * 查询当日按支付方式分组的成功支付金额分时统计。
     *
     * @param todayStart 今日开始时间
     * @param tomorrowStart 明日开始时间
     * @param successStatus 支付成功状态
     * @return 支付方式分时金额统计
     */
    List<PayDashboardPayMethodHourAmountStatsDTO> selectTodayPaidAmountByHourAndPayInfo(@Param("todayStart") LocalDateTime todayStart,
                                                                                         @Param("tomorrowStart") LocalDateTime tomorrowStart,
                                                                                         @Param("successStatus") Integer successStatus);
}



