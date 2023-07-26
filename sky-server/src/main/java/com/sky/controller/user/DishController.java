package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userUserController")
@RequestMapping("/user/dish")
@Api(tags = "用户端菜品相关接口")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 根据分类id查询菜品，菜品必须是“起售”状态
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {
        //构造redis中的key
        String key = "dish_" + categoryId;
        List<DishVO> list_redis = (List<DishVO>) redisTemplate.opsForValue().get(key);
        //查询Redis中是否存在菜品数据
        if (list_redis != null && list_redis.size() > 0) {
            //如果存在，直接返回，无须查询数据
            return Result.success(list_redis);
        }
        //如果不存在，查询数据库，将查询到的数据放入Redis中
        Category category = categoryService.getCategoryByCategoryId(categoryId);
        log.info("用户查询分类下的菜品：{}，{}", categoryId, category.getName());
        List<DishVO> list = dishService.listWithFlavor(categoryId);
        redisTemplate.opsForValue().set(key, list);
        return Result.success(list);
    }


}
