package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 套餐分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 新增菜品
     *
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void addSetmealWithDishes(SetmealDTO setmealDTO) {
        //插入当前的1条套餐
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmeal.setStatus(StatusConstant.DISABLE);
        setmealMapper.addSetmeal(setmeal);
        //获取套餐的id
        Long setmealId = setmeal.getId();
        //插入套餐对应的n道菜品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && setmealDishes.size() > 0) {
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealId);
            });
            setmealDishMapper.addBatchSetmealDishes(setmealDishes);
        } else {
            throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
        }
    }

    /**
     * 起售停售套餐
     *
     * @param status
     * @param setmealId
     */
    @Override
    public void changeSetmealStatus(Integer status, Long setmealId) {
        //查询套餐中是否有菜品，如果没有菜品，则不允许起售
        List<Long> dishIds = setmealDishMapper.getDishIdsBySetmealId(setmealId);
        if (dishIds != null && dishIds.size() > 0){
            Setmeal setmeal = Setmeal.builder()
                    .id(setmealId)
                    .status(status)
                    .build();
            setmealMapper.update(setmeal);
        }else {
            throw  new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
        }
    }

    /**
     * 修改套餐，以及套餐中的菜品
     * @param setmealDTO
     */
    @Override
    public void updateSetmealWithSetmealDish(SetmealDTO setmealDTO) {
        //获得套餐信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //修改套餐信息
        setmealMapper.update(setmeal);
        //先删除原来的套餐id对应的所有菜品，然后插入套餐id对应的所有菜品
        setmealDishMapper.deleteDishesBySetmealId(setmealDTO.getId());
        //获得套餐中的菜品列表
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && setmealDishes.size() > 0){
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealDTO.getId());
            });
            //批量插入套餐中的所有菜品
            setmealDishMapper.addBatchSetmealDishes(setmealDishes);
        }
    }

    /**
     * 根据套餐id查询套餐及对应的菜品
     * @param id
     * @return
     */
    @Override
    public SetmealVO getSetmealAndDishesBySetmealId(Long id) {
        //根据套餐id查询套餐
        Setmeal setmeal = setmealMapper.getSetmealById(id);
        //根据套餐id查询对应的菜品
        List<SetmealDish> setmealDishes = setmealDishMapper.getDishesBySetmealId(id);
        //将套餐和套餐对应的菜品封装成VO对象
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 批量删除套餐
     * @param setmealIds
     */
    @Override
    public void deleteBatchSetmeal(List<Long> setmealIds) {
        //判断当前菜品是否处于停售状态，若处于起售状态则不允许删除
        for (Long setmealId : setmealIds) {
            Setmeal setmeal = setmealMapper.getSetmealById(setmealId);
            if (setmeal.getStatus().equals(StatusConstant.ENABLE)){
                //不允许删除
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        //不需要判断套餐是否关联了菜品，直接级联删除即可
        for (Long setmealId : setmealIds) {
            //删除套餐和套餐关联的菜品
            setmealMapper.deleteSetmealById(setmealId);
            //删除套餐关联的菜品
            setmealDishMapper.deleteDishBySetmealId(setmealId);
        }
    }
}
