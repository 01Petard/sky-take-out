package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;

@Mapper
public interface UserMapper {

    /**
     * 根据微信小程序的登录接口返回的openid，验证用户表中是否存在用户
     *
     * @param openId
     * @return
     */
    @Select("select * from user where openid = #{openId}")
    User getUserByOpenId(String openId);

    /**
     * 注册新用户
     *
     * @param user
     */
    void insert(User user);

    /**
     * 根据用户id查询用户
     *
     * @param userId
     * @return
     */
    @Select("select * from user where id=#{userId}")
    User getById(Long userId);

    /**
     * 统计指定时间内的用户量
     *
     * @param map
     * @return
     */
    Integer getUserCountByMap(HashMap<String, Object> map);

}
