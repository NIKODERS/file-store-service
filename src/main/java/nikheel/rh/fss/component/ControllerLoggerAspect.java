package nikheel.rh.fss.component;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ControllerLoggerAspect {

    private static final Logger logger = LoggerFactory.getLogger(ControllerLoggerAspect.class);

    @Around("execution(* nikheel.rh.fss.controller.*.*(..))")
    public Object logControllerMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();

        logger.info("Starting execution of method: {}", methodName);

        Object result = joinPoint.proceed();

        long elapsedTime = System.currentTimeMillis() - startTime;
        logger.info("Finished execution of method: {}. Elapsed time: {} ms", methodName, elapsedTime);

        return result;
    }
}
