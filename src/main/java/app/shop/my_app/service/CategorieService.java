package app.shop.my_app.service;

import app.shop.my_app.domain.Categorie;
import app.shop.my_app.events.BeforeDeleteCategorie;
import app.shop.my_app.model.CategorieDTO;
import app.shop.my_app.repos.CategorieRepository;
import app.shop.my_app.util.CustomCollectors;
import app.shop.my_app.util.NotFoundException;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class CategorieService {

    private final CategorieRepository categorieRepository;
    private final ApplicationEventPublisher publisher;

    public CategorieService(final CategorieRepository categorieRepository,
            final ApplicationEventPublisher publisher) {
        this.categorieRepository = categorieRepository;
        this.publisher = publisher;
    }

    public List<CategorieDTO> findAll() {
        final List<Categorie> categories = categorieRepository.findAll(Sort.by("id"));
        return categories.stream()
                .map(categorie -> mapToDTO(categorie, new CategorieDTO()))
                .toList();
    }

    public CategorieDTO get(final Long id) {
        return categorieRepository.findById(id)
                .map(categorie -> mapToDTO(categorie, new CategorieDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final CategorieDTO categorieDTO) {
        final Categorie categorie = new Categorie();
        mapToEntity(categorieDTO, categorie);
        return categorieRepository.save(categorie).getId();
    }

    public void update(final Long id, final CategorieDTO categorieDTO) {
        final Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(categorieDTO, categorie);
        categorieRepository.save(categorie);
    }

    public void delete(final Long id) {
        final Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteCategorie(id));
        categorieRepository.delete(categorie);
    }

    private CategorieDTO mapToDTO(final Categorie categorie, final CategorieDTO categorieDTO) {
        categorieDTO.setId(categorie.getId());
        categorieDTO.setNom(categorie.getNom());
        return categorieDTO;
    }

    private Categorie mapToEntity(final CategorieDTO categorieDTO, final Categorie categorie) {
        categorie.setNom(categorieDTO.getNom());
        return categorie;
    }

    public Map<Long, String> getCategorieValues() {
        return categorieRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Categorie::getId, Categorie::getNom));
    }

}
