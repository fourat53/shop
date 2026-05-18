package tn.esprit.back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tn.esprit.back.entities.Image;

@RepositoryRestResource
public interface ImageRepository extends JpaRepository<Image, Long> {
}
