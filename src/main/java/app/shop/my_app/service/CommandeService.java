package app.shop.my_app.service;

import app.shop.my_app.domain.Commande;
import app.shop.my_app.events.BeforeDeleteCommande;
import app.shop.my_app.model.CommandeDTO;
import app.shop.my_app.repos.CommandeRepository;
import app.shop.my_app.util.CustomCollectors;
import app.shop.my_app.util.NotFoundException;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final ApplicationEventPublisher publisher;

    public CommandeService(final CommandeRepository commandeRepository,
            final ApplicationEventPublisher publisher) {
        this.commandeRepository = commandeRepository;
        this.publisher = publisher;
    }

    public List<CommandeDTO> findAll() {
        final List<Commande> commandes = commandeRepository.findAll(Sort.by("id"));
        return commandes.stream()
                .map(commande -> mapToDTO(commande, new CommandeDTO()))
                .toList();
    }

    public CommandeDTO get(final Long id) {
        return commandeRepository.findById(id)
                .map(commande -> mapToDTO(commande, new CommandeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final CommandeDTO commandeDTO) {
        final Commande commande = new Commande();
        mapToEntity(commandeDTO, commande);
        return commandeRepository.save(commande).getId();
    }

    public void update(final Long id, final CommandeDTO commandeDTO) {
        final Commande commande = commandeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(commandeDTO, commande);
        commandeRepository.save(commande);
    }

    public void delete(final Long id) {
        final Commande commande = commandeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteCommande(id));
        commandeRepository.delete(commande);
    }

    private CommandeDTO mapToDTO(final Commande commande, final CommandeDTO commandeDTO) {
        commandeDTO.setId(commande.getId());
        commandeDTO.setDatecommande(commande.getDatecommande());
        commandeDTO.setStatut(commande.getStatut());
        commandeDTO.setClientnom(commande.getClientnom());
        commandeDTO.setClientadresse(commande.getClientadresse());
        return commandeDTO;
    }

    private Commande mapToEntity(final CommandeDTO commandeDTO, final Commande commande) {
        commande.setDatecommande(commandeDTO.getDatecommande());
        commande.setStatut(commandeDTO.getStatut());
        commande.setClientnom(commandeDTO.getClientnom());
        commande.setClientadresse(commandeDTO.getClientadresse());
        return commande;
    }

    public Map<Long, Long> getCommandeValues() {
        return commandeRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Commande::getId, Commande::getId));
    }

}
