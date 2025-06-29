package gestor.feedlotapp.service;

import gestor.feedlotapp.entities.PlanillaRecorrido;
import gestor.feedlotapp.Repository.PlanillaRecorridoRepository;
import gestor.feedlotapp.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PlanillaRecorridoService {
    private final PlanillaRecorridoRepository planillaRecorridoRepository;
    public PlanillaRecorridoService(PlanillaRecorridoRepository planillaRecorridoRepository){
        this.planillaRecorridoRepository=planillaRecorridoRepository;
    }
    @Transactional(readOnly = true)
    public List<PlanillaRecorrido> getAll(){return planillaRecorridoRepository.findAll();}
    @Transactional(readOnly = true)
    public PlanillaRecorrido getById(Integer id){
        return planillaRecorridoRepository.findById(id)
                .orElseThrow(
                        ()->new ResourceNotFoundException("Planilla Recorrido no encontrado con id: " + id)
                );
    }
    public PlanillaRecorrido create(PlanillaRecorrido planillaRecorrido){
        return planillaRecorridoRepository.save(planillaRecorrido);
    }
    public PlanillaRecorrido update(Integer id, PlanillaRecorrido planillaRecorrido){
        return planillaRecorridoRepository.findById(id)
                .map(existing->{
                    existing.setFecha(planillaRecorrido.getFecha());
                    existing.setObservaciones(planillaRecorrido.getObservaciones());
                    existing.setResponsable(planillaRecorrido.getResponsable());
                    return planillaRecorridoRepository.save(existing);
                })
                .orElseThrow(
                        ()->new ResourceNotFoundException("Planilla Recorrido no encontrado con id: " + id)
                );
    }
   public void delete(Integer id){
        PlanillaRecorrido pr = planillaRecorridoRepository.findById(id)
                .orElseThrow(
                        ()->new ResourceNotFoundException("Planilla Recorrido no encontrado con id: " + id)
                );
        planillaRecorridoRepository.delete(pr);
   }
}
