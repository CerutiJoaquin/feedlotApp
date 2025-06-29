package gestor.feedlotapp.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gestor.feedlotapp.Repository.InsumoRepository;
import gestor.feedlotapp.entities.Insumo;
import gestor.feedlotapp.exception.ResourceNotFoundException;

@Service
@Transactional
public class InsumoService {
    private final InsumoRepository insumoRepository;
    public InsumoService(InsumoRepository insumoRepository){this.insumoRepository=insumoRepository;}
    @Transactional(readOnly = true)
    public List<Insumo> getAll(){return insumoRepository.findAll();}
    @Transactional(readOnly = true)
    public Insumo getById(Integer id){
        return insumoRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Insumo no encontrado con id: " + id));
    }
    public Insumo create(Insumo insumo){return insumoRepository.save(insumo);}
    public Insumo update(Integer id, Insumo insumo){
        return insumoRepository.findById(id)
                .map(existing->{
                    existing.setNombre(insumo.getNombre());
                    existing.setTipo(insumo.getTipo());
                    existing.setCantidadActual(insumo.getCantidadActual());
                    existing.setCantidadMinima(insumo.getCantidadMinima());
                    existing.setUnidadMedida(insumo.getUnidadMedida());
                    return insumoRepository.save(existing);
                })
                .orElseThrow(()->new ResourceNotFoundException("Insumo no encontrado con id: " + id));
    }
    public void delete(Integer id){
        Insumo i = insumoRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Insumo no encontrado con id: " + id));
        insumoRepository.delete(i);
    }

}
