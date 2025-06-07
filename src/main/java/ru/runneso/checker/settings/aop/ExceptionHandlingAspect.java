package ru.runneso.checker.settings.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.runneso.checker.domain.exceptions.BaseApplicationException;
import ru.runneso.checker.domain.exceptions.UndefinedException;
import ru.runneso.checker.domain.exceptions.ValidationException;

@Aspect
@Component
public class ExceptionHandlingAspect {
    @Around("@annotation(ru.runneso.checker.settings.aop.HandleServiceExceptions)")
    public Object wrapWithTryCatch(ProceedingJoinPoint pjp) throws Throwable {
        try {
            return pjp.proceed();
        }
        catch (BaseApplicationException error) {
            if (error instanceof UndefinedException) {
                System.out.println("Undefined Exception");
                System.out.println(error.getMessage());
                return error;
            }else if( error instanceof ValidationException){
                System.out.println("Validation Exception");
                System.out.println(error.getMessage());
                return error;
            }
            return error;
        }
        catch (Exception error) {
            System.out.println("Exception");
            System.out.println(error.getMessage());
            return error;
        }
    }
}