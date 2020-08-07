package org.marketcetera.log;

import java.lang.reflect.Method;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
// Marks the AOP functionality as the first one. Needed while logging happens also with AOP, and you make sure, that MDC information is set before any
// other AOP activities.
@Order(1)
@Service
@Aspect
public class MdcContextInitialiser
{
    private static final String BACKEND_FUNCTION_NAME = "backendFunctionName";
    @Around("methodsAnnoatatedWithMethodWithMdcContext()")
    public Object aroundAnnotatedMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        setMdcContextForMethod(joinPoint);
        return joinPoint.proceed();
    }
    @Around("classesAnnotatedWithClassWithMdcContext()")
    public Object aroundAnnotatedClass(ProceedingJoinPoint joinPoint) throws Throwable {
        setMdcContextForClass(joinPoint);
        return joinPoint.proceed();
    }
    @Pointcut(value = "@annotation(MethodWithMdcContext)")
    public void methodsAnnoatatedWithMethodWithMdcContext() {
        // defines pointcut for methods annotated with MethodWithMdcContext
    }
    @Pointcut("@within(ClassWithMdcContext)") // this should work for the annotation service pointcut
    private void classesAnnotatedWithClassWithMdcContext() {
        // defines pointcut for classes annotated with ClassWithMdcContext
    }
    private void setMdcContextForMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        MethodWithMdcContext annotation = method.getAnnotation(MethodWithMdcContext.class);
        String functionName = annotation.functionName();
        if (StringUtils.isBlank(functionName)) {
            functionName = getClassName(signature.getDeclaringTypeName()) + "_" + method.getName();
        }
        MDC.put(BACKEND_FUNCTION_NAME, "[" + functionName + "]");
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void setMdcContextForClass(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class clazz = signature.getDeclaringType();
        ClassWithMdcContext annotation = (ClassWithMdcContext) clazz.getAnnotation(ClassWithMdcContext.class);
        String functionName = annotation.functionName();
        if (StringUtils.isBlank(functionName)) {
            functionName = getClassName(signature.getDeclaringTypeName()) + "_" + signature.getMethod().getName();
        }
        MDC.put(BACKEND_FUNCTION_NAME, "[" + functionName + "]");
    }
    private String getClassName(String classFullName) {
        int startIndexOfClassName = StringUtils.lastIndexOf(classFullName, ".") + 1;
        return StringUtils.substring(classFullName, startIndexOfClassName);
    }
}
