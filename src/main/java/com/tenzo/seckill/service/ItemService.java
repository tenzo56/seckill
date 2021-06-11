package com.tenzo.seckill.service;

import com.tenzo.seckill.domain.Item;
import com.tenzo.seckill.mapper.ItemMapper;
import com.tenzo.seckill.result.Result;
import com.tenzo.seckill.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商家服务端
 * 完成商品增改删查
 */
@Service
public class ItemService {

    @Autowired
    private ItemMapper itemMapper;


    private RedisService redisService;

    private Jedis jedis = redisService.getJedis();

    static Logger logger = LoggerFactory.getLogger(UserService.class);
    /**
     * 库存缓存主键前缀
     */
    private static final String STOCK_PREFIX = "_cachedStock";

    /**
     * 中奖率缓存主键前缀
     */
    private static final String RATE_PREFIX = "_cachedRate";
    //private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

    //RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,1);
//        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
//                .connectString("127.0.0.1:2181").retryPolicy(retryPolicy).build();
//        curatorFramework.start();
//
//        final String nodePath = "/lock_node";
//        final InterProcessMutex mutex = new InterProcessMutex(curatorFramework, nodePath);

    /**
     * 创建新商品
     * @param name 商品名
     * @param description 商品描述
     * @param originalPrice 商品原价
     * @param promotePrice 商品促销价
     * @param stock 商品库存
     * @param rate 商品中奖率
     * @return 创建的新商品实体对象
     */
    @Transactional
    public Result insert(String name, String description, BigDecimal originalPrice, BigDecimal promotePrice, Integer stock, Integer rate) {
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
            logger.info("添加新商品",item);
            return ResultUtil.success(item);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("添加商品失败"+e.getMessage(),item);
            return ResultUtil.error(-1,"加入新商品失败"+e.getMessage());
        }
    }


    /**
     * 修改商品信息
     * @param id 商品id
     * @param name 商品名称
     * @param description 商品描述
     * @param originalPrice 商品原价
     * @param promotePrice 商品促销价
     * @param stock 商品库存
     * @param rate 商品中奖率
     */
    @Transactional
    public Result modify(Integer id, String name, String description, BigDecimal originalPrice, BigDecimal promotePrice, Integer stock, Integer rate) {
        Item item = itemMapper.getById(id);
        item.setName(name);
        item.setDescription(description);
        item.setOriginalPrice(originalPrice);
        item.setPromotePrice(promotePrice);
        item.setRate(rate);
        item.setStock(stock);
        try {
            itemMapper.update(item);
            logger.info("修改商品信息成功",item);
            return ResultUtil.success(item);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("修改商品信息失败"+e.getMessage(),item);
            return ResultUtil.error(-1, "修改商品失败"+e.getMessage());
        }
    }


    /**
     * 查找对应id的商品
     * @param id 商品id
     * @return 对应商品实体对象
     */
    public Result getById(Integer id) {
        try {
            Item item = itemMapper.getById(id);
            logger.info("获取商品信息成功",item);
            return ResultUtil.success(item);
        } catch (Exception e) {
            logger.error("获取商品信息失败"+e.getMessage(),id);
            return ResultUtil.error(-1, "获取商品失败"+e.getMessage());
        }
    }

    /**
     * 返回所有商品
     * @return 商品列表
     */
    public Result getAll() {
        try {
            List<Item> itemList = itemMapper.findAll();
            logger.info("获取商品列表成功", itemList);
            return ResultUtil.success(itemList);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取全部商品信息失败"+e.getMessage());
            return ResultUtil.error(-1, "获取商品列表失败"+e.getMessage());
        }
    }

    /**
     * 通过商品id删除商品
     * 软删除实现，可以恢复数据
     * @param id 商品id
     */
    @Transactional
    public Result deleteById(Integer id) {
        try {
            Item item = itemMapper.getById(id);
            itemMapper.setDelete(item);
            logger.info("删除商品成功",item);
            return ResultUtil.success(item);
        } catch (Exception e) {
            logger.error("删除对应商品失败",id);
            e.printStackTrace();
            return ResultUtil.error(-1,"删除商品失败"+e.getMessage());
        }
    }
}
