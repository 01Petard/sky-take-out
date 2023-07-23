package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询对应的几个套餐id
     * @param dishIds
     * @return
     */
    //SELECT * from setmeal_dish where dish_id in (1,2,3)
    List<Long> getSetmealIdsByDishId(List<Long> dishIds);

    /**
     * 根据套餐id删除所有对应的菜品
     * @param setmealId
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteDishesBySetmealId(Long setmealId);

    /**
     * 根据套餐id，批量插入对应的菜品
     * @param setmealDishes
     */
    void addBatchSetmealDishes(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id查询对应的菜品
     * @param setmealId
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getDishesBySetmealId(Long setmealId);

    /**
     * 根据套餐id删除关联的菜品
     * @param setmealId
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteDishBySetmealId(Long setmealId);

    /**
     * 根据套餐id查询套餐是否关联了菜品
     * @param setmealId
     * @return
     */
    @Select("select dish_id from setmeal_dish where setmeal_id = #{setmealId}")
    List<Long> getDishIdsBySetmealId(Long setmealId);
}
