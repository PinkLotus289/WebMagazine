package com.example.restservice.aspect;

import com.example.restservice.service.VisitCounterService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ProductVisitAspect {

    private final VisitCounterService visitCounterService;

    public ProductVisitAspect(VisitCounterService visitCounterService) {
        this.visitCounterService = visitCounterService;
    }

    @Pointcut("execution(* com.example.restservice.controller.ProductController."
            + "getAllProducts(..))")
    public void productVisitPointcut() {}

    @Before("productVisitPointcut()")
    public void incrementCounter() {
        visitCounterService.increment();
    }
}

