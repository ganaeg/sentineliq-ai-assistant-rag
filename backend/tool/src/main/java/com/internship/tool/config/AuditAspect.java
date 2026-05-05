package com.internship.tool.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.tool.entity.AuditLog;
import com.internship.tool.repository.AuditLogRepository;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class AuditAspect {

    @Autowired
    private AuditLogRepository repository;

    private final ObjectMapper mapper = new ObjectMapper();

    @Around("execution(* com.internship.tool.service.*.*(..))")
    public Object logAudit(ProceedingJoinPoint joinPoint) throws Throwable {

        System.out.println("🔥 AUDIT TRIGGERED: " + joinPoint.getSignature().getName());

        Object result;
        String oldValue = null;
        String newValue = null;

        try {
            // capture input (old value)
            Object[] args = joinPoint.getArgs();
            if (args.length > 0) {
                oldValue = mapper.writeValueAsString(args[0]);
            }

            // execute method
            result = joinPoint.proceed();

            // capture output (new value)
            newValue = mapper.writeValueAsString(result);

        } catch (Exception e) {
            e.printStackTrace();
            result = joinPoint.proceed();
        }

        try {
            AuditLog log = new AuditLog();
            log.setEntityType("RECORD");
            log.setAction(joinPoint.getSignature().getName());
            log.setEntityId(1L); // simple for now
            log.setOldValue(oldValue);
            log.setNewValue(newValue);
            log.setCreatedAt(LocalDateTime.now());

            repository.save(log);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}