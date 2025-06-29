package gestor.feedlotapp.service;

import gestor.feedlotapp.entities.RegistroRecorrido;
import gestor.feedlotapp.Repository.RegistroRecorridoRepository;
import gestor.feedlotapp.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RegistroRecorridoService {
    private final RegistroRecorridoRepository registroRecorridoRepository;
    public RegistroRecorridoService(RegistroRecorridoRepository registroRecorridoRepository){
        this.registroRecorridoRepository=registroRecorridoRepository;
    }
    @Transactional(readOnly = true)
    public List<RegistroRecorrido> getAll(){return registroRecorridoRepository.findAll();}
    @Transactional(readOnly = true)
    public RegistroRecorrido getById(Integer id){
        return registroRecorridoRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Registro Recorrido no encontrado con id: " + id));
    }
    public RegistroRecorrido create(RegistroRecorrido registroRecorrido){
        return registroRecorridoRepository.save(registroRecorrido);
    }
    public RegistroRecorrido update(Integer id, RegistroRecorrido registroRecorrido){
        return registroRecorridoRepository.findById(id)
                .map(existing->{
                    existing.setObservaciones(registroRecorrido.getObservaciones());
                    return registroRecorridoRepository.save(existing);
                })
                .orElseThrow(()-> new ResourceNotFoundException("Registro Recorrido no encontrado con id: " + id));
    }
    public void delete(Integer id){
        RegistroRecorrido rr = registroRecorridoRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Registro Recorrido no encontrado con id: " + id));
        registroRecorridoRepository.delete(rr);
    }
}
