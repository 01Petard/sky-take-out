package com.sky.task;

import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

//    测试使用间隔：@Scheduled(cron = "0/10 * * * * ? ")

    /**
     * 每分钟查询并自动处理：支付超时的订单和派送时间过长的订单
     */
    @Scheduled(cron = "0 * * * * ? ")  //每分钟触发一次
//    @Scheduled(cron = "0/10 * * * * ? ")  //测试用
    public void processTimeoutOrders() {

        //处理支付超时的订单
        log.info("定时检测：开始定时处理超时订单: {}", LocalDateTime.now());
        //查询超时15分钟未支付的订单
        List<Orders> ordersTimeoutList = orderMapper.getTimeoutOrdersByStatus(Orders.PENDING_PAYMENT, LocalDateTime.now().plusMinutes(StatusConstant.ORDER_TIMEOUT_AUTO_CANCEL_TIME));
        //将超时支付的订单设置为取消
        if (ordersTimeoutList != null && ordersTimeoutList.size() > 0) {
            log.info("订单超时：发现需要处理的超时支付订单: 数量：{}，{}", ordersTimeoutList.size(), ordersTimeoutList);
            for (Orders orders : ordersTimeoutList) {
                log.info("订单超时：正在处理的超时支付订单: {}", orders.toString());
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason(MessageConstant.ORDER_TIMEOUT_AUTOCANCEL_REASON);
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
                log.info("订单超时：完成处理！订单id：{}，订单状态：取消", orders.getId());
            }
        } else {
            log.info("定时检测：无超时支付订单，下次检测时间：{}", LocalDateTime.now().plusMinutes(StatusConstant.ORDER_TIMEOUT_AUTO_CANCEL_TIME));
        }

        //处理派送时间过长的订单
        log.info("定时检测：开始定时处理正在派送中的订单: {}", LocalDateTime.now());
        //查询派送时间超过2小时的订单
        List<Orders> ordersDeliveringList = orderMapper.getDeliveringOrdersByStatus(Orders.DELIVERY_IN_PROGRESS, LocalDateTime.now().plusHours(StatusConstant.ORDER_TIMEOUT_AUTO_COMPLETE_TIME));
        //将正在派送中的订单设置为已完成
        if (ordersDeliveringList != null && ordersDeliveringList.size() > 0) {
            log.info("订单派送：发现需要处理的派送中订单: 数量：{}，{}", ordersDeliveringList.size(), ordersDeliveringList);
            for (Orders orders : ordersDeliveringList) {
                log.info("订单派送：正在处理的派送中订单: {}", orders);
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
                log.info("订单派送：完成派送！订单：{}，订单状态：完成", orders);
            }
        } else {
            log.info("定时检测：无派送中订单，下次检测时间：{}", LocalDateTime.now().plusHours(StatusConstant.ORDER_TIMEOUT_AUTO_COMPLETE_TIME));
        }
    }

    /**
     * 每天凌晨1点自动完成正在派送中的订单
     */
    @Scheduled(cron = "0 0 1 * * ? ")  //每天凌晨1点触发一次
//    @Scheduled(cron = "0 0 /2 * * ? ")  //每隔2小时触发一次
//    @Scheduled(cron = "0/10 * * * * ? ")  //测试用
    public void processDeliveringOrders() {
        log.info("定时检测：开始定时处理正在派送中的订单: {}", LocalDateTime.now());
        //查询正在派送中的订单
        List<Orders> ordersList = orderMapper.getDeliveringOrdersByStatus(Orders.DELIVERY_IN_PROGRESS, LocalDateTime.now().plusMinutes(-60));
        //将正在派送中的订单设置为已完成
        if (ordersList != null && ordersList.size() > 0) {
            log.info("订单派送：发现需要处理的派送中订单: 数量：{}，{}", ordersList.size(), ordersList);
            for (Orders orders : ordersList) {
                log.info("订单派送：正在处理的派送中订单: {}", orders);
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
                log.info("订单派送：完成派送！订单：{}，订单状态：完成", orders);
            }
        } else {
            log.info("定时检测：没有需要处理的派送中订单，本次检测结束，下次检测时间：{}", LocalDateTime.now().plusHours(StatusConstant.ORDER_TIMEOUT_AUTO_COMPLETE_TIME));
        }
    }

}