package gestor.feedlotapp.service;

import gestor.feedlotapp.dto.corral.CorralCreateDto;
import gestor.feedlotapp.dto.corral.CorralUpdateDto;
import gestor.feedlotapp.dto.corral.CorralResponseDto;
import gestor.feedlotapp.entities.Corral;
import gestor.feedlotapp.exception.ResourceNotFoundException;
import gestor.feedlotapp.repository.CorralRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CorralService {

    private final CorralRepository repo;

    public CorralService(CorralRepository repo){
        this.repo = repo;
    }


    @Transactional(readOnly = true)
    public List<CorralResponseDto> findAllByOrderByCorralIdAsc() {
        return repo.findAll(Sort.by(Sort.Direction.ASC, "corralId"))
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CorralResponseDto getById(Integer id){
        var c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Corral no encontrado con id: " + id));
        return toResponse(c);
    }

    public CorralResponseDto create(CorralCreateDto dto){
        var c = new Corral();
        c.setCapacidadMax(dto.capacidadMax());
        c.setTipoSuperficie(dto.tipoSuperficie().trim());
        c.setEstado(dto.estado().trim());

        c = repo.save(c);
        return toResponse(c);
    }

    public CorralResponseDto update(Integer id, CorralUpdateDto dto){
        var c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Corral no encontrado con id: " + id));

        if (dto.capacidadMax() != null) c.setCapacidadMax(dto.capacidadMax());
        if (dto.tipoSuperficie() != null) c.setTipoSuperficie(dto.tipoSuperficie().trim());
        if (dto.estado() != null) c.setEstado(dto.estado().trim());

        c = repo.save(c);
        return toResponse(c);
    }

    public void delete(Integer id){
        var c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Corral no encontrado con id: " + id));
        repo.delete(c);
    }


    private CorralResponseDto toResponse(Corral c) {
        int cabezas = (c.getAnimales() == null) ? 0 : c.getAnimales().size();
        return new CorralResponseDto(
                c.getCorralId(),
                c.getCapacidadMax(),
                c.getTipoSuperficie(),
                c.getEstado(),
                cabezas
        );
    }
}
