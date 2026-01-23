package com.example.tomatomall.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("@annotation(com.example.tomatomall.annotation.InterfaceLog)")
    public void controllerPointcut() {
    }
    @Before("controllerPointcut()")
    public void logBefore(JoinPoint joinPoint) {
        HttpServletRequest request = getHttpServletRequest();
        if (request != null) {
            log.info("=== 请求开始 ===");
            log.info("请求URL: {} {}", request.getMethod(), request.getRequestURI());

            String queryString = request.getQueryString();
            if (queryString != null && !queryString.isEmpty()) {
                log.info("查询参数: {}", queryString);
            }

            log.info("客户端IP: {}", getClientIpAddress(request));
            log.info("用户代理: {}", request.getHeader("User-Agent"));
        }

        log.info("调用方法: {}.{}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());

        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            log.info("方法参数: {}", Arrays.toString(args));
        } else {
            log.info("方法参数: 无");
        }
    }

    @AfterReturning(pointcut = "controllerPointcut()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("方法返回: {}", result != null ? result.toString() : "null");
        log.info("=== 请求结束 ===");
    }

    @AfterThrowing(pointcut = "controllerPointcut()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        log.error("方法异常: {}.{} - {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                exception.getMessage());
        log.error("异常堆栈: ", exception);
        log.info("=== 请求异常结束 ===");
    }

    @Around("controllerPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("方法执行时间: {}ms", executionTime);
            return result;
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("方法执行异常，耗时: {}ms", executionTime);
            throw throwable;
        }
    }

    private HttpServletRequest getHttpServletRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}