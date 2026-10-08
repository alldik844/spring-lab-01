package kz.iitu.springlab.aspect;

import kz.iitu.springlab.service.CatalogUpdateService.Change;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(4)
public class ChangeLogAspect {

    private static final Logger log =
            LoggerFactory.getLogger(ChangeLogAspect.class);

    @AfterReturning(
            pointcut = "kz.iitu.springlab.aspect.Pointcuts.updateOperation()",
            returning = "change"
    )
    public void logChange(JoinPoint jp, Change change) {
        log.info("[CHANGE] {} id={} old={} new={}",
                jp.getSignature().toShortString(),
                change.id(),
                change.oldValue(),
                change.newValue());
    }
}