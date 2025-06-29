package gestor.feedlotapp.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gestor.feedlotapp.Repository.AnimalRepository;
import gestor.feedlotapp.entities.Animal;
import gestor.feedlotapp.entities.Insumo;
import gestor.feedlotapp.entities.RegistroTratamiento;
import gestor.feedlotapp.exception.ResourceNotFoundException;


@Service
public class AnimalService{
    private final AnimalRepository animalRepository;

    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    @Transactional(readOnly = true)
    public List<Animal> getAll() {
        return animalRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Animal getById(Integer id) {
        return animalRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Animal no encontrado con id: " + id));
    }

    public Animal create(Animal animal) {
        return animalRepository.save(animal);
    }

    public Animal update(Integer id, Animal animal) {
        return animalRepository.findById(id)
                .map(existing -> {
                    existing.setCaravana(animal.getCaravana());
                    existing.setRaza(animal.getRaza());
                    existing.setEdad(animal.getEdad());
                    existing.setPesoActual(animal.getPesoActual());
                    existing.setSexo(animal.getSexo());
                    existing.setEstadoSalud(animal.getEstadoSalud());
                    return animalRepository.save(existing);
                })
                .orElseThrow(()->new ResourceNotFoundException("Animal no encontrado con id: " + id));
    }

    public void delete(Integer id) {
        Animal a = animalRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Animal no encontrado con id: " + id));
        animalRepository.delete(a);
    }

    @Transactional(readOnly = true)
    public List<Animal> searchByCaravana(String fragmento){
        return animalRepository.findByCaravanaContainingIgnoreCase(fragmento);
    }

    // Métodos específicos
    @Transactional(readOnly = true)
    public Animal getByCaravana(String caravana) {
        return animalRepository.getByCaravana(caravana)
                .orElseThrow(()->new ResourceNotFoundException("Animal no encontrado con caravana: " + caravana));
    }
    @Transactional(readOnly = true)
    public List<Animal> getByRaza(String raza){
        return animalRepository.getByRaza(raza);
    }
    @Transactional(readOnly = true)
    public List<Animal> getBySexo(Boolean sexo){
        return animalRepository.getBySexo(sexo);
    }
    @Transactional(readOnly = true)
    public List<Animal> getByCorral_CorralId(Integer corral){
        return animalRepository.getByCorral_CorralId(corral);
    }
    @Transactional(readOnly = true)
    public List<Animal> getByEstadoSalud(String estadoSalud){
        return animalRepository.getByEstadoSalud(estadoSalud);
    }
    @Transactional
    public RegistroTratamiento addTreatment(int animalId, Insumo medicamento, float dosis) {
        Animal animal = animalRepository.findById(animalId)
            .orElseThrow(() -> 
                new ResourceNotFoundException("Animal no encontrado con id: " + animalId));

        RegistroTratamiento tratamiento = new RegistroTratamiento();
        tratamiento.setMedicamento(medicamento);
        tratamiento.setDosis(dosis);
        tratamiento.setFecha(Date.from(LocalDate.now().atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant()));    
        tratamiento.setAnimal(animal);

        animal.getTratamientos().add(tratamiento);
        animalRepository.save(animal);

        return tratamiento;
    }
   @Transactional(readOnly=true)
public Animal getAnimalWithHistory(String caravana) {
  return animalRepository.findWithHistoryByCaravana(caravana)
    .orElseThrow(() -> new ResourceNotFoundException("No encontrado"));
}

}
