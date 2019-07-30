package io.choerodon.hap.system;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 上传文件路径配置.
 *
 * @author qiang.zeng
 * @since 2018/12/25.
 */
@Configuration
public class UploadFilePathConfig implements WebMvcConfigurer {

    public static final String STATIC_IMAGE_ACCESS_PATH = "/resources/upload/image/**";

    @Value("${file.imageUploadDirectory}")
    private String imageUploadDirectory;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(STATIC_IMAGE_ACCESS_PATH).addResourceLocations("file:" + imageUploadDirectory + "/");
    }
}