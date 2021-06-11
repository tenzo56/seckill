package com.tenzo.seckill.mapper;

import com.tenzo.seckill.domain.Item;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 商品映射文件
 * 负责商品对象与数据库的交互
 * CRUD
 */
@Mapper
public interface ItemMapper {
    /**
     * 插入新商品
     * @param item
     */
    @Insert("insert into item (name, description, original_price, promote_price, stock, " +
            "rate) values(#{name}, #{description}, " +
            "#{originalPrice}, #{promotePrice}, #{stock}, #{rate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Item item);

    @Delete("delete from item where id=#{id}")
    void deleteById(int id);

    @Select("select * from item where is_delete=0")
    List<Item> findAll();

    @Select("select * from item where id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "version", column = "version"),
            @Result(property = "description", column = "description"),
            @Result(property = "originalPrice", column = "original_price"),
            @Result(property = "promotePrice", column = "promote_price"),
            @Result(property = "stock", column = "stock"),
            @Result(property = "rate", column = "rate")
    })
    Item getById(@Param(value = "id") int id);

    @Update("update item set name=#{name}, version=#{version} + 1, " +
            "description = #{description}, original_price=#{originalPrice}, promote_price=#{promotePrice}, stock=#{stock}, rate=#{rate} where id=#{id} and version = #{version}")
    void update(Item item);

    @Update("update item set is_delete=1, version=#{version} + 1 where id=#{id} and version = #{version}")
    void setDelete(Item item);

    @Update("update item set rate=#{rate}, version=#{version} + 1 where id=#{id} and version = #{version}")
    void setRate(Item item);

//    @Update("update ")
//    void setChanceById(@Param(value = "id") Integer id, @Param(value = "chance") Integer chance);
}
