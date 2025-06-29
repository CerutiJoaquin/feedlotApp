package gestor.feedlotapp.service;

import gestor.feedlotapp.entities.PlanillaTratamiento;
import gestor.feedlotapp.Repository.PlanillaTratamientoRepository;
import gestor.feedlotapp.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PlanillaTratamientoService {
    private final PlanillaTratamientoRepository planillaTratamientoRepository;
    public PlanillaTratamientoService(PlanillaTratamientoRepository planillaTratamientoRepository){
        this.planillaTratamientoRepository=planillaTratamientoRepository;
    }
    @Transactional(readOnly = true)
    public List<PlanillaTratamiento> getAll(){return planillaTratamientoRepository.findAll();}
    @Transactional(readOnly = true)
    public PlanillaTratamiento findById(Integer id){
        return planillaTratamientoRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Planilla Tratamiento no encontrada con id: "+id));
    }
    public PlanillaTratamiento create(PlanillaTratamiento planillaTratamiento){
        return planillaTratamientoRepository.save(planillaTratamiento);
    }
    public PlanillaTratamiento update(Integer id, PlanillaTratamiento planillaTratamiento){
        return planillaTratamientoRepository.findById(id)
                .map(existing->{
                    existing.setFecha(planillaTratamiento.getFecha());
                    existing.setObservaciones(planillaTratamiento.getObservaciones());
                    existing.setResponsable(planillaTratamiento.getResponsable());
                    return planillaTratamientoRepository.save(existing);
                })
                .orElseThrow(()->new ResourceNotFoundException("Planilla Tratamiento no encontrada con id: "+id));
    }
    public void delete(Integer id){
        PlanillaTratamiento pt = planillaTratamientoRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Planilla Tratamiento no encontrada con id: "+id));
        planillaTratamientoRepository.delete(pt);
    }
    @Transactional(readOnly = true)
    public Optional<PlanillaTratamiento> findByFecha(Date fecha){
        return planillaTratamientoRepository.findByFecha(fecha);
    };
    @Transactional(readOnly = true)
   public List<PlanillaTratamiento> findByFechaBetween(Date desde, Date hasta){
        return planillaTratamientoRepository.findByFechaBetween(desde,hasta);
    };

}
