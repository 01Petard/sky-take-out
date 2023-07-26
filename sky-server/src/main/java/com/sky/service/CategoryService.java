package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {
    /**
     * 修改分类的启用或禁用
     * @param status
     * @param id
     */
    void changeStatus(Integer status, Long id);

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 新增分类
     * @param categoryDTO
     */
    void addCateGory(CategoryDTO categoryDTO);

    /**
     * 修改分类信息
     * @param categoryDTO
     */
    void update(CategoryDTO categoryDTO);

    /**
     * 删除分类
     * @param id
     */
    void delete(Long id);


    /**
     * 根据类型查询分类
     * @param type
     */
    List<Category> list(Integer type);

    /**
     * 根据分类id返回分类名称
     * @param categoryId
     * @return
     */
    Category getCategoryByCategoryId(Long categoryId);
}
