package kr.blendit.common.config;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder.postConfigurer(mapper -> {
            mapper.configOverride(List.class)
                .setSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY));
            mapper.configOverride(Set.class)
                .setSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY));
            mapper.configOverride(Map.class)
                .setSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY));
        });
    }
}
