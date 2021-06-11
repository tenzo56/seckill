package com.tenzo.seckill.controller;

import com.tenzo.seckill.domain.Item;
import com.tenzo.seckill.result.Result;
import com.tenzo.seckill.service.AdminService;
import com.tenzo.seckill.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/admin")
public class AdminController {

    /**
     * 自动装配admin service类
     */
    @Autowired
    AdminService adminService;

    /**
     * 根据用户会员等级设置最大中奖机会
     * @param level
     * @param chance
     * @return
     */
    @RequestMapping("/setChance")
    public Result setChanceByLevel(int level, int chance) {
        try {
            adminService.changeMaxChance(level, chance);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error(-1,"设置最大中奖次数失败"+e.getMessage());
        }
        return ResultUtil.success();
    }

    /**
     * 设置商品中奖率
     * @param id
     * @param rate
     * @return
     */
    @RequestMapping("/setRate")
    public Result setRate(int id, int rate) {
        try {
            adminService.changeRate(id, rate);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error(-1, "设置商品中奖率失败"+e.getMessage());
        }
        return ResultUtil.success();
    }

    @RequestMapping("/deleteUser")
    public Result deleteUser(int id) {
        try{
            adminService.deleteUser(id);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error(-1, "删除用户失败"+e.getMessage());
        }
        return ResultUtil.success();
    }

    @RequestMapping("/addItem")
    public Result addItem(String name, String description, BigDecimal originalPrice, BigDecimal promotePrice, Integer stock, Integer rate) {
        try {
            Item item = adminService.insert(name, description, originalPrice, promotePrice, stock, rate);
            return ResultUtil.success(item);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error(-1, "添加商品失败"+e.getMessage());
        }
    }

    @RequestMapping("/setLevel")
    public Result setLevel(int id, int level) {
        try {
            adminService.setLevel(id, level);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error(-1, "设置用户等级失败"+e.getMessage());
        }
        return ResultUtil.success();
    }
}
