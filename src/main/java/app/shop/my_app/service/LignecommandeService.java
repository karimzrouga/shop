package app.shop.my_app.service;

import app.shop.my_app.domain.Commande;
import app.shop.my_app.domain.Lignecommande;
import app.shop.my_app.domain.Produit;
import app.shop.my_app.events.BeforeDeleteCommande;
import app.shop.my_app.events.BeforeDeleteProduit;
import app.shop.my_app.model.LignecommandeDTO;
import app.shop.my_app.repos.CommandeRepository;
import app.shop.my_app.repos.LignecommandeRepository;
import app.shop.my_app.repos.ProduitRepository;
import app.shop.my_app.util.NotFoundException;
import app.shop.my_app.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class LignecommandeService {

    private final LignecommandeRepository lignecommandeRepository;
    private final ProduitRepository produitRepository;
    private final CommandeRepository commandeRepository;

    public LignecommandeService(final LignecommandeRepository lignecommandeRepository,
            final ProduitRepository produitRepository,
            final CommandeRepository commandeRepository) {
        this.lignecommandeRepository = lignecommandeRepository;
        this.produitRepository = produitRepository;
        this.commandeRepository = commandeRepository;
    }

    public List<LignecommandeDTO> findAll() {
        final List<Lignecommande> lignecommandes = lignecommandeRepository.findAll(Sort.by("id"));
        return lignecommandes.stream()
                .map(lignecommande -> mapToDTO(lignecommande, new LignecommandeDTO()))
                .toList();
    }

    public LignecommandeDTO get(final Long id) {
        return lignecommandeRepository.findById(id)
                .map(lignecommande -> mapToDTO(lignecommande, new LignecommandeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final LignecommandeDTO lignecommandeDTO) {
        final Lignecommande lignecommande = new Lignecommande();
        mapToEntity(lignecommandeDTO, lignecommande);
        return lignecommandeRepository.save(lignecommande).getId();
    }

    public void update(final Long id, final LignecommandeDTO lignecommandeDTO) {
        final Lignecommande lignecommande = lignecommandeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(lignecommandeDTO, lignecommande);
        lignecommandeRepository.save(lignecommande);
    }

    public void delete(final Long id) {
        final Lignecommande lignecommande = lignecommandeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        lignecommandeRepository.delete(lignecommande);
    }

    private LignecommandeDTO mapToDTO(final Lignecommande lignecommande,
            final LignecommandeDTO lignecommandeDTO) {
        lignecommandeDTO.setId(lignecommande.getId());
        lignecommandeDTO.setQuantite(lignecommande.getQuantite());
        lignecommandeDTO.setPrix(lignecommande.getPrix());
        lignecommandeDTO.setProduits(lignecommande.getProduits() == null ? null : lignecommande.getProduits().getId());
        lignecommandeDTO.setCommande(lignecommande.getCommande() == null ? null : lignecommande.getCommande().getId());
        return lignecommandeDTO;
    }

    private Lignecommande mapToEntity(final LignecommandeDTO lignecommandeDTO,
            final Lignecommande lignecommande) {
        lignecommande.setQuantite(lignecommandeDTO.getQuantite());
        lignecommande.setPrix(lignecommandeDTO.getPrix());
        final Produit produits = lignecommandeDTO.getProduits() == null ? null : produitRepository.findById(lignecommandeDTO.getProduits())
                .orElseThrow(() -> new NotFoundException("produits not found"));
        lignecommande.setProduits(produits);
        final Commande commande = lignecommandeDTO.getCommande() == null ? null : commandeRepository.findById(lignecommandeDTO.getCommande())
                .orElseThrow(() -> new NotFoundException("commande not found"));
        lignecommande.setCommande(commande);
        return lignecommande;
    }

    @EventListener(BeforeDeleteProduit.class)
    public void on(final BeforeDeleteProduit event) {
        final ReferencedException referencedException = new ReferencedException();
        final Lignecommande produitsLignecommande = lignecommandeRepository.findFirstByProduitsId(event.getId());
        if (produitsLignecommande != null) {
            referencedException.setKey("produit.lignecommande.produits.referenced");
            referencedException.addParam(produitsLignecommande.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteCommande.class)
    public void on(final BeforeDeleteCommande event) {
        final ReferencedException referencedException = new ReferencedException();
        final Lignecommande commandeLignecommande = lignecommandeRepository.findFirstByCommandeId(event.getId());
        if (commandeLignecommande != null) {
            referencedException.setKey("commande.lignecommande.commande.referenced");
            referencedException.addParam(commandeLignecommande.getId());
            throw referencedException;
        }
    }

}
