package gestor.feedlotapp.service;

import gestor.feedlotapp.dto.planillatratamiento.*;
import gestor.feedlotapp.entities.PlanillaTratamiento;
import gestor.feedlotapp.exception.ResourceNotFoundException;
import gestor.feedlotapp.repository.PlanillaTratamientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PlanillaTratamientoService {

    private final PlanillaTratamientoRepository repo;

    public PlanillaTratamientoService(PlanillaTratamientoRepository repo){
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<PlanillaTratamientoResponseDto> getAll(){
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PlanillaTratamientoResponseDto findById(Integer id){
        var p = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planilla Tratamiento no encontrada con id: " + id));
        return toResponse(p);
    }

    public PlanillaTratamientoResponseDto create(PlanillaTratamientoCreateDto dto){
        var p = new PlanillaTratamiento();
        p.setFecha(Date.valueOf(dto.fecha()));
        p.setResponsable(dto.responsable().trim());
        p.setObservaciones(dto.observaciones() != null ? dto.observaciones().trim() : null);

        p = repo.save(p);
        return toResponse(p);
    }

    public PlanillaTratamientoResponseDto update(Integer id, PlanillaTratamientoUpdateDto dto){
        var p = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planilla Tratamiento no encontrada con id: " + id));

        if (dto.fecha() != null)        p.setFecha(Date.valueOf(dto.fecha()));
        if (dto.responsable() != null)  p.setResponsable(dto.responsable().trim());
        if (dto.observaciones() != null)p.setObservaciones(dto.observaciones().trim());

        p = repo.save(p);
        return toResponse(p);
    }

    public void delete(Integer id){
        var p = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planilla Tratamiento no encontrada con id: " + id));
        repo.delete(p);
    }

    @Transactional(readOnly = true)
    public PlanillaTratamientoResponseDto findByFecha(LocalDate fecha){
        var opt = repo.findByFecha(Date.valueOf(fecha));
        var p = opt.orElseThrow(() -> new ResourceNotFoundException("Planilla Tratamiento no encontrada para fecha: " + fecha));
        return toResponse(p);
    }

    @Transactional(readOnly = true)
    public List<PlanillaTratamientoResponseDto> findByFechaBetween(LocalDate desde, LocalDate hasta){
        return repo.findByFechaBetween(Date.valueOf(desde), Date.valueOf(hasta))
                .stream().map(this::toResponse).toList();
    }

    private PlanillaTratamientoResponseDto toResponse(PlanillaTratamiento p){
        Integer count = (p.getRegistrosTratamientos() == null) ? 0 : p.getRegistrosTratamientos().size();
        return new PlanillaTratamientoResponseDto(
                p.getPlanillaTratamientoId(),
                p.getFecha().toLocalDate(),
                p.getResponsable(),
                p.getObservaciones(),
                count
        );
    }
}
