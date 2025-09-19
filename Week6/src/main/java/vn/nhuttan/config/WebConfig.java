package vn.nhuttan.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Cấu hình này nói rằng: Bất kỳ URL nào cũng sẽ được tìm trong thư mục static trước tiên.
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}