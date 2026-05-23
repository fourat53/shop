package tn.esprit.back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Type;

@Configuration
public class RestConfig implements RepositoryRestConfigurer {

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
        .allowedOrigins("http://localhost:3000", "http://127.0.0.1:3000")
        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true);
  }
}
