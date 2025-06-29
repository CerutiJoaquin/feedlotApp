package gestor.feedlotapp.service;

import gestor.feedlotapp.entities.RegistroComedero;
import gestor.feedlotapp.Repository.RegistroComederoRepository;
import gestor.feedlotapp.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RegistroComederoService {
    private final RegistroComederoRepository registroComederoRepository;
    public RegistroComederoService(RegistroComederoRepository registroComederoRepository){
        this.registroComederoRepository=registroComederoRepository;
    }
    @Transactional(readOnly = true)
    public List<RegistroComedero> getAll(){return registroComederoRepository.findAll();}
    @Transactional(readOnly = true)
    public RegistroComedero getById(Integer id){
        return registroComederoRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Registro Comedero no encontrado con id: "+ id));
    }
    public RegistroComedero create(RegistroComedero registroComedero){
        return registroComederoRepository.save(registroComedero);
    }
    public RegistroComedero update(Integer id, RegistroComedero registroComedero){
        return registroComederoRepository.findById(id)
                .map(existing->{
                    existing.setCantidad_consumida(registroComedero.getCantidad_consumida());
                    existing.setObservaciones(registroComedero.getObservaciones());
                    return registroComederoRepository.save(existing);
                })
                .orElseThrow(()->new ResourceNotFoundException("Registro Comedero no encontrado con id: "+ id));
    }
    public void delete(Integer id){
        RegistroComedero rc = registroComederoRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Registro Comedero no encontrado con id: "+ id));
        registroComederoRepository.delete(rc);
    }
}
