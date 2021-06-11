package com.tenzo.seckill.mapper;

import com.tenzo.seckill.domain.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Insert("insert into user (name, password) values(#{name}, #{password})")
    void add(User user);

    @Update("update user set name=#{name}, password=#{password}, max_chance = #{maxChance}, version=#{version} + 1 where id=#{id} and version = #{version}")
    void update(User user);

    @Update("update user set max_chance= #{maxChance}, version=#{version} + 1 where id=#{id} and version = #{version}")
    void updateChance(User user);

    @Update("update user set is_delete=1, version=#{version} + 1 where id=#{id} and version = #{version}")
    void delete(User user);

    @Select("select * from user where id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "password", column = "password"),
            @Result(property = "version", column = "version"),
            @Result(property = "maxChance", column = "max_chance"),
            @Result(property = "level", column = "level"),
            @Result(property = "isDelete", column = "is_delete")
    })
    User getById(@Param(value = "id") int id);

    @Update("update user set max_chance=#{maxChance}, version=#{version} + 1 where version = #{version}")
    void changeChanceByLevel(User user);

    @Select("select * from user where level=#{level}")
    List<User> getByLevel(@Param(value = "level") int level);

    @Update("update user set level=#{level}, version=#{version} + 1 where id=#{id} and version = #{version}")
    void setLevel(User user);

    @Select("select * from user where id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "password", column = "password"),
            @Result(property = "level", column = "level"),
    })
    User getInfo(@Param(value = "id") int id);

    @Select("select * from user where name = #{name} and password=#{password} and is_delete=0")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "password", column = "password"),
            @Result(property = "maxChance", column = "max_chance"),
            @Result(property = "level", column = "level")
    })
    User loginCheck(@Param(value = "name") String name, @Param(value = "password") String password);
}
