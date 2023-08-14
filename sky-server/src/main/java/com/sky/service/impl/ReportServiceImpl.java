package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 统计指定时间内的营业额
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //存放从begin到end日期范围内的每天日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
//        LocalDateTime weekend = begin.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).atStartOfDay();
        //将begin到end的每一天放到dateList中
        if (begin.equals(end)){  //统计“昨天”一天的营业额
            dateList.add(end);
        }else {
            while (!begin.equals(end)) {
                begin = begin.plusDays(1);  //计算指定日期的后一天的指定日期
                dateList.add(begin);
            }
        }
        //存放每天的营业额总额
        ArrayList<Double> turnoverList = new ArrayList<>();
        //查询从begin到end每一天的营业额总额，放入turnoverList中
        for (LocalDate date : dateList) {
            //查询date日期对应的“已完成的订单”的营业额数据
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);  //获取date日期的最初始时间（00:00:00）
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);  //获取date日期的最结尾时间（23:59:59）
            HashMap<String, Object> map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.getOrdersSumByMap(map);
            turnover = turnover == null ? 0.00 : turnover;
            turnoverList.add(turnover);
        }
        //将List列表中的每个元素按逗号分隔、拼接，转换成一个字符串，转换成返回给Echart表格使用的数据类型
        String days = StringUtils.join(dateList, ",");
        String turnoverSums = StringUtils.join(turnoverList, ",");
        return TurnoverReportVO.builder()
                .dateList(days)
                .turnoverList(turnoverSums)
                .build();
    }

    /**
     * 统计指定时间内的用户量
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //存放从begin到end日期范围内的每天日期
        List<LocalDate> dateList = new ArrayList<>();
        //将begin到end的每一天放到dateList中
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);  //计算指定日期的后一天的指定日期
            dateList.add(begin);
        }
        //存放每天新增的用户总量
        ArrayList<Integer> newUserList = new ArrayList<>();
        //存放截止每天的用户总量
        ArrayList<Integer> totalUserList = new ArrayList<>();
        //查询从begin到end每一天的用户总量，放入turnoverList中
        for (LocalDate date : dateList) {
            //查询date日期对应的“已完成的订单”的营业额数据
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);  //获取date日期的最初始时间（00:00:00）
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);  //获取date日期的最结尾时间（23:59:59）
            HashMap<String, Object> map = new HashMap<>();
            map.put("end", endTime);
            //总用户数量
            Integer totalUser = orderMapper.getUserCountByMap(map);
            totalUser = totalUser == null ? 0 : totalUser;
            totalUserList.add(totalUser);

            map.put("begin", beginTime);
            //新增用户数量
            Integer newUser = orderMapper.getUserCountByMap(map);
            newUser = newUser == null ? 0 : newUser;
            newUserList.add(newUser);
        }
        //将List列表中的每个元素按逗号分隔、拼接，转换成一个字符串，转换成返回给Echart表格使用的数据类型
        String days = StringUtils.join(dateList, ",");
        String newUsers = StringUtils.join(newUserList, ",");
        String totalUsers = StringUtils.join(totalUserList, ",");
        return UserReportVO.builder()
                .dateList(days)
                .newUserList(newUsers)
                .totalUserList(totalUsers)
                .build();
    }
}
