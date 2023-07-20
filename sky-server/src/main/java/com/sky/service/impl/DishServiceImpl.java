package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import com.sky.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class DishServiceImpl implements DishService {

    @Resource
    private DishMapper dishMapper;

    /**
     * 分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {

        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<Dish> page = dishMapper.pageQuery(dishPageQueryDTO);
//        long total = page.getTotal();
//        List<Category> records = page.getResult();
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 停售起售菜品
     * @param status
     * @param id
     */
    @Override
    public void changeDishStatus(Integer status, Long id) {
        dishMapper.changeDishStatus(status, id);
    }

    /**
     * 修改菜品信息
     * @param dishDTO
     */
    @Override
    public void updateDishInfo(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

//        dish.setCreateTime(LocalDateTime.now());
//        dish.setUpdateTime(LocalDateTime.now());
//        dish.setCreateUser(BaseContext.getCurrentId());
//        dish.setUpdateUser(BaseContext.getCurrentId());

        dishMapper.updateDishInfo(dish);
    }


}
