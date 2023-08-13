package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.core.annotation.Order;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * 插入订单数据
     *
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     *
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     *
     * @param orders
     */
    void update(Orders orders);

    /**
     * 根据订单状态和下单时间查询超时的订单
     * 每隔15分钟触发一次
     *
     * @return
     */
    @Select("select * from orders where  status=#{status} and order_time<#{orderTime}")
    List<Orders> getTimeoutOrdersByStatus(Integer status, LocalDateTime orderTime);

    /**
     * 根据订单状态查询正在派送中的订单
     *
     * @return
     */
    @Select("select * from orders where  status=#{status} and order_time<#{orderTime}")
    List<Orders> getDeliveringOrdersByStatus(Integer status, LocalDateTime orderTime);




}
