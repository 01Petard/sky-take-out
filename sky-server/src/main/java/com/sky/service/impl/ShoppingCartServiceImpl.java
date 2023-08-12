package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //获得当前用户的id
        Long userId = BaseContext.getCurrentId();
        //封装前端接收的购物车数据
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(userId);
        //根据用户id查询商品是否已经在购物车中
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(shoppingCart);
        //判断当前商品是否已经在购物车中
        if (shoppingCarts != null && shoppingCarts.size() > 0) {
            //如果存在了，数量加一
            ShoppingCart cart = shoppingCarts.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(cart);
        } else {
            //如果不存在，需要插入一条购物车数据
            //判断本次添加到购物车的是菜品还是套餐
            Long dishId = shoppingCartDTO.getDishId();
            if (dishId != null) {
                //本次添加的是菜品
                Dish dish = dishMapper.getDishById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            } else {
                //本次添加的是套餐
                Long setmealId = shoppingCartDTO.getSetmealId();
                Setmeal setmeal = setmealMapper.getSetmealById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }

    }

    /**
     * 查看购物车
     *
     * @return
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {
        //获得当前用户的id
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build();
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        return list;
    }

    /**
     * 清空购物车
     */
    @Override
    public void cleanShoppingCart() {
        //获得当前用户的id
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);
    }

    /**
     * 减少购物车
     */
    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //获得当前用户的id
        Long userId = BaseContext.getCurrentId();
        //封装前端接收的购物车数据
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(userId);
        //查询该商品，获得商品的数量
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(shoppingCart);
        //获得将商品的信息，将商品的数量减一，最后保存到数据库
        ShoppingCart cart = shoppingCarts.get(0);
        if (cart.getNumber() > 1){
            //如果减去商品时数量大于1，就将商品数量减一
            cart.setNumber(cart.getNumber() - 1);
            shoppingCartMapper.updateNumberById(cart);
        }
        else if (cart.getNumber() == 1) {
            //如果减去商品时数量为1，就将该商品从购物车删除
            if (shoppingCartDTO.getSetmealId() == null){  //如果商品类型是”菜品“，就删除该菜品
                shoppingCartMapper.deleteByDishId(userId, shoppingCartDTO.getDishId());
            }else {   //如果商品类型是”套餐“，就删除该套餐
                shoppingCartMapper.deleteBySetmealId(userId, shoppingCartDTO.getSetmealId());
            }
        }


    }
}
