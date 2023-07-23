package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 新增套餐
     * @param setmealDTO
     */
    void addSetmealWithDishes(SetmealDTO setmealDTO);

    /**
     * 起售停售套餐
     * @param status
     * @param setmealId
     */
    void changeSetmealStatus(Integer status, Long setmealId);

    /**
     * 修改套餐
     * @param setmealDTO
     */
    void updateSetmealWithSetmealDish(SetmealDTO setmealDTO);

    /**
     * 根据套餐id查询套餐及对应的菜品
     * @param id
     * @return
     */
    SetmealVO getSetmealAndDishesBySetmealId(Long id);

    /**
     * 批量删除套餐
     * @param ids
     */
    void deleteBatchSetmeal(List<Long> ids);
}
