package com.printer.myprinter;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.printer.myprinter.interceptor.JwtInterceptor;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;
   
    @Value("${JWT_SECRET}")
    private String jwtSecret; // ✅ ให้ Spring inject แทน static

    public WebConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                // ยกเว้นเฉพาะ path ที่ต้อง public (login endpoints)
                .excludePathPatterns(
                        "/printer/user/signin",
                        "/printer/user/admin-signin",
                        "/hello",
                        "/error"
                );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // ใช้ CORS origins จาก environment variable แทน wildcard *
        String allowedOrigins = System.getProperty("CORS_ALLOWED_ORIGINS",
                System.getenv().getOrDefault("CORS_ALLOWED_ORIGINS", "http://localhost:3000"));
        String[] origins = allowedOrigins.split(",");

        registry.addMapping("/**")
                .allowedOrigins(origins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type", "Accept");
    }

  @PostConstruct
    public void validateSecret() {
        if (jwtSecret == null || jwtSecret.length() < 32) {
            throw new IllegalStateException(
                "JWT_SECRET must be set and at least 32 characters long.");
        }
    }
   
 public String getJwtSecret() {
        return jwtSecret;
    }


}
