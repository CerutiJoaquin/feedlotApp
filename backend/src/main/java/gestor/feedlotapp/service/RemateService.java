package gestor.feedlotapp.service;

import gestor.feedlotapp.entities.Remate;
import gestor.feedlotapp.Repository.RemateRepository;
import gestor.feedlotapp.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RemateService {
    private final RemateRepository remateRepository;
    public RemateService(RemateRepository remateRepository){
        this.remateRepository=remateRepository;
    }

    @Transactional(readOnly = true)
    public List<Remate> getAll(){return remateRepository.findAll();}

    @Transactional(readOnly = true)
    public Remate findById(Integer id){
        return remateRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Remate no encontrado con id: "+id));
    }

    public Remate create(Remate remate){
        return remateRepository.save(remate);
    }

    public Remate update(Integer id, Remate remate){
        return remateRepository.findById(id)
                .map(existing->{
                    existing.setFecha(remate.getFecha());
                    existing.setUbicacion(remate.getUbicacion());
                    existing.setConsignatario(remate.getConsignatario());
                    existing.setDetalles(remate.getDetalles());
                    return remateRepository.save(existing);
                })
                .orElseThrow(()->new ResourceNotFoundException("Remate no encontrado con id: "+id));
    }
    public void delete(Integer id){
        Remate r = remateRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Remate no encontrado con id: "+id));
        remateRepository.delete(r);
    }
}
