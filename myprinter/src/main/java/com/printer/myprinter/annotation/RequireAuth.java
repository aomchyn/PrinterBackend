package com.printer.myprinter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAuth {
    /**
     * รายการ role ที่อนุญาตให้เข้าถึง (เช่น "admin")
     * ถ้าไม่ระบุ = อนุญาตทุก role ที่มี valid token
     */
    String[] roles() default {};
}
