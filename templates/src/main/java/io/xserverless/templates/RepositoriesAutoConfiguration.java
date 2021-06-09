package io.xserverless.templates;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = {"io.xserverless.devcenter.function"})
public class RepositoriesAutoConfiguration {
}
