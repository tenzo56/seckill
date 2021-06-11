package com.tenzo.seckill.service;

import com.tenzo.seckill.domain.User;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * 登录服务层
 * 实现将登录的用户信息通过session传递
 */
public class LoginService {
    private static final String USER = "user";

    /**
     * 获取请求的数据
     * @return
     */
    private HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }

    /**
     * 获取登录用户信息
     * @return
     */
    public User getUser() {
        HttpSession session = getRequest().getSession();
        return (User) session.getAttribute(USER);
    }

    /**
     * 设置已登录的用户信息
     * @param user
     */
    public void setUser(User user) {
        HttpSession session = getRequest().getSession();
        if (user != null) {
            session.setAttribute(USER, user);
            /**
             * 十分钟没有活动session失效
             */
            session.setMaxInactiveInterval(600);
        }
    }
}
