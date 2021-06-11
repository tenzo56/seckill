package com.tenzo.seckill.service;

import com.tenzo.seckill.domain.Item;
import com.tenzo.seckill.domain.User;
import com.tenzo.seckill.mapper.ItemMapper;
import com.tenzo.seckill.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AdminService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    ItemMapper itemMapper;
    /**
     * 库存缓存主键前缀
     */
    private static final String STOCK_PREFIX = "_cachedStock";

    /**
     * 中奖率缓存主键前缀
     */
    private static final String RATE_PREFIX = "_cachedRate";
    /**
     * 最大中奖次数缓存主键前缀
     */
    private static final String CHANCE_PREFIX = "_cachedChance";
    // 配置redis服务
    private Jedis jedis = new Jedis("localhost",6379);

    /**
     * 根据用户等级更改最大中奖次数
     * @param level 用户等级
     * @param chance 最大中奖次数
     */
    @Transactional
    public void changeMaxChance(Integer level, Integer chance) {
        try {
            List<User> userList = userMapper.getByLevel(level);
            for (User user : userList) {
                user.setMaxChance(chance);
                userMapper.changeChanceByLevel(user);
                jedis.setex(CHANCE_PREFIX + user.getId() + level, 600L, String.valueOf(chance));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 插入新商品
     * @param name 商品名
     * @param description 商品描述
     * @param originalPrice 商品原价
     * @param promotePrice 商品促销价
     * @param stock 商品库存
     * @param rate 商品中奖率
     * @return
     */
    @Transactional
    public Item insert(String name, String description, BigDecimal originalPrice, BigDecimal promotePrice, Integer stock, Integer rate) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setOriginalPrice(originalPrice);
        item.setPromotePrice(promotePrice);
        item.setStock(stock);
        item.setRate(rate);
        try {
            itemMapper.insert(item);
            jedis.setex(STOCK_PREFIX + item.getId(), 30L, String.valueOf(stock));
            jedis.setex(RATE_PREFIX + item.getId(), 600L, String.valueOf(rate));
        } catch (Exception e) {
            e.printStackTrace();
            //logger.error(e.getMessage());
        }
        return item;
    }

    /**
     * 更改特定商品中奖率
     * @param id 商品id
     * @param rate 中奖率
     */
    @Transactional
    public void changeRate(int id, int rate) {
        try {
            Item item = itemMapper.getById(id);
            item.setRate(rate);
            itemMapper.setRate(item);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除商品
     * 采用软删除，可恢复数据
     * @param id 商品id
     */
    @Transactional
    public void deleteUser(Integer id) {
        try {
            userMapper.delete(userMapper.getById(id));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置用户级别
     * @param id 用户id
     * @param level 用户级别
     */
    @Transactional
    public void setLevel(Integer id, Integer level) {
        try {
            User user = userMapper.getById(id);
            user.setLevel(level);
            userMapper.setLevel(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
