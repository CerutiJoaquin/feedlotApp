package gestor.feedlotapp.service;

import gestor.feedlotapp.dto.planillacomedero.*;
import gestor.feedlotapp.entities.PlanillaComedero;
import gestor.feedlotapp.exception.ResourceNotFoundException;
import gestor.feedlotapp.repository.PlanillaComederoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

@Service
@Transactional
public class PlanillaComederoService {

    private final PlanillaComederoRepository repo;

    public PlanillaComederoService(PlanillaComederoRepository repo){
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<PlanillaComederoResponseDto> getAll(){
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PlanillaComederoResponseDto getById(Integer id){
        var p = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planilla Comedero no encontrada con id: " + id));
        return toResponse(p);
    }

    public PlanillaComederoResponseDto create(PlanillaComederoCreateDto dto){
        var p = new PlanillaComedero();
        p.setFecha(Date.valueOf(dto.fecha()));
        p.setEncargado(dto.encargado().trim());
        p.setObservaciones(dto.observaciones() != null ? dto.observaciones().trim() : null);

        p = repo.save(p);
        return toResponse(p);
    }

    public PlanillaComederoResponseDto update(Integer id, PlanillaComederoUpdateDto dto){
        var p = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planilla Comedero no encontrada con id: " + id));

        if (dto.fecha() != null)       p.setFecha(Date.valueOf(dto.fecha()));
        if (dto.encargado() != null)   p.setEncargado(dto.encargado().trim());
        if (dto.observaciones() != null) p.setObservaciones(dto.observaciones().trim());

        p = repo.save(p);
        return toResponse(p);
    }

    public void delete(Integer id){
        var p = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planilla Comedero no encontrada con id: " + id));
        repo.delete(p);
    }

    private PlanillaComederoResponseDto toResponse(PlanillaComedero p){
        Integer count = (p.getRegistrosComederos() == null) ? 0 : p.getRegistrosComederos().size();
        return new PlanillaComederoResponseDto(
                p.getPlanillaComederoId(),
                p.getFecha().toLocalDate(),
                p.getEncargado(),
                p.getObservaciones(),
                count
        );
    }
}
