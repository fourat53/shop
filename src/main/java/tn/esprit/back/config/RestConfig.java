package tn.esprit.back.config;

import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import jakarta.persistence.metamodel.Type;
import jakarta.persistence.EntityManager;

@Configuration
public class RestConfig implements RepositoryRestConfigurer {
  @Value("${front.dev.url}")
  private String frontDevUrl;

  @Value("${front.prod.url}")
  private String frontProdUrl;

  private final EntityManager entityManager;

  public RestConfig(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
    Class<?>[] classes = entityManager.getMetamodel().getEntities().stream()
        .map(Type::getJavaType)
        .toArray(Class[]::new);
    config.exposeIdsFor(classes);

    cors.addMapping("/**")
        .allowedOrigins(frontDevUrl, frontProdUrl)
        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true);
  }
}
