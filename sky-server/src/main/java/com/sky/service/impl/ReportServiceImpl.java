package com.sky.service.impl;

import com.aliyuncs.http.HttpResponse;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;

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
        if (begin.equals(end)) {  //统计“昨天”一天的营业额
            dateList.add(end);
        } else {
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
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
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
            Integer totalUser = userMapper.getUserCountByMap(map);
            totalUser = totalUser == null ? 0 : totalUser;
            totalUserList.add(totalUser);

            map.put("begin", beginTime);
            //新增用户数量
            Integer newUser = userMapper.getUserCountByMap(map);
            newUser = newUser == null ? 0 : newUser;
            newUserList.add(newUser);
        }
        //将List列表中的每个元素按逗号分隔、拼接，转换成一个字符串，转换成返回给Echart表格使用的数据类型
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    /**
     * 统计指定时间内的订单量
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        //存放从begin到end日期范围内的每天日期
        List<LocalDate> dateList = new ArrayList<>();
        //将begin到end的每一天放到dateList中
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);  //计算指定日期的后一天的指定日期
            dateList.add(begin);
        }
        //存放每天的订单总数
        ArrayList<Integer> totalOrderCountList = new ArrayList<>();
        //存放截止每天的有效订单数
        ArrayList<Integer> validOrderCountList = new ArrayList<>();
        //遍历日期集合，查询每天的订单总数和有效订单数
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);  //获取date日期的最初始时间（00:00:00）
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);  //获取date日期的最结尾时间（23:59:59）
            //查询每天的订单总数
            Integer totalOrderCount = getOrderCount(beginTime, endTime, null);
            totalOrderCountList.add(totalOrderCount);
            //查询每天的有效订单数
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);
            validOrderCountList.add(validOrderCount);
        }
        //计算时间区间内的总订单数量
        Integer totalOrderCount = totalOrderCountList.stream().reduce(Integer::sum).get();
        //计算时间区间内的有效订单数量
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        //计算订单完成率
        double orderCompletionRate = totalOrderCount != 0 ? validOrderCount.doubleValue() / totalOrderCount.doubleValue() : 0.0;
        //将List列表中的每个元素按逗号分隔、拼接，转换成一个字符串，转换成返回给Echart表格使用的数据类型
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(totalOrderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 根据条件返回订单数量
     *
     * @param begin
     * @param end
     * @param status
     * @return
     */
    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);
        return orderMapper.getOrdersCount(map);
    }

    /**
     * 返回销量前10的菜品或套餐
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getSaleTop10Statistics(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);  //获取date日期的最初始时间（00:00:00）
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);  //获取date日期的最结尾时间（23:59:59）
        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getSalesTop(beginTime, endTime);
        List<String> names = goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numbers = goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        //将List列表中的每个元素按逗号分隔、拼接，转换成一个字符串，转换成返回给Echart表格使用的数据类型
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(names, ","))
                .numberList(StringUtils.join(numbers, ","))
                .build();
    }

    /**
     * 导出Excel数据报表
     *
     * @param response
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        //查询前30天--昨天的经营数据
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);
        LocalDateTime beginTime = LocalDateTime.of(dateBegin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(dateEnd, LocalTime.MAX);
        //查询获得概览数据
        BusinessDataVO businessData = workspaceService.getBusinessData(beginTime, endTime);
        //创建excel对象
        InputStream templateInput = this.getClass().getClassLoader().getResourceAsStream("template/BusinessDataStatisticalReport.xlsx");
        try {
            assert templateInput != null;
            XSSFWorkbook excel = new XSSFWorkbook(templateInput);
            //获取第一个sheet页
            XSSFSheet sheet = excel.getSheetAt(0);
//            XSSFSheet sheet = excel.getSheet("sheet1");
            //设置概览数据
            //设置”时间“
            sheet.getRow(1).getCell(1).setCellValue("时间：" + dateBegin + "至" + dateEnd);
            //设置”营业额“
            sheet.getRow(3).getCell(2).setCellValue(businessData.getTurnover());
            //设置”订单完成率“
            sheet.getRow(3).getCell(4).setCellValue(businessData.getOrderCompletionRate());
            //设置”新增用户数“
            sheet.getRow(3).getCell(6).setCellValue(businessData.getNewUsers());
            //设置”有效订单“
            sheet.getRow(4).getCell(2).setCellValue(businessData.getValidOrderCount());
            //设置”平均客单价“
            sheet.getRow(4).getCell(4).setCellValue(businessData.getUnitPrice());
            //设置明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = dateBegin.plusDays(i);
                BusinessDataVO businessDataDetail = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                //设置”日期“
                sheet.getRow(7 + i).getCell(1).setCellValue(date.toString());
                //设置”营业额“
                sheet.getRow(7 + i).getCell(2).setCellValue(businessDataDetail.getTurnover());
                //设置”有效订单“
                sheet.getRow(7 + i).getCell(3).setCellValue(businessDataDetail.getValidOrderCount());
                //设置”订单完成率“
                sheet.getRow(7 + i).getCell(4).setCellValue(businessDataDetail.getOrderCompletionRate());
                //设置”平均客单价“
                sheet.getRow(7 + i).getCell(5).setCellValue(businessDataDetail.getUnitPrice());
                //设置”新增用户数“
                sheet.getRow(7 + i).getCell(6).setCellValue(businessDataDetail.getNewUsers());
            }
            //通过输出流将Excel文件下载到客户端浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);

            //关闭资源
            excel.close();
            outputStream.close();
            templateInput.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


}
