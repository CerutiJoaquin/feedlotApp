package gestor.feedlotapp.service;

import gestor.feedlotapp.dto.pesaje.PesajeCreateDto;
import gestor.feedlotapp.dto.pesaje.PesajeUpdateDto;
import gestor.feedlotapp.dto.pesaje.PesajeResponseDto;
import gestor.feedlotapp.entities.Animal;
import gestor.feedlotapp.entities.Pesaje;
import gestor.feedlotapp.exception.ResourceNotFoundException;
import gestor.feedlotapp.repository.AnimalRepository;
import gestor.feedlotapp.repository.PesajeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PesajeService {

    private final PesajeRepository pesajeRepo;
    private final AnimalRepository animalRepo;

    public PesajeService(PesajeRepository pesajeRepo, AnimalRepository animalRepo){
        this.pesajeRepo = pesajeRepo;
        this.animalRepo = animalRepo;
    }


    @Transactional(readOnly = true)
    public List<PesajeResponseDto> getAll(){
        return pesajeRepo.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PesajeResponseDto getById(Long id){
        Pesaje p = pesajeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pesaje no encontrado con id: " + id));
        return toResponse(p);
    }

    public PesajeResponseDto create(PesajeCreateDto dto){
        if(dto == null) throw new IllegalArgumentException("Datos de pesajes inválidos");
        if (dto.peso() <= 0) throw new IllegalArgumentException("El peso debe ser mayor a 0");


        Long id = dto.animalId();
        String caravana = (dto.caravana() == null) ? null : dto.caravana().trim();

        boolean sinId = (id == null);
        boolean sinCaravana = (caravana == null || caravana.isEmpty());
        if(sinId && sinCaravana){
            throw new IllegalArgumentException("Debe indicar 'animalId' o 'caravana' ");
        }

        Animal animal;


        if(!sinId & !sinCaravana){
            Animal porId = animalRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Animal no encontrado con id: " + id));
            Animal porCaravana = animalRepo.findByCaravana(caravana)
                    .orElseThrow(() -> new ResourceNotFoundException("Animal no encontrado con caravana: " + caravana));

            if (!porId.getAnimalId().equals(porCaravana.getAnimalId())) {
                throw new IllegalArgumentException("El 'animalId' y la 'caravana' pertenecen a animales distintos");
            }
            animal = porId;
        } else if (!sinId){
            animal = animalRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Animal no encontrado con id: " + id));
        }else{
            animal = animalRepo.findByCaravana(caravana)
                    .orElseThrow(() -> new ResourceNotFoundException("Animal no encontrado con la caravana: " + caravana));
        }

        if (animal.getVentaDetalleBaja() != null) {
            throw new IllegalStateException("No se puede pesar un animal vendido.");
        }

        Pesaje p = new Pesaje();
        if(dto.fecha() != null) p.setFecha(dto.fecha());
        p.setPeso(dto.peso());
        p.setAnimal(animal);

        p = pesajeRepo.save(p);
        animal.setPesoActual(BigDecimal.valueOf(dto.peso()));
        return toResponse(p);

    }

    public PesajeResponseDto update(Long id, PesajeUpdateDto dto){
        Pesaje p = pesajeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pesaje no encontrado con id: " + id));
        if(dto.fecha() != null){
            p.setFecha(dto.fecha());
        }

        if (dto.peso()  != null) {
            if (dto.peso() <= 0) throw new IllegalArgumentException("El peso debe ser mayor a 0");
            p.setPeso(dto.peso());
        }
        if (dto.animalId() != null) {
            Animal animal = animalRepo.findById(dto.animalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Animal no encontrado con id: " + dto.animalId()));
            p.setAnimal(animal);
        }

        p = pesajeRepo.save(p);
        return toResponse(p);
    }

    public void delete(Long id){
        Pesaje p = pesajeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pesaje no encontrado con id: " + id));
        pesajeRepo.delete(p);
    }


    @Transactional(readOnly = true)
    public List<PesajeResponseDto> getByAnimalIdOrdered(Long animalId) {
        return pesajeRepo.findByAnimalAnimalIdOrderByFechaAsc(animalId)
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @Transactional(readOnly = true)
    public List<PesajeResponseDto> getPesajesByAnimalId(Long animalId) {
        return getByAnimalIdOrdered(animalId);
    }

    @Transactional(readOnly = true)
    public List<PesajeResponseDto> getByCaravanaOrdered(String caravana) {
        return pesajeRepo.findByAnimalCaravanaOrderByFechaAsc(caravana)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PesajeResponseDto> getBetweenDates(LocalDate desde, LocalDate hasta) {
        return pesajeRepo.findByFechaBetween(Date.valueOf(desde), Date.valueOf(hasta))
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @Transactional(readOnly = true)
    public List<PesajeResponseDto> getPesajesByAnimalIdOrdered(Long animalId) {
        return getByAnimalIdOrdered(animalId);
    }

    @Transactional(readOnly = true)
    public List<PesajeResponseDto> getPesajesByCaravanaOrdered(String caravana) {
        return getByCaravanaOrdered(caravana);
    }

    @Transactional(readOnly = true)
    public List<PesajeResponseDto> getPesajesBetweenDates(LocalDate desde, LocalDate hasta) {
        return getBetweenDates(desde, hasta);
    }


    private PesajeResponseDto toResponse(Pesaje p) {
        LocalDate fecha = (p.getFecha() != null) ? p.getFecha() : null;
        Long animalId = (p.getAnimal() != null) ? p.getAnimal().getAnimalId() : null;

        return new PesajeResponseDto(
                p.getPesajeId(),
                fecha,
                p.getPeso(),
                animalId
        );
    }
}
