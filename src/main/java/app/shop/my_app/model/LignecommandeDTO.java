package app.shop.my_app.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LignecommandeDTO {

    private Long id;

    private Integer quantite;

    private Double prix;

    @NotNull
    private Long produits;

    @NotNull
    private Long commande;

}
