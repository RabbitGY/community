package com.gy.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
//@Aspect
public class AlphaAspect {
    //定义切点，织入到何处，所有service包里所有类的所有方法   ..表示所有参数
    @Pointcut("execution(* com.gy.community.service.*.*(..))")
    public void pointcut(){
    }

    //针对pointcut()注解的切点之前
    @Before("pointcut()")
    public void before(){
        System.out.println("before");
    }

    //针对pointcut()注解的切点之后
    @After("pointcut()")
    public void after(){
        System.out.println("after");
    }

    //针对pointcut()注解的切点,有返回值以后
    @AfterReturning("pointcut()")
    public void afterReturning(){
        System.out.println("afterReturning");
    }

    //针对pointcut()注解的切点,抛异常以后
    @AfterThrowing("pointcut()")
    public void afterThrowing(){
        System.out.println("afterThrowing");
    }

    //针对pointcut()注解的切点,前后都处理
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        //调目标组件的方法
        System.out.println("around before");
        Object o = joinPoint.proceed();
        System.out.println("around after");
        return o;
    }


}
