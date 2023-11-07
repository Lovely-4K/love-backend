package com.lovely4k.backend.common.log;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    @Pointcut("execution(* com.lovely4k.backend..controller..*.*(..))")
    public void allController() { }

    @Pointcut("within(com.lovely4k.backend.common.GlobalExceptionHandler)")
    public void globalExceptionHandler() { }

}
