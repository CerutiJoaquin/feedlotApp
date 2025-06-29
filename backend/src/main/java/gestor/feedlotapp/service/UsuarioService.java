package gestor.feedlotapp.service;

import gestor.feedlotapp.entities.Usuario;
import gestor.feedlotapp.Repository.UsuarioRepository;
import gestor.feedlotapp.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    public UsuarioService(UsuarioRepository usuarioRepository){
        this.usuarioRepository=usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Usuario> getAll(){return usuarioRepository.findAll();}

    @Transactional(readOnly = true)
    public Usuario getById(Integer id){
        return usuarioRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Usuario no encontrado con id: " +id));
    }

    public Usuario create (Usuario usuario){
        return usuarioRepository.save(usuario);
    }
    public Usuario update(Integer id, Usuario usuario){
        return usuarioRepository.findById(id)
                .map(existing->{
                    existing.setNombre(usuario.getNombre());
                    existing.setApellido(usuario.getApellido());
                    existing.setEmail(usuario.getEmail());
                    existing.setContrasenia(usuario.getContrasenia());
                    return usuarioRepository.save(existing);
                })
                .orElseThrow(()-> new ResourceNotFoundException("Usuario no encontrado con id: " +id));
    }

    public void delete(Integer id){
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Usuario no encontrado con id: " +id));
        usuarioRepository.delete(u);
    }
}
