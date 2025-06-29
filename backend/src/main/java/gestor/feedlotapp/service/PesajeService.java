package gestor.feedlotapp.service;

import java.sql.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gestor.feedlotapp.Repository.PesajeRepository;
import gestor.feedlotapp.entities.Pesaje;
import gestor.feedlotapp.exception.ResourceNotFoundException;

@Service
@Transactional
public class PesajeService {
    private static  PesajeRepository pesajeRepository;
    public PesajeService(PesajeRepository pesajeRepository){this.pesajeRepository=pesajeRepository;}

    @Transactional(readOnly = true)
    public List<Pesaje> getAll(){return pesajeRepository.findAll();}
    @Transactional(readOnly = true)
    public Pesaje getById(Integer id){return pesajeRepository.findById(id)
            .orElseThrow(()->new ResourceNotFoundException("Pesaje no encontrado con id: " + id));
    }
    
    public static List<Pesaje> getPesajesByAnimalId(int animalId) {
        return pesajeRepository.findByAnimal_AnimalId(animalId);
    }
    public Pesaje create(Pesaje pesaje){return pesajeRepository.save(pesaje);}
    public Pesaje update(Integer id, Pesaje pesaje){
        return pesajeRepository.findById(id)
                .map(existing->{
                    existing.setFecha(pesaje.getFecha());
                    existing.setPeso(pesaje.getPeso());
                    existing.setObservaciones(pesaje.getObservaciones());
                    return pesajeRepository.save(existing);
                })
                .orElseThrow(()->new ResourceNotFoundException("Pesaje no encontrado con id: " + id));
    }
    public void delete(Integer id){
        Pesaje p = pesajeRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Pesaje no encontrado con id: " + id));
        pesajeRepository.delete(p);
    }
    @Transactional(readOnly = true)
    public List<Pesaje> getPesajesByAnimalIdOrdered(Integer animalId) {
        return pesajeRepository.findByAnimalAnimalIdOrderByFechaAsc(animalId);
    }
    @Transactional(readOnly = true)
    public List<Pesaje> getPesajesByCaravanaOrdered(String caravana) {
        return pesajeRepository.findByAnimalCaravanaOrderByFechaAsc(caravana);
    }
    @Transactional(readOnly = true)
    public List<Pesaje> getPesajesBetweenDates(Date desde, Date hasta) {
        return pesajeRepository.findByFechaBetween(desde, hasta);
    }

}
