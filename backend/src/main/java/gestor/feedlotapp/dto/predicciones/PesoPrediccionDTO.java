package gestor.feedlotapp.dto.predicciones;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PesoPrediccionDTO {
    private LocalDate fecha;
    private BigDecimal pesoKg;
    private boolean predicho;

    public PesoPrediccionDTO(LocalDate fecha, BigDecimal pesoKg, boolean predicho) {
        this.fecha = fecha;
        this.pesoKg = pesoKg;
        this.predicho = predicho;
    }

    public LocalDate getFecha() { return fecha; }
    public BigDecimal getPesoKg() { return pesoKg; }
    public boolean isPredicho() { return predicho; }
}
