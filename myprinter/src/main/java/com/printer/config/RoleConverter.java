package com.printer.config;

import com.printer.myprinter.enums.Role;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role role) {
        return role == null ? null : role.name(); // เก็บเป็นตัวพิมพ์ใหญ่
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        // แปลงเป็นตัวพิมพ์ใหญ่ก่อนหา enum
        return Role.valueOf(dbData.toUpperCase());
    }
}
