package app.shop.my_app.repos;

import app.shop.my_app.domain.Commande;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CommandeRepository extends JpaRepository<Commande, Long> {
}
