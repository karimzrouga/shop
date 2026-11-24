package app.shop.my_app.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ProduitDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String nom;

    @NotNull
    @Size(max = 255)
    private String reference;

    private Integer stock;

    private Double prix;

    @NotNull
    private Long categorie;

}
