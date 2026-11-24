package app.shop.my_app.repos;

import app.shop.my_app.domain.Produit;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProduitRepository extends JpaRepository<Produit, Long> {

    Produit findFirstByCategorieId(Long id);

}
