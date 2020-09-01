package com.ralabs.security.app.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean


@Configuration
class FreeMarkerConfig {

    @Primary
    @Bean
    fun factoryBean(): FreeMarkerConfigurationFactoryBean? {
        val bean = FreeMarkerConfigurationFactoryBean()
        bean.setTemplateLoaderPath("classpath:/templates")
        return bean
    }

}