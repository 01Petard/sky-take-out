package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
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

}
