package gestor.feedlotapp.dto.predicciones;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ConsumoPrediccionDTO {
    private LocalDate fecha;
    private BigDecimal consumoTotalKg;
    private BigDecimal consumoPorCabezaKg;
    private boolean predicho;

    public ConsumoPrediccionDTO(LocalDate fecha, BigDecimal consumoTotalKg,
                                BigDecimal consumoPorCabezaKg, boolean predicho) {
        this.fecha = fecha;
        this.consumoTotalKg = consumoTotalKg;
        this.consumoPorCabezaKg = consumoPorCabezaKg;
        this.predicho = predicho;
    }

    public LocalDate getFecha() { return fecha; }
    public BigDecimal getConsumoTotalKg() { return consumoTotalKg; }
    public BigDecimal getConsumoPorCabezaKg() { return consumoPorCabezaKg; }
    public boolean isPredicho() { return predicho; }
}
