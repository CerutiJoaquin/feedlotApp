package gestor.feedlotapp.venta.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Data @AllArgsConstructor
public class MagSyncResult {
    private LocalDate fecha;
    private int intentados;
    private int guardados;
    private int actualizados;
    private int saltados;
}

