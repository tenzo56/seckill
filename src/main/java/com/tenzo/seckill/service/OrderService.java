package com.tenzo.seckill.service;

import com.tenzo.seckill.domain.Order;
import com.tenzo.seckill.mapper.OrderMapper;
import com.tenzo.seckill.result.Result;
import com.tenzo.seckill.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

/**
 * 订单服务层
 * 完成了订单的创建
 */
@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    private final String ORDER_PREFIX = "_chachedOrder";

    private final Jedis jedis = new Jedis("localhost",6379);


    @Transactional
    public Result setOrder(int uid, int iid) {
        try {
            Order order = new Order();
            order.setUid(uid);
            order.setIid(iid);
            //order.setCreateTime(new Date());
            orderMapper.createOrder(order);
            jedis.setex(ORDER_PREFIX+order.getId(),600L, order.toString());
            return ResultUtil.success(order);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error(-1,"创建订单失败");
        }
    }
}
