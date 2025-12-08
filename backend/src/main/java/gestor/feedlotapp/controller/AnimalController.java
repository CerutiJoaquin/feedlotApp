package gestor.feedlotapp.controller;

import java.net.URI;
import java.util.List;

import gestor.feedlotapp.dto.pesaje.PesajeResponseDto;
import gestor.feedlotapp.dto.registrotratamiento.RegistroTratamientoResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import gestor.feedlotapp.dto.animal.*;

import gestor.feedlotapp.entities.RegistroTratamiento;

import gestor.feedlotapp.exception.ResourceNotFoundException;
import gestor.feedlotapp.service.AnimalService;
import gestor.feedlotapp.service.PesajeService;
import gestor.feedlotapp.service.RegistroTratamientoService;

import gestor.feedlotapp.dto.animal.*;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/animal")
public class AnimalController {

    private final AnimalService animalService;
    private final PesajeService pesajeService;
    private final RegistroTratamientoService registroTratamientoService;

    public AnimalController(AnimalService animalService,
                            PesajeService pesajeService,
                            RegistroTratamientoService registroTratamientoService) {
        this.animalService = animalService;
        this.pesajeService = pesajeService;
        this.registroTratamientoService = registroTratamientoService;
    }

    @GetMapping
    public ResponseEntity<List<AnimalResponseDto>> getAllAnimales() {
        return ResponseEntity.ok(animalService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalResponseDto> getAnimalById(@PathVariable Long id) {
        return ResponseEntity.ok(animalService.getById(id));
    }

    @PostMapping
    public ResponseEntity<AnimalResponseDto> createAnimal(@Valid @RequestBody AnimalCreateDto dto) {
        var creado = animalService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.animalId()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnimalResponseDto> updateAnimal(@PathVariable Long id,
                                                          @Valid @RequestBody AnimalUpdateDto dto) {
        return ResponseEntity.ok(animalService.updateDatos(id, dto));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<AnimalResponseDto> updateFechaTrat(@PathVariable Long id,
                                                          @Valid @RequestBody AnimalUpdateTratDto dto) {
        return ResponseEntity.ok(animalService.updateFechaTrat(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnimal(@PathVariable Long id) {
        try {
            animalService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/buscar")
    public ResponseEntity<List<AnimalResponseDto>> searchByCaravana(@RequestParam("fragmento") String fragmento) {
        return ResponseEntity.ok(animalService.searchByCaravana(fragmento));
    }

    @GetMapping("/caravana/{caravana}")
    public ResponseEntity<AnimalResponseDto> getAnimalByCaravana(@PathVariable String caravana) {
        return ResponseEntity.ok(animalService.getByCaravana(caravana));
    }

    @GetMapping(params = "raza")
    public ResponseEntity<List<AnimalResponseDto>> getAnimalByRaza(@RequestParam String raza) {
        return ResponseEntity.ok(animalService.getByRaza(raza));
    }

    @GetMapping(params = "sexo")
    public ResponseEntity<List<AnimalResponseDto>> getAnimalBySexo(@RequestParam boolean sexo) {
        return ResponseEntity.ok(animalService.getBySexo(sexo));
    }

    @GetMapping(params = "estadoSalud")
    public ResponseEntity<List<AnimalResponseDto>> getAnimalByEstadoSalud(@RequestParam String estadoSalud) {
        return ResponseEntity.ok(animalService.getByEstadoSalud(estadoSalud));
    }

    @GetMapping(params = "corralId")
    public ResponseEntity<List<AnimalResponseDto>> getAnimalByCorral(@RequestParam("corralId") Integer corralId) {
        return ResponseEntity.ok(animalService.getByCorral_CorralId(corralId));
    }

    @GetMapping("/trace/{caravana}")
    public ResponseEntity<AnimalResponseDto> trace(@PathVariable String caravana) {
        return ResponseEntity.ok(animalService.getAnimalWithHistory(caravana));
    }


    @GetMapping("/{id}/pesajes")
    public ResponseEntity<List<PesajeResponseDto>> getPesajesByAnimal(@PathVariable("id") Long animalId) {
        return ResponseEntity.ok(pesajeService.getByAnimalIdOrdered(animalId));
    }


    @GetMapping("/{id}/tratamientos")
    public ResponseEntity<List<RegistroTratamientoResponseDto>> getTratamientosByAnimal(@PathVariable("id") Long animalId) {
        return ResponseEntity.ok(registroTratamientoService.findByAnimalId(animalId));
    }

    @PostMapping("/{id}/tratamiento")
    public ResponseEntity<RegistroTratamiento> createTratamiento(@PathVariable Long id,
                                                                 @Valid @RequestBody RegistroTratamiento body) {
        var t = animalService.addTreatment(id, body.getInsumo(), body.getDosis());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(t.getRegistroTratamientoId()).toUri();
        return ResponseEntity.created(location).body(t);
    }
}
