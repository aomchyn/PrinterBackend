package com.printer.myprinter;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.printer.myprinter.interceptor.JwtInterceptor;
import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;
    private static String secret;

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

    public static String getSecret() {
        if (secret == null) {
            // ลอง system property ก่อน (จะถูก set โดย MyprinterApplication จาก .env)
            secret = System.getProperty("JWT_SECRET");

            // ถ้าไม่มี ลองโหลดจาก .env โดยตรง
            if (secret == null) {
                try {
                    Dotenv dotenv = Dotenv.configure()
                            .directory(System.getProperty("user.dir"))
                            .ignoreIfMissing()
                            .load();
                    secret = dotenv.get("JWT_SECRET");
                } catch (Exception e) {
                    // fallback: ลอง sub-directory
                    Dotenv dotenv = Dotenv.configure()
                            .directory(System.getProperty("user.dir") + "/myprinter")
                            .ignoreIfMissing()
                            .load();
                    secret = dotenv.get("JWT_SECRET");
                }
            }

            if (secret == null || secret.length() < 32) {
                throw new IllegalStateException(
                        "JWT_SECRET must be set and at least 32 characters long. " +
                                "Set it in .env file or as an environment variable.");
            }
        }
        return secret;
    }
}