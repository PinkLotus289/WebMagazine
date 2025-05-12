package com.example.restservice.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


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
        logger.info("Метод {} завершился успешно. Результат: {}", joinPoint.getSignature(), result);
    }

    @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        logger.error("Метод {} выбросил исключение: {}", joinPoint.getSignature(),
                ex.getMessage(), ex);
    }

    @Before("controllerMethods()")
    public void logBeforeController(JoinPoint joinPoint) {
        logger.info("➡️ Контроллер вызван: {}", joinPoint.getSignature());
    }

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfterController(JoinPoint joinPoint, Object result) {
        if (!(result instanceof String)
                || !joinPoint.getSignature().toShortString().contains("LogController")) {
            logger.info("✅ Контроллер {} отработал. Результат: {}",
                    joinPoint.getSignature(), result);
        } else {
            logger.info("✅ Контроллер {} отработал. (результат лог-файл, тело не логируем)",
                    joinPoint.getSignature());
        }
    }

    @AfterThrowing(pointcut = "controllerMethods()", throwing = "ex")
    public void logControllerException(JoinPoint joinPoint, Throwable ex) {
        logger.error("❌ Контроллер {} выбросил исключение: {}", joinPoint.getSignature(),
                ex.getMessage(), ex);
    }

    @Around("controllerMethods()")
    public Object logHttpStatusAndTiming(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();

        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;

            int status;
            if (result instanceof ResponseEntity<?> entity) {
                status = entity.getStatusCode().value();
            } else {
                status = 200;
            }

            if (!(result instanceof String) || !uri.equals("/logs")) {
                logger.info("📥 {} {} от IP {} ➡️ статус: {} | время: {} мс", method, uri,
                        ip, status, duration);
            } else {
                logger.info("📥 {} {} от IP {} ➡️ статус: {} | время: {} мс (возврат лог-файла, "
                                + "тело не логируем)",
                        method, uri, ip, status, duration);
            }

            return result;
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - start;
            logger.error("❌ Ошибка при выполнении {} {}: {} | время: {} мс",
                    method, uri, ex.getMessage(), duration, ex);
            throw new RuntimeException("Ошибка при выполнении запроса " + method + " " + uri, ex);
        }
    }

}

