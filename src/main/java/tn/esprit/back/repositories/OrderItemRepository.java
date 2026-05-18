package tn.esprit.back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tn.esprit.back.entities.OrderItem;

@RepositoryRestResource
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
