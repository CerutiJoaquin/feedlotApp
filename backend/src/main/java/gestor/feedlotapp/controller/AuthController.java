package gestor.feedlotapp.controller;

import gestor.feedlotapp.service.AuthService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.net.URI;
import java.util.Map;

record RegisterRequest(
        @NotBlank String nombre,
        @NotBlank String apellido,
        @Email @NotBlank String email,
        @NotBlank String contrasenia) {}

record LoginRequest(
        @Email @NotBlank String email,
        @NotBlank String contrasenia) {}

record UserDTO(Integer usuarioId, String nombre, String apellido, String email) {}

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) { this.auth = auth; }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterRequest r) {
        var u = auth.register(r.nombre(), r.apellido(), r.email(), r.contrasenia());
        var dto = new UserDTO(u.getUsuarioId(), u.getNombre(), u.getApellido(), u.getEmail());
        return ResponseEntity.created(URI.create("/api/auth/users/" + u.getUsuarioId())).body(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest r) {
        return auth.login(r.email(), r.contrasenia())
                .<ResponseEntity<?>>map(u ->
                        ResponseEntity.ok(new UserDTO(u.getUsuarioId(), u.getNombre(), u.getApellido(), u.getEmail())))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Credenciales inválidas")));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,String>> handleIllegalArg(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", ex.getMessage()));
    }
}
