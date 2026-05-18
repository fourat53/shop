package tn.esprit.back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tn.esprit.back.entities.Cart;

@RepositoryRestResource
public interface CartRepository extends JpaRepository<Cart, Long> {
}
