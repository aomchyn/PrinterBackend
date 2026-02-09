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
    
    public WebConfig(JwtInterceptor jwtInterceptor){
        this.jwtInterceptor = jwtInterceptor;
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(jwtInterceptor)
                // ✅ กำหนดว่า path ไหนต้องตรวจสอบ
                .addPathPatterns("/**")
                // ✅ ยกเว้น path เหล่านี้
                .excludePathPatterns(
                    "/user/login",
                    "/user/register",
                    "/fgcode/**", 
                    "/order/**", // ✅ ยกเว้นทุก path ที่ขึ้นต้นด้วย /fgcode
                    "/error"
                );
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
    
    public static String getSecret(){
        if (secret == null){
            Dotenv dotenv = Dotenv.configure()
                    .directory(System.getProperty("user.dir") + "/myprinter")
                    .load();
            secret = dotenv.get("JWT_SECRET");
        }
        return secret;
    }
}