package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {
    /**
     * 分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 启用/禁用分类
     *
     * @param category
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Category category);

    /**
     * 添加新的分类
     *
     * @param category
     */
    @Insert("insert into category (type, name, sort, status, create_time, update_time, create_user, update_user) " +
            "value (#{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    void addCategory(Category category);

    /**
     * 根据id删除分类
     * @param id
     */
    @Delete("delete from category where id = #{id}")
    void delete(Long id);

    /**
     * 根据类型id查询分类
     * @param type
     * @return
     */
    @Select("select * from category where status = 1 and type = #{type} order by sort asc, create_time desc")
    List<Category> list(Integer type);

    /**
     * 根据类型id查询分类，此方法为type==null时的应对办法
     * @param type
     * @return
     */
    @Select("select * from category where status = 1 order by sort asc, create_time desc")
    List<Category> listAll(Integer type);

    /**
     * 根据分类id返回分类名称
     * @param categoryId
     * @return
     */
    @Select("select * from category where id = #{categoryId}")
    Category getCategoryByCategoryId(Long categoryId);
}
