package kr.blendit.common.storage.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LocalStorageProperties.class)
public class LocalStorageConfig {

}
