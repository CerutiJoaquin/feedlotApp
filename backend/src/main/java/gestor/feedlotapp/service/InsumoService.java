package gestor.feedlotapp.service;


import java.util.List;

import gestor.feedlotapp.enums.insumo.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gestor.feedlotapp.dto.insumo.*;
import gestor.feedlotapp.entities.Insumo;
import gestor.feedlotapp.exception.ResourceNotFoundException;
import gestor.feedlotapp.repository.InsumoRepository;
@Service
@Transactional
public class InsumoService {

    private final InsumoRepository insumoRepo;

    public InsumoService(InsumoRepository insumoRepo){
        this.insumoRepo = insumoRepo;
    }

    @Transactional(readOnly = true)
    public List<InsumoResponseDto> getAllInsumos(){
        return insumoRepo.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public InsumoResponseDto getInsumoById(Integer id){
        var i = insumoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado con id: " + id));
        return toResponse(i);
    }

    public InsumoResponseDto createInsumo(InsumoCreateDto dto){
        if (dto == null) throw new IllegalArgumentException("Datos inválidos");
        if (isBlank(dto.nombre())) throw new IllegalArgumentException("El nombre es obligatorio");
        if (dto.cantidad() < 0)        throw new IllegalArgumentException("La cantidad no puede ser negativa");
        if (dto.cantidadMinima() < 0)  throw new IllegalArgumentException("La cantidad mínima no puede ser negativa");

        var i = new Insumo();
        i.setNombre(dto.nombre().trim());

        i.setCategoria(dto.categoria());
        i.setTipo(dto.tipo());
        i.setCantidad(dto.cantidad());
        i.setCantidadMinima(dto.cantidadMinima());
        i.setUnidadMedida(safeTrimOrNull(dto.unidadMedida()));
        if (dto.fechaIngreso() != null) i.setFechaIngreso(dto.fechaIngreso());
        if (dto.codigo() != null)       i.setCodigo(safeTrimOrNull(dto.codigo()));

        i = insumoRepo.save(i);
        return toResponse(i);
    }

    public InsumoResponseDto updateInsumo(Integer id, InsumoUpdateDto dto){
        var i = insumoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado con id: " + id));

        if (dto.cantidad() != null && dto.cantidad() < 0)
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        if (dto.cantidadMinima() != null && dto.cantidadMinima() < 0)
            throw new IllegalArgumentException("La cantidad mínima no puede ser negativa");

        if (dto.nombre() != null) {
            if (isBlank(dto.nombre())) throw new IllegalArgumentException("El nombre no puede quedar vacío");
            i.setNombre(dto.nombre().trim());
        }

        if (dto.categoria() != null) i.setCategoria(dto.categoria());
        if (dto.tipo() != null) i.setTipo(dto.tipo());
        if (dto.cantidad() != null) i.setCantidad(dto.cantidad());
        if (dto.cantidadMinima() != null) i.setCantidadMinima(dto.cantidadMinima());
        if (dto.unidadMedida() != null) i.setUnidadMedida(safeTrimOrNull(dto.unidadMedida()));

        i = insumoRepo.save(i);
        return toResponse(i);
    }

    public void deleteInsumo(Integer id){
        var i = insumoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado con id: " + id));
        insumoRepo.delete(i);
    }

    public List<InsumoAlertDto> getLowStockAlerts() {
        return insumoRepo.findAll().stream()
                .filter(i -> i.getCantidad() <= i.getCantidadMinima())
                .map(i -> new InsumoAlertDto(
                        i.getInsumoId(),
                        i.getNombre(),
                        i.getCantidad(),
                        i.getCantidadMinima(),
                        Math.max(0, i.getCantidadMinima() - i.getCantidad())
                ))
                .toList();
    }


    @Transactional(readOnly = true)
    public List<InsumoResponseDto> getInsumoByCategoria(Categoria categoria){
        return insumoRepo.findByCategoria(categoria).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<InsumoOptionDto> listMedicamentos(String q, Pageable pageable) {
        String term = (q == null) ? "" : q.trim();
        Page<Insumo> page = insumoRepo.findByCategoriaAndNombreContainingIgnoreCase(Categoria.MEDICAMENTO, term, pageable);
        if (page.isEmpty() && !term.isEmpty()) {
            page = insumoRepo.findByCategoriaAndCodigoContainingIgnoreCase(Categoria.MEDICAMENTO, term, pageable);
        }
        return page.map(i -> new InsumoOptionDto(i.getInsumoId(), i.getNombre(), i.getCodigo()));
    }

    // --- Helpers ---
    private InsumoResponseDto toResponse(Insumo i) {
        return new InsumoResponseDto(
                i.getInsumoId(),
                i.getNombre(),
                i.getCategoria(),
                i.getTipo(),
                i.getCantidad(),
                i.getCantidadMinima(),
                i.getUnidadMedida(),
                i.getFechaIngreso()
        );
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String safeTrimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
