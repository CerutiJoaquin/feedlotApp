package gestor.feedlotapp.service;

import gestor.feedlotapp.dto.registrorecorrido.*;
import gestor.feedlotapp.entities.Corral;
import gestor.feedlotapp.entities.PlanillaRecorrido;
import gestor.feedlotapp.entities.RegistroRecorrido;
import gestor.feedlotapp.exception.ResourceNotFoundException;
import gestor.feedlotapp.repository.CorralRepository;
import gestor.feedlotapp.repository.PlanillaRecorridoRepository;
import gestor.feedlotapp.repository.RegistroRecorridoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RegistroRecorridoService {

    private final RegistroRecorridoRepository repo;
    private final CorralRepository corralRepo;
    private final PlanillaRecorridoRepository planillaRepo;

    public RegistroRecorridoService(RegistroRecorridoRepository repo,
                                    CorralRepository corralRepo,
                                    PlanillaRecorridoRepository planillaRepo) {
        this.repo = repo;
        this.corralRepo = corralRepo;
        this.planillaRepo = planillaRepo;
    }

    @Transactional(readOnly = true)
    public List<RegistroRecorridoResponseDto> getAll(){
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public RegistroRecorridoResponseDto getById(Integer id){
        var rr = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro Recorrido no encontrado con id: " + id));
        return toResponse(rr);
    }

    public RegistroRecorridoResponseDto create(RegistroRecorridoCreateDto dto){
        Corral corralId = corralRepo.findById(dto.corralId())
                .orElseThrow(() -> new ResourceNotFoundException("Corral no encontrado con id: " + dto.corralId()));

        var rr = new RegistroRecorrido();
        rr.setObservaciones(dto.observaciones() != null ? dto.observaciones().trim() : null);
        rr.setCorralId(corralId);

        rr = repo.save(rr);
        return toResponse(rr);
    }

    public RegistroRecorridoResponseDto update(Integer id, RegistroRecorridoUpdateDto dto){
        var rr = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro Recorrido no encontrado con id: " + id));

        if (dto.observaciones() != null) rr.setObservaciones(dto.observaciones().trim());
        if (dto.corralId() != null) {
            var corral = corralRepo.findById(dto.corralId())
                    .orElseThrow(() -> new ResourceNotFoundException("Corral no encontrado con id: " + dto.corralId()));
            rr.setCorralId(corral);
        }
        if (dto.planillaRecorridoId() != null) {
            var planilla = planillaRepo.findById(dto.planillaRecorridoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Planilla Recorrido no encontrada con id: " + dto.planillaRecorridoId()));
            rr.setPlanillaRecorrido(planilla);
        }

        rr = repo.save(rr);
        return toResponse(rr);
    }

    public void delete(Integer id){
        var rr = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro Recorrido no encontrado con id: " + id));
        repo.delete(rr);
    }

    private RegistroRecorridoResponseDto toResponse(RegistroRecorrido rr){
        return new RegistroRecorridoResponseDto(
                rr.getRegistroRecorridoId(),
                rr.getObservaciones(),
                rr.getFecha(),
                rr.getCorralId() != null ? rr.getCorralId().getCorralId() : null,
                rr.getPlanillaRecorrido() != null ? rr.getPlanillaRecorrido().getPlanillaRecorridoId() : null
        );
    }
}
