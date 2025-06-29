package gestor.feedlotapp.service;

import gestor.feedlotapp.entities.Venta;
import gestor.feedlotapp.Repository.VentaRepository;
import gestor.feedlotapp.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VentaService {
    private final VentaRepository ventaRepository;
    public VentaService(VentaRepository ventaRepository){
        this.ventaRepository=ventaRepository;
    }
    @Transactional(readOnly = true)
    public List<Venta> getAll(){return ventaRepository.findAll();}

    @Transactional(readOnly = true)
    public Venta getById(Integer id){
        return ventaRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Venta no encontrada con id: " + id));
    }

    public Venta create(Venta venta){
        return ventaRepository.save(venta);
    }

    public Venta update(Integer id, Venta venta){
        return ventaRepository.findById(id)
                .map(existing->{
                    existing.setFecha(venta.getFecha());
                    existing.setLote(venta.getLote());
                    existing.setMonto(venta.getMonto());
                    return ventaRepository.save(existing);
                })
                .orElseThrow(()->new ResourceNotFoundException("Venta no encontrada con id: " + id));
    }
    public void delete(Integer id){
        Venta v = ventaRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Venta no encontrada con id: " + id));
        ventaRepository.delete(v);
    }
}
