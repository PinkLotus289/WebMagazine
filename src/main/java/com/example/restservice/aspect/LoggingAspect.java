package com.example.restservice.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Pointcut("execution(* com.example.restservice.service.*.*(..))")
    public void serviceMethods() {}

    @Pointcut("execution(* com.example.restservice.controller.*.*(..))")
    public void controllerMethods() {}

    @Before("serviceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Вызов метода: {}", joinPoint.getSignature());
    }

    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        logger.info("Метод {} завершился успешно. Результат: {}",
                joinPoint.getSignature(), result);
    }

    @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        logger.error("Метод {} выбросил исключение: {}",
                joinPoint.getSignature(), ex.getMessage(), ex);
    }

    @Before("controllerMethods()")
    public void logBeforeController(JoinPoint joinPoint) {
        logger.info("➡️ Контроллер вызван: {}", joinPoint.getSignature());
    }

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfterController(JoinPoint joinPoint, Object result) {
        logger.info("✅ Контроллер {} успешно отработал. Результат: {}",
                joinPoint.getSignature(), result);
    }

    @AfterThrowing(pointcut = "controllerMethods()", throwing = "ex")
    public void logControllerException(JoinPoint joinPoint, Throwable ex) {
        logger.error("❌ Контроллер {} выбросил исключение: {}",
                joinPoint.getSignature(), ex.getMessage(), ex);
    }

}

