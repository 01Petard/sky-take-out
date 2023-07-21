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
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class DishServiceImpl implements DishService {

    @Resource
    private DishMapper dishMapper;
    @Resource
    private DishFlavorMapper dishFlavorMapper;
    @Resource
    private SetmealDishMapper setmealDishMapper;

    /**
     * 分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
//        long total = page.getTotal();
//        List<Category> records = page.getResult();
        return new PageResult(page.getTotal(), page.getResult());
    }


    /**
     * 新增菜品和对应的口味
     *
     * @param dishDTO
     */
    @Transactional
    @Override
    public void addDishWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dish.setStatus(StatusConstant.ENABLE); //默认添加菜品后立即起售
        //向菜品表插入1条菜品数据
        dishMapper.addDish(dish);
        //获取插入语句生成的主键值
        Long dishId = dish.getId();
        //向菜品口味表插入n条菜品口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.addBatchFlavor(flavors);
        }
    }

    /**
     * 停售起售菜品
     *
     * @param status
     * @param id
     */
    @Override
    public void changeDishStatus(Integer status, Long id) {
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        dishMapper.changeDishStatus(status, id, now, currentId);
    }

    /**
     * 批量删除菜品
     *
     * @param dishIds
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> dishIds) {
        //判断当前菜品是否处于起售状态
        for (Long dishId : dishIds) {
            Dish dish = dishMapper.getDishById(dishId);
            if (dish.getStatus().equals(StatusConstant.ENABLE)) {
                System.out.println("当前菜品：" + dish.getId() + "，" + dish.getName() + "，售卖状态：" + dishId);
                //当前菜品处于起售状态，不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断当前菜品是否被套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishId(dishIds);
        if (setmealIds != null && setmealIds.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品表中的菜品数据
        for (Long dishId : dishIds) {
            dishMapper.deleteDishById(dishId);
            //删除菜品id关联的口味id
            dishFlavorMapper.deleteFlavorByDishId(dishId);
        }

    }

    /**
     * 根据菜品id查询菜品和对饮的口味数据
     *
     * @param id
     * @return
     */
    @Override
    public DishVO getDishByIdWithFlavor(Long id) {
        //根据菜品id查询菜品
        Dish dish = dishMapper.getDishById(id);
        //根据菜品id查询口味
        List<DishFlavor> dishFlavors = dishFlavorMapper.getFlavorByDishId(id);
        //将查询到的数据封装到DishVO
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }


    /**
     * 修改菜品信息和对应的口味信息
     *
     * @param dishDTO
     */
    @Override
    public void updateDishWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //修改菜品表基本信息
        dishMapper.update(dish);
        //删除原有的口味信息
        dishFlavorMapper.deleteFlavorByDishId(dishDTO.getId());
        //再插入新的口味信息
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.addBatchFlavor(flavors);
        }

    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> getDishesByCategoryId(Long categoryId) {
        List<Dish> dishes = dishMapper.getDishesByCategoryId(categoryId);
        return dishes;
    }


}
