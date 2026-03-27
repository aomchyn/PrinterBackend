package com.printer.myprinter.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate Limiter แบบ in-memory สำหรับป้องกัน brute-force attacks บน login endpoints
 * ใช้ sliding window: จำกัดจำนวน request ต่อ key (เช่น IP หรือ username) ภายในช่วงเวลาที่กำหนด
 */
@Service
public class RateLimitService {

    private static final Logger log = LoggerFactory.getLogger(RateLimitService.class);

    // จำนวน login attempts สูงสุดต่อ key ภายใน window
    private static final int MAX_ATTEMPTS = 5;
    // ระยะเวลา window (มิลลิวินาที) — 15 นาที
    private static final long WINDOW_MS = 15 * 60 * 1000;

    private final ConcurrentHashMap<String, AttemptRecord> attempts = new ConcurrentHashMap<>();

    /**
     * ตรวจสอบว่า key นี้ถูก rate limit หรือไม่
     * @return true ถ้าอนุญาต, false ถ้าถูกบล็อก
     */
    public boolean isAllowed(String key) {
        long now = System.currentTimeMillis();

        AttemptRecord record = attempts.compute(key, (k, existing) -> {
            if (existing == null || now - existing.windowStart > WINDOW_MS) {
                // window ใหม่
                return new AttemptRecord(now, new AtomicInteger(1));
            }
            existing.count.incrementAndGet();
            return existing;
        });

        boolean allowed = record.count.get() <= MAX_ATTEMPTS;

        if (!allowed) {
            long remainingSeconds = (WINDOW_MS - (now - record.windowStart)) / 1000;
            log.warn("Rate limit exceeded for key: {}. Retry after {} seconds.", key, remainingSeconds);
        }

        return allowed;
    }

    /**
     * ล้าง record ของ key (เรียกหลัง login สำเร็จ)
     */
    public void resetKey(String key) {
        attempts.remove(key);
    }

    /**
     * คืนจำนวนวินาทีที่เหลือก่อน window reset
     */
    public long getRemainingSeconds(String key) {
        AttemptRecord record = attempts.get(key);
        if (record == null) return 0;
        long elapsed = System.currentTimeMillis() - record.windowStart;
        long remaining = (WINDOW_MS - elapsed) / 1000;
        return Math.max(0, remaining);
    }

    private static class AttemptRecord {
        final long windowStart;
        final AtomicInteger count;

        AttemptRecord(long windowStart, AtomicInteger count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}
