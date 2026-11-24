package app.shop.my_app.service;

import app.shop.my_app.domain.Categorie;
import app.shop.my_app.domain.Produit;
import app.shop.my_app.events.BeforeDeleteCategorie;
import app.shop.my_app.events.BeforeDeleteProduit;
import app.shop.my_app.model.ProduitDTO;
import app.shop.my_app.repos.CategorieRepository;
import app.shop.my_app.repos.ProduitRepository;
import app.shop.my_app.util.CustomCollectors;
import app.shop.my_app.util.NotFoundException;
import app.shop.my_app.util.ReferencedException;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ProduitService {

    private final ProduitRepository produitRepository;
    private final CategorieRepository categorieRepository;
    private final ApplicationEventPublisher publisher;

    public ProduitService(final ProduitRepository produitRepository,
            final CategorieRepository categorieRepository,
            final ApplicationEventPublisher publisher) {
        this.produitRepository = produitRepository;
        this.categorieRepository = categorieRepository;
        this.publisher = publisher;
    }

    public List<ProduitDTO> findAll() {
        final List<Produit> produits = produitRepository.findAll(Sort.by("id"));
        return produits.stream()
                .map(produit -> mapToDTO(produit, new ProduitDTO()))
                .toList();
    }

    public ProduitDTO get(final Long id) {
        return produitRepository.findById(id)
                .map(produit -> mapToDTO(produit, new ProduitDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ProduitDTO produitDTO) {
        final Produit produit = new Produit();
        mapToEntity(produitDTO, produit);
        return produitRepository.save(produit).getId();
    }

    public void update(final Long id, final ProduitDTO produitDTO) {
        final Produit produit = produitRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(produitDTO, produit);
        produitRepository.save(produit);
    }

    public void delete(final Long id) {
        final Produit produit = produitRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteProduit(id));
        produitRepository.delete(produit);
    }

    private ProduitDTO mapToDTO(final Produit produit, final ProduitDTO produitDTO) {
        produitDTO.setId(produit.getId());
        produitDTO.setNom(produit.getNom());
        produitDTO.setReference(produit.getReference());
        produitDTO.setStock(produit.getStock());
        produitDTO.setPrix(produit.getPrix());
        produitDTO.setCategorie(produit.getCategorie() == null ? null : produit.getCategorie().getId());
        return produitDTO;
    }

    private Produit mapToEntity(final ProduitDTO produitDTO, final Produit produit) {
        produit.setNom(produitDTO.getNom());
        produit.setReference(produitDTO.getReference());
        produit.setStock(produitDTO.getStock());
        produit.setPrix(produitDTO.getPrix());
        final Categorie categorie = produitDTO.getCategorie() == null ? null : categorieRepository.findById(produitDTO.getCategorie())
                .orElseThrow(() -> new NotFoundException("categorie not found"));
        produit.setCategorie(categorie);
        return produit;
    }

    public Map<Long, String> getProduitValues() {
        return produitRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Produit::getId, Produit::getNom));
    }

    @EventListener(BeforeDeleteCategorie.class)
    public void on(final BeforeDeleteCategorie event) {
        final ReferencedException referencedException = new ReferencedException();
        final Produit categorieProduit = produitRepository.findFirstByCategorieId(event.getId());
        if (categorieProduit != null) {
            referencedException.setKey("categorie.produit.categorie.referenced");
            referencedException.addParam(categorieProduit.getId());
            throw referencedException;
        }
    }

}
