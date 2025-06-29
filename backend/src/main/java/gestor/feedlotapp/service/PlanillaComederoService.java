package gestor.feedlotapp.service;

import gestor.feedlotapp.entities.PlanillaComedero;
import gestor.feedlotapp.Repository.PlanillaComederoRepository;
import gestor.feedlotapp.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PlanillaComederoService {
    private final PlanillaComederoRepository planillaComederoRepository;
    public PlanillaComederoService(PlanillaComederoRepository planillaComederoRepository){
        this.planillaComederoRepository=planillaComederoRepository;
    }
    @Transactional(readOnly = true)
    public List<PlanillaComedero> getAll(){return planillaComederoRepository.findAll();}
    @Transactional(readOnly = true)
    public PlanillaComedero getById(Integer id){
        return planillaComederoRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Planilla Comedero no encontrada con id: " + id));
    }
    public PlanillaComedero create(PlanillaComedero planillaComedero){
        return planillaComederoRepository.save(planillaComedero);
    }
    public PlanillaComedero update(Integer id, PlanillaComedero planillaComedero){
        return planillaComederoRepository.findById(id)
                .map(existing->{
                    existing.setFecha(planillaComedero.getFecha());
                    existing.setEncargado(planillaComedero.getEncargado());
                    existing.setObservaciones(planillaComedero.getObservaciones());
                    return planillaComederoRepository.save(existing);
                })
                .orElseThrow(()->new ResourceNotFoundException("Planilla Comedero no encontrada con id: " + id));
    }
    public void delete(Integer id){
        PlanillaComedero pc = planillaComederoRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Planilla Comedero no encontrada con id: " + id));
        planillaComederoRepository.delete(pc);
    }
}
