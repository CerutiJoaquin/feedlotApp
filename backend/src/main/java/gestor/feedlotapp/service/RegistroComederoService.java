package gestor.feedlotapp.service;

import gestor.feedlotapp.dto.registrocomedero.RegistroComederoCreateDto;
import gestor.feedlotapp.dto.registrocomedero.RegistroComederoResponseDto;
import gestor.feedlotapp.dto.registrocomedero.RegistroComederoUpdateDto;
import gestor.feedlotapp.entities.Corral;
import gestor.feedlotapp.entities.Insumo;
import gestor.feedlotapp.entities.PlanillaComedero;
import gestor.feedlotapp.entities.RegistroComedero;
import gestor.feedlotapp.entities.RegistroComederoDetalle;
import gestor.feedlotapp.exception.ResourceNotFoundException;
import gestor.feedlotapp.repository.CorralRepository;
import gestor.feedlotapp.repository.InsumoRepository;
import gestor.feedlotapp.repository.PlanillaComederoRepository;
import gestor.feedlotapp.repository.RegistroComederoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class RegistroComederoService {

    private final RegistroComederoRepository repo;
    private final CorralRepository corralRepo;
    private final PlanillaComederoRepository planillaRepo;
    private final InsumoRepository insumoRepo;

    public RegistroComederoService(RegistroComederoRepository repo,
                                   CorralRepository corralRepo,
                                   PlanillaComederoRepository planillaRepo,
                                   InsumoRepository insumoRepo) {
        this.repo = repo;
        this.corralRepo = corralRepo;
        this.planillaRepo = planillaRepo;
        this.insumoRepo = insumoRepo;
    }



    @Transactional(readOnly = true)
    public List<RegistroComederoResponseDto> getAll() {
        return repo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RegistroComederoResponseDto getById(Integer id) {
        var rc = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro Comedero no encontrado con id: " + id));
        return toResponse(rc);
    }

    public RegistroComederoResponseDto create(RegistroComederoCreateDto dto) {
        if (dto.cantidad() == null) {
            throw new IllegalArgumentException("La cantidad servida debe ser mayor a 0");
        }

        Corral corral = corralRepo.findById(dto.corralId())
                .orElseThrow(() -> new ResourceNotFoundException("Corral no encontrado con id: " + dto.corralId()));

        Insumo insumo = insumoRepo.findById(dto.insumoId())
                .orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado con id: " + dto.insumoId()));


        float cantidadServir = dto.cantidad().floatValue();
        if (insumo.getCantidad() < cantidadServir) {
            throw new IllegalStateException("Stock insuficiente para el insumo: " + insumo.getNombre());
        }

        insumo.setCantidad(insumo.getCantidad() - cantidadServir);
        insumoRepo.save(insumo);


        PlanillaComedero planilla = null;
        if (dto.planillaComederoId() != null) {
            planilla = planillaRepo.findById(dto.planillaComederoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Planilla Comedero no encontrada con id: " + dto.planillaComederoId()));
        }


        var rc = new RegistroComedero();
        rc.setCorral(corral);
        rc.setInsumo(insumo);
        rc.setPlanillaComedero(planilla);
        rc.setCantidad(dto.cantidad());
        rc = repo.save(rc);

        return toResponse(rc);
    }

    public RegistroComederoResponseDto update(Integer id, RegistroComederoUpdateDto dto) {
        var rc = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro Comedero no encontrado con id: " + id));


        if (dto.corralId() != null) {
            Corral corral = corralRepo.findById(dto.corralId())
                    .orElseThrow(() -> new ResourceNotFoundException("Corral no encontrado con id: " + dto.corralId()));
            rc.setCorral(corral);
        }

        if (dto.planillaComederoId() != null) {
            PlanillaComedero planilla = planillaRepo.findById(dto.planillaComederoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Planilla Comedero no encontrada con id: " + dto.planillaComederoId()));
            rc.setPlanillaComedero(planilla);
        }

        rc = repo.save(rc);
        return toResponse(rc);
    }

    public void delete(Integer id) {
        var rc = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro Comedero no encontrado con id: " + id));
        repo.delete(rc);
    }


    private RegistroComederoResponseDto toResponse(RegistroComedero rc) {
        return new RegistroComederoResponseDto(
                rc.getRegistroComederoId(),
                rc.getCantidad(),
                rc.getFecha(),
                rc.getCorral() != null ? rc.getCorral().getCorralId() : null,
                rc.getInsumo() != null ? rc.getInsumo().getInsumoId() : null,
                rc.getPlanillaComedero() != null ? rc.getPlanillaComedero().getPlanillaComederoId() : null
        );
    }
}
