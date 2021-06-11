package com.tenzo.seckill.mapper;

import com.tenzo.seckill.domain.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper {

    /**
     * 插入订单信息
     * @param order
     */
    @Insert("insert into seckill.order (uid, iid) values(#{uid}, #{iid})")
    @Options(useGeneratedKeys=true, keyProperty="id")
    void createOrder(Order order);

    /**
     * 查询订单信息
     * @param id
     * @return
     */
    @Select("select uid, iid, create_time from order where id=#{id}")
    Order selectById(int id);
}
