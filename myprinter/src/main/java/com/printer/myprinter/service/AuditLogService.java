package com.printer.myprinter.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private static final Logger auditLog = LoggerFactory.getLogger("AUDIT_LOG");

    public void logAction(String adminId, String action, String resourceType, String resourceId, String details) {
        // Format: [AUDIT] AdminID: x | Action: y | Resource: z | ID: w | Details: d
        auditLog.info("[AUDIT] AdminID: {} | Action: {} | Resource: {} | ID: {} | Details: {}", 
                adminId, action, resourceType, resourceId, details);
    }
}
