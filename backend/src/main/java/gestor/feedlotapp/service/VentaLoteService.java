package gestor.feedlotapp.service;

import gestor.feedlotapp.entities.VentaLote;
import gestor.feedlotapp.Repository.VentaLoteRepository;
import gestor.feedlotapp.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VentaLoteService {
    private final VentaLoteRepository ventaLoteRepository;
    public VentaLoteService(VentaLoteRepository ventaLoteRepository){
        this.ventaLoteRepository=ventaLoteRepository;
    }

    @Transactional(readOnly = true)
    public List<VentaLote> getAll(){return ventaLoteRepository.findAll();}

    @Transactional(readOnly = true)
    public VentaLote getById(Integer id){
        return ventaLoteRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Venta Lote no encontrada con id: " + id));
    }

    public VentaLote create (VentaLote ventaLote){
        return ventaLoteRepository.save(ventaLote);
    }

    public VentaLote update(Integer id, VentaLote ventaLote){
        return ventaLoteRepository.findById(id)
                .map(existing->{
                    existing.setDescripcion(ventaLote.getDescripcion());
                    return ventaLoteRepository.save(existing);
                })
                .orElseThrow(()->new ResourceNotFoundException("Venta Lote no encontrada con id: " + id));
    }

    public void delete(Integer id){
        VentaLote vl = ventaLoteRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Venta Lote no encontrada con id: " + id));
        ventaLoteRepository.delete(vl);
    }
}
