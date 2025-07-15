package gestor.feedlotapp.controller;

import java.net.URI;
import java.util.List;

import org.springframework.data.repository.query.parser.Part;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import gestor.feedlotapp.entities.Animal;
import gestor.feedlotapp.entities.Pesaje;
import gestor.feedlotapp.entities.RegistroTratamiento;
import gestor.feedlotapp.exception.ResourceNotFoundException;
import gestor.feedlotapp.service.AnimalService;
import gestor.feedlotapp.service.PesajeService;
import gestor.feedlotapp.service.RegistroTratamientoService;
import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/animal")
public class AnimalController {

        private final AnimalService animalService;

        public AnimalController(AnimalService animalService) {
                this.animalService = animalService;
        }


        @GetMapping
        public ResponseEntity<List<Animal>> getAllAnimales() {
                List<Animal> lista = animalService.getAll();
                return ResponseEntity.ok(lista);
        }


        @GetMapping("/{id}")
        public ResponseEntity<Animal> getAnimalById(@PathVariable int id) {
                Animal animal = animalService.getById(id);
                return animal != null
                        ? ResponseEntity.ok(animal)
                        : ResponseEntity.notFound().build();
        }


        @PostMapping
        public ResponseEntity<Animal> createAnimal(@Valid @RequestBody Animal animal) {
                Animal creado = animalService.create(animal);
                URI location = ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(creado.getAnimalId())
                        .toUri();
                return ResponseEntity.created(location).body(creado);
        }


        @PutMapping("/{id}")
        public ResponseEntity<Animal> updateAnimal(
                @PathVariable int id,
                @Valid @RequestBody Animal animal) {
                Animal actualizado = animalService.update(id, animal);
                return actualizado != null
                        ? ResponseEntity.ok(actualizado)
                        : ResponseEntity.notFound().build();
        }


        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteAnimal(@PathVariable int id) {
               try{
                       animalService.delete(id);
                       return ResponseEntity.noContent().build();
               }catch (ResourceNotFoundException ex){
                       return ResponseEntity.notFound().build();
               }
        }


        @GetMapping("/buscar")
        public ResponseEntity<List<Animal>> searchByCaravana(
                @RequestParam("fragmento") String fragmento) {
                List<Animal> resultados = animalService.searchByCaravana(fragmento);
                return ResponseEntity.ok(resultados);
        }


        @GetMapping("/caravana/{caravana}")
        public ResponseEntity<Animal> getAnimalByCaravana(@PathVariable String caravana) {
                Animal animal = animalService.getByCaravana(caravana);
                return animal != null
                        ? ResponseEntity.ok(animal)
                        : ResponseEntity.notFound().build();
        }


        @GetMapping(params = "raza")
        public ResponseEntity<List<Animal>> getAnimalByRaza(@RequestParam String raza) {
                return ResponseEntity.ok(animalService.getByRaza(raza));
        }


        @GetMapping(params = "sexo")
        public ResponseEntity<List<Animal>> getAnimalBySexo(@RequestParam boolean sexo) {
                return ResponseEntity.ok(animalService.getBySexo(sexo));
        }


        @GetMapping(params = "estadoSalud")
        public ResponseEntity<List<Animal>> getAnimalByEstadoSalud(
                @RequestParam String estadoSalud) {
                return ResponseEntity.ok(animalService.getByEstadoSalud(estadoSalud));
        }


        @GetMapping(params = "corralId")
        public ResponseEntity<List<Animal>> getAnimalByCorral(
                @RequestParam("corralId") Integer corralId) {
                return ResponseEntity.ok(
                        animalService.getByCorral_CorralId(corralId)
                );
        }

        @PostMapping("/{id}/tratamiento")
        public ResponseEntity<RegistroTratamiento> createTratamiento(
        @PathVariable int id,
        @Valid @RequestBody RegistroTratamiento tratamientoDto) {
            RegistroTratamiento t = animalService.addTreatment(
                id,
                tratamientoDto.getMedicamento(),
                tratamientoDto.getDosis()
            );
                URI location = ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(t.getRegistroTratamientoId())
                        .toUri();

              return ResponseEntity.created(location).body(t);
        }

      @GetMapping("/trace/{caravana}")
public ResponseEntity<Animal> trace(@PathVariable String caravana) {
  Animal a = animalService.getAnimalWithHistory(caravana);
  return ResponseEntity.ok(a);
}

    @GetMapping("/{id}/pesajes")
    public ResponseEntity<List<Pesaje>> getPesajesByAnimal(@PathVariable("id") int animalId) {
        List<Pesaje> list = PesajeService.getPesajesByAnimalId(animalId);
        return ResponseEntity.ok(list);
    }

    
    @GetMapping("/{id}/tratamientos")
    public ResponseEntity<List<RegistroTratamiento>> getTratamientosByAnimal(@PathVariable("id") Integer animalId) {
        List<RegistroTratamiento> list = RegistroTratamientoService.findByAnimalId(animalId);
        return ResponseEntity.ok(list);
    }
}