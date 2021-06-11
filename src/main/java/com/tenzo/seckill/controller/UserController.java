package com.tenzo.seckill.controller;

import com.tenzo.seckill.result.Result;
import com.tenzo.seckill.service.ItemService;
import com.tenzo.seckill.service.UserService;
import com.tenzo.seckill.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消费者端口
 * 实现注册账户，修改账户信息，查看账户信息以及购买商品的功能
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    ItemService itemService;

    @Autowired
    KafkaTemplate kafkaTemplate;
    /**
     * 注册用户
     * @param name 用户名
     * @param password 密码
     */
    @ResponseBody
    @RequestMapping("/add")
    public Result add(String name, String password) {
        try {
            userService.addUser(name, password);
        } catch (Exception e) {
            e.printStackTrace();
            ResultUtil.error(-1,"注册用户失败"+e.getMessage());
        }
        return ResultUtil.success();
    }

    /**
     * 购买商品
     * @param id 商品id
     */
    @ResponseBody
    @RequestMapping("/purchase")
    @Transactional(rollbackFor=Exception.class)
    public Result purchase(int id) {
        try {
            return userService.purchase(id);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error(-1,"购买商品失败"+e.getMessage());
        }
    }

    /**
     * 修改用户信息
     * @param id 用户id
     * @param name 用户名
     * @param password 用户密码
     */
    @ResponseBody
    @RequestMapping("/modify")
    public Result modify(int id, String name, String password) {
        try {
            userService.modifyUser(id, name, password);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error(-1, "修改账户信息失败"+e.getMessage());
        }
        return ResultUtil.success();
    }

    /**
     * 查看账户信息
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("/listInfo")
    public Result listInfo(int id) {
        try {
            return ResultUtil.success(userService.listInfo(id));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error(-1,"查看账户信息失败"+e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping("/login")
    public Result login(String name, String password) {
        try {
            return userService.login(name, password);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error(-1, "登陆时出现错误"+e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping("/clean")
    public Result clearCache() {
        try {
            userService.cleanCache();
            return ResultUtil.success("缓存清理成功");
        } catch (Exception e) {
            return ResultUtil.error(-1,"清理缓存发生错误");
        }
    }
}
