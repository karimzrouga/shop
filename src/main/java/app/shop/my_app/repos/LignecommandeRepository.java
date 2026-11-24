package app.shop.my_app.repos;

import app.shop.my_app.domain.Lignecommande;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LignecommandeRepository extends JpaRepository<Lignecommande, Long> {

    Lignecommande findFirstByProduitsId(Long id);

    Lignecommande findFirstByCommandeId(Long id);

}
