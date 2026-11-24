package app.shop.my_app.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CommandeDTO {

    private Long id;

    @NotNull
    private LocalDateTime datecommande;

    @Size(max = 255)
    private String statut;

    @Size(max = 255)
    private String clientnom;

    @Size(max = 255)
    private String clientadresse;

}
