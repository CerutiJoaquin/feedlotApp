package gestor.feedlotapp.service;

import gestor.feedlotapp.dto.registrotratamiento.RegistroTratamientoCreateDto;
import gestor.feedlotapp.dto.registrotratamiento.RegistroTratamientoResponseDto;
import gestor.feedlotapp.dto.registrotratamiento.RegistroTratamientoUpdateDto;
import gestor.feedlotapp.entities.Animal;
import gestor.feedlotapp.entities.RegistroTratamiento;
import gestor.feedlotapp.enums.insumo.Categoria;
import gestor.feedlotapp.exception.ResourceNotFoundException;
import gestor.feedlotapp.repository.AnimalRepository;
import gestor.feedlotapp.repository.InsumoRepository;
import gestor.feedlotapp.repository.PlanillaTratamientoRepository;
import gestor.feedlotapp.repository.RegistroTratamientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class RegistroTratamientoService {

    private final RegistroTratamientoRepository registroTratamientoRepo;
    private final AnimalRepository animalRepo;
    private final InsumoRepository insumoRepo;
    private final PlanillaTratamientoRepository planillaRepo;

    public RegistroTratamientoService(RegistroTratamientoRepository registroTratamientoRepo,
                                      AnimalRepository animalRepo,
                                      InsumoRepository insumoRepo,
                                      PlanillaTratamientoRepository planillaRepo) {
        this.registroTratamientoRepo = registroTratamientoRepo;
        this.animalRepo = animalRepo;
        this.insumoRepo = insumoRepo;
        this.planillaRepo = planillaRepo;
    }


    @Transactional(readOnly = true)
    public List<RegistroTratamientoResponseDto> getAll() {
        return registroTratamientoRepo.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RegistroTratamientoResponseDto getById(Integer id) {
        var rt = registroTratamientoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro Tratamiento no encontrado con id: " + id));
        return toResponse(rt);
    }


    @Transactional
    public RegistroTratamientoResponseDto create(RegistroTratamientoCreateDto dto) {
        if (dto == null) throw new IllegalArgumentException("Datos de tratamiento inválidos");
        if (dto.dosis() == null || dto.dosis() <= 0f)
            throw new IllegalArgumentException("La dosis debe ser mayor a 0");


        var insumo = insumoRepo.findByIdForUpdate(dto.medicamentoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Insumo no encontrado con id: " + dto.medicamentoId()));
        if (insumo.getCategoria() != Categoria.MEDICAMENTO) {
            throw new IllegalStateException("El insumo seleccionado no es un medicamento");
        }


        Long id = dto.animalId();
        String caravana = (dto.caravana() == null) ? null : dto.caravana().trim();

        boolean sinId = (id == null);
        boolean sinCaravana = (caravana == null || caravana.isEmpty());
        if (sinId && sinCaravana)
            throw new IllegalArgumentException("Debe indicar 'animalId' o 'caravana'.");

        Animal animal;
        if (!sinId && !sinCaravana) {
            var aPorId = animalRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Animal no encontrado con id: " + id));
            var aPorCaravana = animalRepo.findWithHistoryByCaravana(caravana)
                    .orElseThrow(() -> new ResourceNotFoundException("Animal no encontrado con caravana: " + caravana));
            if (!aPorId.getAnimalId().equals(aPorCaravana.getAnimalId()))
                throw new IllegalArgumentException("El 'animalId' y la 'caravana' pertenecen a animales distintos.");
            animal = aPorId;
        } else if (!sinId) {
            animal = animalRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Animal no encontrado con id: " + id));
        } else {
            animal = animalRepo.findByCaravana(caravana)
                    .orElseThrow(() -> new ResourceNotFoundException("Animal no encontrado con caravana: " + caravana));
        }


        float dosis = dto.dosis();
        if (insumo.getCantidad() < dosis)
            throw new IllegalStateException("Stock insuficiente de " + insumo.getNombre());
        insumo.setCantidad(insumo.getCantidad() - dosis);
        insumoRepo.save(insumo);


        var rt = new RegistroTratamiento();
        rt.setAnimal(animal);
        rt.setInsumo(insumo);
        rt.setDosis(dosis);
        rt.setResponsable(dto.responsable());
        rt.setDescripcion(dto.descripcion());
        rt.setFecha(dto.fecha() != null ? dto.fecha() : LocalDate.now());

        if (dto.responsable() != null && !dto.responsable().isBlank())
            rt.setResponsable(dto.responsable().trim());

        if (dto.descripcion() != null && !dto.descripcion().isBlank())
            rt.setDescripcion(dto.descripcion().trim());

        if (dto.planillaTratamientoId() != null) {
            var pl = planillaRepo.findById(dto.planillaTratamientoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Planilla Tratamiento no encontrada con id: " + dto.planillaTratamientoId()));
            rt.setPlanillaTratamiento(pl);
        }

        rt = registroTratamientoRepo.save(rt);
        return toResponse(rt);
    }



    @Transactional
    public RegistroTratamientoResponseDto update(Integer id, RegistroTratamientoUpdateDto dto) {
        var rt = registroTratamientoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro Tratamiento no encontrado con id: " + id));

        if (dto.dosis() != null) {
            if (dto.dosis() <= 0f) throw new IllegalArgumentException("La dosis debe ser mayor a 0");
            rt.setDosis(dto.dosis());
        }
        if (dto.medicamentoId() != null) {
            var insumo = insumoRepo.findById(dto.medicamentoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado con id: " + dto.medicamentoId()));
            if (insumo.getCategoria() != Categoria.MEDICAMENTO)
                throw new IllegalStateException("El insumo seleccionado no es un medicamento");
            rt.setInsumo(insumo);
        }
        if (dto.animalId() != null) {
            var animal = animalRepo.findById(dto.animalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Animal no encontrado con id: " + dto.animalId()));
            rt.setAnimal(animal);
        }
        if (dto.planillaTratamientoId() != null) {
            var pl = planillaRepo.findById(dto.planillaTratamientoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Planilla Tratamiento no encontrada con id: " + dto.planillaTratamientoId()));
            rt.setPlanillaTratamiento(pl);
        }

        rt = registroTratamientoRepo.save(rt);
        return toResponse(rt);
    }



    public void delete(Integer id) {
        var rt = registroTratamientoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro Tratamiento no encontrado con id: " + id));
        registroTratamientoRepo.delete(rt);
    }



    @Transactional(readOnly = true)
    public List<RegistroTratamientoResponseDto> findByAnimalId(Long animalId) {
        return registroTratamientoRepo.findByAnimal_AnimalId(animalId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<RegistroTratamientoResponseDto> findByAnimalCaravana(String caravana) {
        return registroTratamientoRepo.findByAnimal_Caravana(caravana)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<RegistroTratamientoResponseDto> findByFechaBetween(LocalDate desde, LocalDate hasta) {
        return registroTratamientoRepo.findByFechaBetween(desde, hasta)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<RegistroTratamientoResponseDto> findByAnimalIdAndFechaBetween(Long animalId,
                                                                              LocalDate desde,
                                                                              LocalDate hasta) {
        return registroTratamientoRepo
                .findByAnimal_AnimalIdAndFechaBetweenOrderByFechaAsc(animalId, desde, hasta)
                .stream().map(this::toResponse).toList();
    }



    private RegistroTratamientoResponseDto toResponse(RegistroTratamiento rt) {
        String medName      = (rt.getInsumo() != null) ? rt.getInsumo().getNombre() : null;
        Long animalId   = (rt.getAnimal() != null) ? rt.getAnimal().getAnimalId() : null;
        String animalCaravana = (rt.getAnimal() != null) ? rt.getAnimal().getCaravana() : null;
        String responsable = (rt.getResponsable() != null) ? rt.getResponsable() : null;
        String descripcion = (rt.getDescripcion() != null) ? rt.getDescripcion() : null;
        Integer planillaId = (rt.getPlanillaTratamiento() != null)
                ? rt.getPlanillaTratamiento().getPlanillaTratamientoId() : null;

        return new RegistroTratamientoResponseDto(
                rt.getRegistroTratamientoId(),
                rt.getFecha(),
                rt.getDosis(),
                medName,
                animalId,
                animalCaravana,
                responsable,
                descripcion,
                planillaId
        );
    }
}
