package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {

    /**
     * 分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 新增菜品
     * @param dish
     */
    @AutoFill(value = OperationType.INSERT)
    void addDish(Dish dish);


    /**
     * 修改菜品状态
     * @param status
     * @param id
     */
    @Update("update dish set status = #{status},update_time = #{now},update_user = #{currentId} where id = #{id}")
//    @AutoFill(value = OperationType.UPDATE)
    //不使用公共字段填充，使用最原始的办法修改字段
    void changeDishStatus(Integer status, Long id, LocalDateTime now, Long currentId);

    /**
     * 根据id返回菜品
     * @param dishId
     * @return
     */
    @Select("select * from dish where id = #{id}")
    Dish getDishById(Long dishId);

    /**
     * 根据id删除菜品
     * @param dishId
     */
    @Delete("delete from dish where id = #{id}")
    void deleteDishById(Long dishId);

    /**
     * 修改菜品信息
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Select("select * from dish where category_id = #{categoryId}")
    List<Dish> getDishesByCategoryId(Long categoryId);

    /**
     * 根据分类id查询菜品，用于返回给用户端，只能查询“起售”状态的菜品
     * @param categoryId
     * @return
     */
    @Select("select * from dish where category_id = #{categoryId} and status = 1")
    List<Dish> getDishesByCategoryId2User(Long categoryId);


    /**
     * 动态条件查询菜品
     * @param dish
     * @return
     */
    List<Dish> getDish(Dish dish);

    /**
     * 根据条件统计菜品数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);

}
