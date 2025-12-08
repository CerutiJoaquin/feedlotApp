package gestor.feedlotapp.service;

import gestor.feedlotapp.dto.cliente.*;
import gestor.feedlotapp.entities.Cliente;
import gestor.feedlotapp.exception.ResourceNotFoundException;
import gestor.feedlotapp.repository.ClienteRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ClienteService {

    private final ClienteRepository repo;

    public ClienteService(ClienteRepository repo){
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDto> getAll(){
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponseDto getById(Integer id){
        var c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
        return toResponse(c);
    }

    public ClienteResponseDto create(ClienteCreateDto dto){
        if (repo.existsByEmail(dto.email().trim())) {
            throw new IllegalArgumentException("Ya existe un cliente con ese email");
        }

        var c = new Cliente();
        c.setNombre(dto.nombre().trim());
        c.setApellido(dto.apellido().trim());
        c.setEmail(dto.email().trim());
        c.setCuit(dto.cuit().trim());
        c.setTelefono(dto.telefono().trim());

        c = repo.save(c);
        return toResponse(c);
    }

    public ClienteResponseDto update(Integer id, ClienteUpdateDto dto){
        var c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));

        if (dto.nombre()   != null) c.setNombre(dto.nombre().trim());
        if (dto.apellido() != null) c.setApellido(dto.apellido().trim());
        if (dto.email()    != null) {
            var email = dto.email().trim();
            if (!email.equalsIgnoreCase(c.getEmail()) && repo.existsByEmail(email)) {
                throw new IllegalArgumentException("Ya existe un cliente con ese email");
            }
            c.setEmail(email);
        }
        if (dto.cuit()     != null) c.setCuit(dto.cuit().trim());
        if (dto.telefono() != null) c.setTelefono(dto.telefono().trim());

        c = repo.save(c);
        return toResponse(c);
    }

    public void delete(Integer id){
        var c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
        repo.delete(c);
    }

    private ClienteResponseDto toResponse(Cliente c) {
        return new ClienteResponseDto(
                c.getClienteId(),
                c.getNombre(),
                c.getApellido(),
                c.getEmail(),
                c.getCuit(),
                c.getTelefono()
        );
    }
}
