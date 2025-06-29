package gestor.feedlotapp.service;

import gestor.feedlotapp.entities.Corral;
import gestor.feedlotapp.Repository.CorralRepository;
import gestor.feedlotapp.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CorralService {
    private final CorralRepository corralRepository;
    public CorralService (CorralRepository corralRepository){this.corralRepository = corralRepository;}

    @Transactional(readOnly = true)
    public List<Corral> getAll(){return corralRepository.findAll();}
    @Transactional(readOnly = true)
    public Corral getById(Integer id){
        return corralRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Corral no encontrado con id: " + id));
    }
    public Corral create(Corral corral){return corralRepository.save(corral);}
    public Corral corral(Integer id, Corral corral){
        return corralRepository.findById(id)
                .map(existing->{
                    existing.setNombre(corral.getNombre());
                    existing.setDescripcion(corral.getDescripcion());
                    existing.setCapacidadMin(corral.getCapacidadMin());
                    existing.setCapacidadMax(corral.getCapacidadMax());
                    existing.setTipoSuperficie(corral.getTipoSuperficie());
                    existing.setEstado(corral.getEstado());
                    return corralRepository.save(existing);
                })
                .orElseThrow(()->new ResourceNotFoundException("Corral no encontrado con id: " + id));
    }
    public void delete(Integer id){
        Corral c = corralRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Corral no encontrado con id: " + id));
        corralRepository.delete(c);
    }

}
