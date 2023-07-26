package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 根据微信小程序的登录接口返回的openid，验证用户表中是否存在用户
     * @param openId
     * @return
     */
    @Select("select * from user where openid = #{openId}")
    User getUserByOpenId(String openId);

    /**
     * 注册新用户
     * @param user
     */
    void insert(User user);
}
