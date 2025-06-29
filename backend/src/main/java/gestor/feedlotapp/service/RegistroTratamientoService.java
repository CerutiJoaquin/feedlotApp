package gestor.feedlotapp.service;

import gestor.feedlotapp.entities.RegistroTratamiento;
import gestor.feedlotapp.Repository.RegistroTratamientoRepository;
import gestor.feedlotapp.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

@Service
@Transactional
public class RegistroTratamientoService {
    private static  RegistroTratamientoRepository registroTratamientoRepository;
    public RegistroTratamientoService(RegistroTratamientoRepository registroTratamientoRepository){
        this.registroTratamientoRepository=registroTratamientoRepository;
    }
    @Transactional(readOnly = true)
    public List<RegistroTratamiento> getAll(){return registroTratamientoRepository.findAll();}

    @Transactional(readOnly = true)
    public RegistroTratamiento getById(Integer id){
        return registroTratamientoRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Registro Tratamiento no encontrado con id: "+id));
    }

    public RegistroTratamiento create(RegistroTratamiento registroTratamiento){
        return registroTratamientoRepository.save(registroTratamiento);
    }
    public RegistroTratamiento update(Integer id, RegistroTratamiento registroTratamiento) {
        return registroTratamientoRepository.findById(id)
                .map(existing -> {
                    existing.setFecha(registroTratamiento.getFecha());
                    existing.setDosis(registroTratamiento.getDosis());
                   return registroTratamientoRepository.save(existing);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Registro Tratamiento no encontrado con id: " + id));
    }
    public void delete(Integer id){
        RegistroTratamiento rt = registroTratamientoRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Registro Tratamiento no encontrado con id: "+id));
        registroTratamientoRepository.delete(rt);
    }

    // Metodos especificos
    public static List<RegistroTratamiento> findByAnimalId(Integer animalId){
        return registroTratamientoRepository.findByAnimal_AnimalId(animalId);
    }

    @Transactional(readOnly = true)
    public List<RegistroTratamiento> findByAnimalCaravana(String caravana){
        return registroTratamientoRepository.findByAnimalCaravana(caravana);
    }

    @Transactional(readOnly = true)
    public List<RegistroTratamiento> findByFechaBetween(java.sql.Date desde, Date hasta){
        return registroTratamientoRepository.findByFechaBetween(desde,hasta);
    }
    @Transactional(readOnly = true)
    public List<RegistroTratamiento> findByAnimalIdAndFechaBetween(Integer animalId, Date desde, Date hasta){
        return registroTratamientoRepository.findByAnimal_AnimalIdAndFechaBetweenOrderByFechaAsc(animalId,desde,hasta);
    }

}
