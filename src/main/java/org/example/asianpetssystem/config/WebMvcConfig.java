package org.example.asianpetssystem.config;

import org.example.asianpetssystem.entity.AttachmentType;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // 注册 AttachmentType 的转换器
        registry.addConverter(new Converter<String, AttachmentType>() {
            @Override
            public AttachmentType convert(String source) {
                if (source == null || source.trim().isEmpty()) {
                    return null;
                }
                try {
                    return AttachmentType.valueOf(source.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("未知的附件类型: " + source);
                }
            }
        });
    }
}
