package com.hellozjf.learn.springboot2.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author Jingfeng Zhou
 */
@Component
@Aspect
@Slf4j
public class ControllerAspect {

    @Around("execution(* com.hellozjf.learn.springboot2.controller.TestController.*(..))")
    public Object testControllerCost(ProceedingJoinPoint invocation) throws Throwable {
        return calcTime(invocation);
    }

    @Around("execution(* org.slf4j.Logger.*(..))")
    public Object testLoggerCost(ProceedingJoinPoint invocation) throws Throwable {
        return calcTime(invocation);
    }

    private Object calcTime(ProceedingJoinPoint invocation) throws Throwable {
        long t1 = System.currentTimeMillis();
        Object result = invocation.proceed();
        long t2 = System.currentTimeMillis();
        log.debug("{} cost {}", invocation.getSignature(), t2 - t1);
        return result;
    }
}
