package com.tenzo.seckill.utils;

import com.tenzo.seckill.enums.ResultEnum;
import com.tenzo.seckill.result.Result;

public class ResultUtil {

    /**
     * 成功且带数据
     * @param object
     * @return
     */
    public static Result success(Object object) {
        Result result = new Result();
        result.setCode(ResultEnum.SUCCESS.getCode());
        result.setMsg(ResultEnum.SUCCESS.getMsg());
        result.setData(object);
        return result;
    }

    /**
     * 成功且不带数据
     * @return
     */
    public static Result success() {
        return success(null);
    }

    /**
     * 出现错误失败
     * @param code
     * @param msg
     * @return
     */
    public static Result error(Integer code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
