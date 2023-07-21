package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 新增菜品和对应的口味数据
     *
     * @param dishDTO
     */
    void addDishWithFlavor(DishDTO dishDTO);

    /**
     * 修改菜品状态
     *
     * @param status
     * @param id
     */
    void changeDishStatus(Integer status, Long id);

    /**
     * 批量删除菜品
     *
     * @param dishIds
     */
    void deleteBatch(List<Long> dishIds);

    /**
     * 根据菜品id查询菜品和对饮的口味数据
     *
     * @param id
     * @return
     */
    DishVO getDishByIdWithFlavor(Long id);

    /**
     * 修改菜品信息和对应的口味信息
     *
     * @param dishDTO
     */
    void updateDishWithFlavor(DishDTO dishDTO);

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    List<Dish> getDishesByCategoryId(Long categoryId);
}
