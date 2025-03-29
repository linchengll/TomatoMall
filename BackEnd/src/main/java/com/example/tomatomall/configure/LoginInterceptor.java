package com.example.tomatomall.configure;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    TokenUtil tokenUtil;

    private static final List<String> WHITE_LIST = Arrays.asList(
            "/api/accounts/login",
            "/api/accounts",
            "/api/images"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();
        System.out.println("请求路径：" + requestURI);
        if (WHITE_LIST.contains(requestURI)) {
            return true;
        }

        String token = request.getHeader("token");
        System.out.println("token: "+token);
        if (token != null && tokenUtil.verifyToken(token)) {
            request.getSession().setAttribute("currentUser",tokenUtil.getUser(token));
            return true;
        }else {
            throw TomatoMallException.notLogin();
        }
    }

}
