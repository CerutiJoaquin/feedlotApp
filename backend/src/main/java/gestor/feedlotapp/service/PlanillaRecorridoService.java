package gestor.feedlotapp.service;

import gestor.feedlotapp.dto.planillarecorrido.*;
import gestor.feedlotapp.entities.PlanillaRecorrido;
import gestor.feedlotapp.exception.ResourceNotFoundException;
import gestor.feedlotapp.repository.PlanillaRecorridoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

@Service
@Transactional
public class PlanillaRecorridoService {

    private final PlanillaRecorridoRepository repo;

    public PlanillaRecorridoService(PlanillaRecorridoRepository repo){
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<PlanillaRecorridoResponseDto> getAll(){
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PlanillaRecorridoResponseDto getById(Integer id){
        var p = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planilla Recorrido no encontrado con id: " + id));
        return toResponse(p);
    }

    public PlanillaRecorridoResponseDto create(PlanillaRecorridoCreateDto dto){
        var p = new PlanillaRecorrido();
        p.setFecha(Date.valueOf(dto.fecha()));
        p.setResponsable(dto.responsable().trim());
        p.setObservaciones(dto.observaciones() != null ? dto.observaciones().trim() : null);

        p = repo.save(p);
        return toResponse(p);
    }

    public PlanillaRecorridoResponseDto update(Integer id, PlanillaRecorridoUpdateDto dto){
        var p = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planilla Recorrido no encontrado con id: " + id));

        if (dto.fecha() != null)        p.setFecha(Date.valueOf(dto.fecha()));
        if (dto.responsable() != null)  p.setResponsable(dto.responsable().trim());
        if (dto.observaciones() != null)p.setObservaciones(dto.observaciones().trim());

        p = repo.save(p);
        return toResponse(p);
    }

    public void delete(Integer id){
        var p = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planilla Recorrido no encontrado con id: " + id));
        repo.delete(p);
    }

    private PlanillaRecorridoResponseDto toResponse(PlanillaRecorrido p){
        Integer count = (p.getRegistrosRecorridos() == null) ? 0 : p.getRegistrosRecorridos().size();
        return new PlanillaRecorridoResponseDto(
                p.getPlanillaRecorridoId(),
                p.getFecha().toLocalDate(),
                p.getResponsable(),
                p.getObservaciones(),
                count
        );
    }
}
