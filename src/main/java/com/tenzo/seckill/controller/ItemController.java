package com.tenzo.seckill.controller;

import com.tenzo.seckill.result.Result;
import com.tenzo.seckill.service.ItemService;
import com.tenzo.seckill.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @ResponseBody
    @RequestMapping("/insert")
    public Result insert(String name, String description, BigDecimal originalPrice, BigDecimal promotePrice, Integer stock, Integer rate)
    {
        try {
            return itemService.insert(name, description, originalPrice, promotePrice, stock, rate);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error(-1,"添加商品失败"+e.getMessage());
        }
    }


    @ResponseBody
    @RequestMapping("/modify")
    public Result modify(Integer id, String name, String description, BigDecimal originalPrice, BigDecimal promotePrice, Integer stock, Integer rate) {
        try {
            itemService.modify(id, name, description, originalPrice, promotePrice, stock, rate);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error(-1,"修改商品信息失败"+e.getMessage());
        }
        return ResultUtil.success();
    }

    @ResponseBody
    @RequestMapping("/delete")
    public Result delete(Integer id) {
        try {
            return itemService.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error(-1,"删除商品失败"+e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping("/get")
    public Result getById(Integer id) {
        try {
            return ResultUtil.success(itemService.getById(id));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error(-1,"获取商品信息失败"+e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping("/list")
    public Result listAll() {
        try {
            return ResultUtil.success(itemService.getAll());
        } catch (Exception e) {
            return ResultUtil.error(-1,"拉取商品清单失败"+e.getMessage());
        }
    }
}
