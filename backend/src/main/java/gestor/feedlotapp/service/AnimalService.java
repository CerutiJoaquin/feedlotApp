package gestor.feedlotapp.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import gestor.feedlotapp.dto.animal.AnimalUpdateTratDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gestor.feedlotapp.dto.animal.*;

import gestor.feedlotapp.entities.Animal;
import gestor.feedlotapp.entities.Insumo;
import gestor.feedlotapp.entities.RegistroTratamiento;

import gestor.feedlotapp.exception.ResourceNotFoundException;
import gestor.feedlotapp.repository.AnimalRepository;
import gestor.feedlotapp.repository.CorralRepository;

@Service
@Transactional
public class AnimalService {

    private static final ZoneId AR_CORDOBA = ZoneId.of("America/Argentina/Cordoba");

    private final AnimalRepository animalRepo;
    private final CorralRepository corralRepo;

    public AnimalService(AnimalRepository a, CorralRepository c) {
        this.animalRepo = a;
        this.corralRepo = c;
    }



    @Transactional(readOnly = true)
    public List<AnimalResponseDto> getAll() {
        return animalRepo.findActivos()
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AnimalResponseDto getById(Long id) {
        var a = animalRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal no encontrado con id: " + id));
        return toResponse(a);
    }

    public AnimalResponseDto create(AnimalCreateDto dto) {
        var corral = corralRepo.getReferenceById(dto.corralId());

        var a = new Animal();
        a.setCaravana(dto.caravana().trim());
        a.setRaza(dto.raza().trim());
        a.setSexo(dto.sexo());
        a.setPesoActual(dto.pesoActual());
        a.setEstadoSalud(dto.estadoSalud().trim());
        a.setCorral(corral);
        a.setFechaNacimiento(dto.fechaNacimiento());
        a.setProxTratamiento(dto.proxTratamiento());


        if (dto.fechaNacimiento() != null) {
            a.setEdadMeses(calcEdadMeses(dto.fechaNacimiento()));
        } else {
            a.setEdadMeses(null);
        }

        a = animalRepo.save(a);
        return toResponse(a);
    }

    public AnimalResponseDto updateDatos(Long id, AnimalUpdateDto dto) {
        var a = animalRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal no encontrado con id: " + id));

        if (dto.estadoSalud()    != null) a.setEstadoSalud(dto.estadoSalud().trim());
        if (dto.corralId()       != null) a.setCorral(corralRepo.getReferenceById(dto.corralId()));
        if (dto.proxTratamiento()!= null) a.setProxTratamiento(dto.proxTratamiento());

        a = animalRepo.save(a);
        return toResponse(a);
    }
    public AnimalResponseDto updateFechaTrat(Long id, AnimalUpdateTratDto dto) {
        var a = animalRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal no encontrado con id: " + id));
        if (dto.proxTratamiento()!= null) a.setProxTratamiento(dto.proxTratamiento());

        a = animalRepo.save(a);
        return toResponse(a);
    }

    public void delete(Long id) {
        var a = animalRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal no encontrado con id: " + id));

        if (a.getVentaDetalleBaja() != null) {
            throw new IllegalStateException("No se puede eliminar: el animal fue vendido.");
        }
        animalRepo.delete(a);
    }



    @Transactional(readOnly = true)
    public List<AnimalResponseDto> searchByCaravana(String fragmento) {
        return animalRepo.findActivosByCaravanaLike(fragmento)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AnimalResponseDto getByCaravana(String caravana) {
        var a = animalRepo.findByCaravana(caravana)
                .orElseThrow(() -> new ResourceNotFoundException("Animal no encontrado con caravana: " + caravana));
        return toResponse(a);
    }

    @Transactional(readOnly = true)
    public List<AnimalResponseDto> getByRaza(String raza) {
        return animalRepo.getByRaza(raza).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AnimalResponseDto> getBySexo(Boolean sexo) {
        return animalRepo.getBySexo(Boolean.TRUE.equals(sexo)).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AnimalResponseDto> getByCorral_CorralId(Integer corralId) {
        return animalRepo.getByCorral_CorralId(corralId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AnimalResponseDto> getByEstadoSalud(String estadoSalud) {
        return animalRepo.getByEstadoSalud(estadoSalud).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AnimalResponseDto getAnimalWithHistory(String caravana) {
        var a = animalRepo.findWithHistoryByCaravana(caravana)
                .orElseThrow(() -> new ResourceNotFoundException("No encontrado"));
        return toResponse(a);
    }



    public RegistroTratamiento addTreatment(Long animalId, Insumo medicamento, float dosis) {
        var animal = animalRepo.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal no encontrado con id: " + animalId));

        var t = new RegistroTratamiento();
        t.setInsumo(medicamento);
        t.setDosis(dosis);
        t.setFecha(LocalDate.now(AR_CORDOBA));
        t.setAnimal(animal);

        animal.getTratamientos().add(t);
        animalRepo.save(animal);
        return t;
    }




    private AnimalResponseDto toResponse(Animal a) {
        return new AnimalResponseDto(
                a.getAnimalId(),
                a.getCaravana(),
                a.getRaza(),
                a.getSexo(),
                a.getEdadMeses(),
                a.getPesoInicial(),
                a.getPesoActual(),
                a.getEstadoSalud(),
                a.getEstado(),
                a.getCorral() != null ? a.getCorral().getCorralId() : null,
                a.getFechaIngreso(),
                a.getFechaNacimiento(),
                a.getProxTratamiento()
        );
    }


    // ------------ Calculo de la edad ------------

    private Integer calcEdadMeses(LocalDate fechaNac) {
        if (fechaNac == null) return null;
        var hoy = LocalDate.now(AR_CORDOBA);
        long mesesFloor = ChronoUnit.MONTHS.between(fechaNac, hoy);
        LocalDate prev = fechaNac.plusMonths(mesesFloor);
        LocalDate next = prev.plusMonths(1);
        long diasTrans = ChronoUnit.DAYS.between(prev, hoy);
        long diasTramo = ChronoUnit.DAYS.between(prev, next);
        if (diasTrans * 2 >= diasTramo) mesesFloor++;
        return (int) mesesFloor;
    }
}

