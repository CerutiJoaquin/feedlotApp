package gestor.feedlotapp.controller;

import gestor.feedlotapp.entities.Usuario;
import gestor.feedlotapp.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/usuario")
@Validated
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> getAll() {
        return ResponseEntity.ok(usuarioService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Usuario> create(@Valid @RequestBody Usuario usuario) {
        Usuario creado = usuarioService.create(usuario);
        URI location = URI.create("/api/usuario/" + creado.getUsuarioId());
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> update(
            @PathVariable Integer id,
            @Valid @RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.update(id, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
