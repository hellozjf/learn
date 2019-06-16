package com.hellozjf.learn.projects.scoresystem.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

import javax.persistence.Entity;
import java.util.Set;

/**
 * 参考：https://blog.csdn.net/winnershili/article/details/80433458
 *
 * @author hellozjf
 */
@Configuration
public class SpringDataRestConfig {

    @Bean
    public RepositoryRestConfigurer repositoryRestConfigurer() {

        return new RepositoryRestConfigurerAdapter() {
            @Override
            public void configureRepositoryRestConfiguration( RepositoryRestConfiguration config) {

                final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
                provider.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
                final Set<BeanDefinition> beans = provider.findCandidateComponents("com.hellozjf.learn.projects.scoresystem.domain");
                for (final BeanDefinition bean : beans) {
                    try {
                        config.exposeIdsFor(Class.forName(bean.getBeanClassName()));
                    } catch (final ClassNotFoundException e) {
                        // Can't throw ClassNotFoundException due to the method signature. Need to cast it
                        throw new IllegalStateException("Failed to expose `id` field due to", e);
                    }
                }
            }
        };
    }
}
