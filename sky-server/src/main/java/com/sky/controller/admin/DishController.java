package com.sky.controller.admin;

import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.User;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {

    @Autowired
    private DishService dishService;


    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 新增菜品
     *
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result addDish(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        dishService.addDishWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 删除菜品
     *
     * @return
     */
    @DeleteMapping
    @ApiOperation("菜品批量删除")
    public Result deleteDish(@RequestParam List<Long> ids) {  //注意：@RequestParam对应的参数必须和前端返回的参数一样，这里的ids不能改成其他名称
        log.info("菜品批量删除：{}", ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 根据id查询菜品和对应的口味，返回给前端用于修改菜品
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishVO> getDishById(@PathVariable Long id) {
        log.info("根据id查询菜品和对应的口味：{}", id);
        DishVO dishVO = dishService.getDishByIdWithFlavor(id);
        return Result.success(dishVO);
    }


    /**
     * 修改菜品信息
     *
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品信息")
    public Result updateDishInfo(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品信息：{}", dishDTO);
        dishService.updateDishWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 停售起售菜品
     *
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("停售起售菜品")
    public Result changeDishStatus(@PathVariable(value = "status") Integer status, Long id) {
        log.info("停售起售菜品，菜品状态：{}，菜品id：{}", status, id);
        dishService.changeDishStatus(status, id);
        return Result.success();
    }


    /**
     * 根据分类id查询菜品
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> getDishesByCategoryId(Long categoryId){
        log.info("根据分类id查询菜品：categoryId：{}", categoryId);
        List<Dish> dishes = dishService.getDishesByCategoryId(categoryId);
        return Result.success(dishes);
    }

}
