package com.tenzo.seckill.service;

import com.tenzo.seckill.domain.Item;
import com.tenzo.seckill.domain.User;
import com.tenzo.seckill.mapper.ItemMapper;
import com.tenzo.seckill.mapper.UserMapper;
import com.tenzo.seckill.mq.MQueue;
import com.tenzo.seckill.result.Result;
import com.tenzo.seckill.utils.ResultUtil;
import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import redis.clients.jedis.Jedis;

import java.util.Random;
import java.util.Set;

/**
 * 用户服务层
 * 实现用户注册，登录，购买，拉取商品清单
 */
@Service
public class UserService extends LoginService{
    @Autowired
    UserMapper userMapper;

    @Autowired
    ItemMapper itemMapper;

    @Autowired
    OrderService orderService;

    MQueue mq = new MQueue();

    static Logger logger = LoggerFactory.getLogger(UserService.class);
    /**
     * 库存缓存主键前缀
     */
    private static final String STOCK_PREFIX = "_cachedStock";

    /**
     * 中奖率缓存主键前缀
     */
    private static final String RATE_PREFIX = "_cachedRate";

    private static final String CHANCE_PREFIX = "_cachedChance";

    private static final String ORDER_PREFIX = "_cachedOrder";

    private final Jedis jedis = new Jedis("localhost",6379);

    /**
     * 注册新用户
     * @param name 用户名
     * @param password 密码
     */
    @Transactional
    public void addUser(String name, String password) {
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        try {
            userMapper.add(user);
            logger.info(user.toString());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }


    /**
     * 修改用户信息
     * @param id 用户id
     * @param name 用户名
     * @param password 用户密码
     */
    @Transactional
    public void modifyUser(Integer id, String name, String password) {
        try {
            User user = userMapper.getById(id);
            user.setName(name);
            user.setPassword(password);
            userMapper.update(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 购买秒杀商品
     * @param id 商品id
     */
    @Transactional(rollbackFor=Exception.class)
    public Result purchase(int id) {

        String stockKey = STOCK_PREFIX + id;
        String rateKey = RATE_PREFIX + id;
        String chanceKey = CHANCE_PREFIX + id;
        int rate;
        int stock;
        int chance;
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,0);
//        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
//                .connectString("127.0.0.1:2181").retryPolicy(retryPolicy).build();
//        curatorFramework.start();
//
//        final String nodePath = "/lock_node";
//        final InterProcessMutex mutex = new InterProcessMutex(curatorFramework, nodePath);
        /**
         * 检查用户是否登录,在session中获取用户信息
         */
        User customer = getUser();
        if (customer == null) {
            logger.info("用户未登录尝试购买 id = "+id);
            return ResultUtil.error(0,"请先登录");
        }
        /**
         * 校验缓存数据是否被清空或过期
         */
        if (!jedis.exists(stockKey)) {
            //System.out.println("stock = null!");
            jedis.setex(stockKey,300L, String.valueOf(itemMapper.getById(id).getStock()));
        }
        //System.out.println("stock cache = "+ jedis.get(stockKey));
        if (!jedis.exists(rateKey)) {

            jedis.setex(rateKey, 300L, String.valueOf(itemMapper.getById(id).getRate()));
        }
        if (!jedis.exists(chanceKey)) {
            jedis.setex(chanceKey, 600L, String.valueOf(userMapper.getById(customer.getId()).getMaxChance()));
        }
        stock = Integer.parseInt(jedis.get(stockKey));
        rate = Integer.parseInt(jedis.get(rateKey));
        chance = Integer.parseInt(jedis.get(chanceKey));
        logger.info("正从缓存中获取库存,中奖率和中奖次数");


        //校验抽奖次数
        if (chance<1) {
            return ResultUtil.error(0,"抽奖次数已达上限~");
        }
        // 校验剩余库存
        if (stock<1) {
            System.out.println("real stock: "+ itemMapper.getById(id).getStock());
            System.out.println(Integer.parseInt(String.valueOf(itemMapper.getById(id).getStock())));
            System.out.println("Stock:" + stock);
            return ResultUtil.error(0,"商品已经被抢空了~");
        }
        // 校验是否抽中
        Random random = new Random();
        int randNum = random.nextInt(100);
        System.out.println(randNum);
        System.out.println("rate = " + rate);
        if (randNum>rate) {
            return ResultUtil.error(0,"请换个姿势再摇一次~");
        }
        try {
            /**
             * 执行数据库操作
             */
            //mutex.acquire();
            jedis.decr(stockKey);
            jedis.decr(chanceKey);
            int chanceCache =Integer.parseInt(jedis.get(chanceKey));
            System.out.println(chanceCache);
            Item item = itemMapper.getById(id);
            int originalStock = item.getStock();
            /**
             * 检查是否真实库存充足
             */
            if (originalStock<1) {
                return ResultUtil.error(0,"商品已被抢光~");
            }
            item.setStock(originalStock - 1);
            try {
                itemMapper.update(item);
                /**
                 * 生成对应订单信息, 扣减用户中奖次数
                 */
                User newCus = userMapper.getById(customer.getId());
                newCus.setMaxChance(chanceCache);
//                customer.setMaxChance(chanceCache);
//                System.out.println(customer.getMaxChance());
                userMapper.updateChance(newCus);
                orderService.setOrder(customer.getId(),id);
                // 缓存写入订单信息
                jedis.setex(ORDER_PREFIX + customer.getId(), 600L, String.valueOf(id));
                logger.info("用户"+customer.getId()+"成功抢购商品"+id);
                return ResultUtil.success("购买商品"+item+"成功");
            } catch (Exception e) {
                logger.error("更新数据库发生错误"+e.getMessage(),item);
                e.printStackTrace();
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultUtil.error(-1, "购买失败"+e.getMessage());
            }
        } catch (Exception e) {
            /**
             * 出现异常事务回滚
             * 补回缓存库存
             */
            jedis.incr(stockKey);
            jedis.incr(chanceKey);
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error(-1, "购买失败"+e.getMessage());
        } finally {
            try {
                //mutex.release();
                jedis.close();
            } catch (Exception e) {
                e.printStackTrace();
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultUtil.error(-1,"购买时出现问题"+e.getMessage());
            }
        }
    }

    /**
     * 拉取用户信息
     * @param id 用户id
     * @return
     */
    public User listInfo(int id) {
        try {
            return userMapper.getInfo(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 用户登录
     * @param name
     * @param password
     * @return
     */
    public Result login(String name, String password) {
        try {
            User user = userMapper.loginCheck(name, password);
            /**
             * 校验用户账户密码是否匹配
             */
            if (user!=null) {
                /**
                 * 将用户登录信息存入session
                 */
                setUser(user);
                logger.info("用户登录成功",name);
                return ResultUtil.success("登录成功：" + user);
            } else {
                logger.info("账户或密码错误", name, password);
                return ResultUtil.error(0, "用户名或密码错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("登录时发生错误"+e.getMessage(),name,password);
            return ResultUtil.error(-1,"登录失败"+e.getMessage());
        }
    }

    /**
     * 清空缓存
     */
    public void cleanCache() {
        try {
            Set<String> keySet = jedis.keys("*");
            for (String key:keySet) {
                jedis.del(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("清理缓存失败");
        }

    }
}
