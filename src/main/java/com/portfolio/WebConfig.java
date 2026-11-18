package com.portfolio;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Serves uploaded images from data/uploads/ at /uploads/. */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final ImageStorage images;

	public WebConfig(ImageStorage images) {
		this.images = images;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/uploads/**")
				.addResourceLocations(images.getUploadDir().toUri().toString());
	}
}
