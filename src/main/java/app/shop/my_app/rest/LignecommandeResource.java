package app.shop.my_app.rest;

import app.shop.my_app.model.LignecommandeDTO;
import app.shop.my_app.service.LignecommandeService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/lignecommandes", produces = MediaType.APPLICATION_JSON_VALUE)
public class LignecommandeResource {

    private final LignecommandeService lignecommandeService;

    public LignecommandeResource(final LignecommandeService lignecommandeService) {
        this.lignecommandeService = lignecommandeService;
    }

    @GetMapping
    public ResponseEntity<List<LignecommandeDTO>> getAllLignecommandes() {
        return ResponseEntity.ok(lignecommandeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LignecommandeDTO> getLignecommande(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(lignecommandeService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createLignecommande(
            @RequestBody @Valid final LignecommandeDTO lignecommandeDTO) {
        final Long createdId = lignecommandeService.create(lignecommandeDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateLignecommande(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final LignecommandeDTO lignecommandeDTO) {
        lignecommandeService.update(id, lignecommandeDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteLignecommande(@PathVariable(name = "id") final Long id) {
        lignecommandeService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
