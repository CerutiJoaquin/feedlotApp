package gestor.feedlotapp.service;

import gestor.feedlotapp.entities.Cliente;
import gestor.feedlotapp.Repository.ClienteRepository;
import gestor.feedlotapp.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ClienteService {
    private final ClienteRepository clienteRepository;
    public ClienteService(ClienteRepository clienteRepository){this.clienteRepository=clienteRepository;}

    @Transactional(readOnly = true)
    public List<Cliente> getAll(){
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente getById(Integer id){

        return clienteRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Cliente no encontrado con id: " + id));
    }

    public Cliente create(Cliente cliente){
        return clienteRepository.save(cliente);
    }

    public Cliente update(Integer id, Cliente cliente){
        return clienteRepository.findById(id)
                .map(existing->{
                    existing.setNombre(cliente.getNombre());
                    existing.setApellido(cliente.getApellido());
                    existing.setEmail(cliente.getEmail());
                    existing.setCuit(cliente.getCuit());
                    existing.setTelefono(cliente.getTelefono());
                    return clienteRepository.save(existing);
                })
                .orElseThrow(()->new ResourceNotFoundException("Cliente no encontrado con id: " + id));
    }

    public void delete(Integer id){
        Cliente c = clienteRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Cliente no encontrado con id: " + id));
        clienteRepository.delete(c);
    }
}
