package gestor.feedlotapp.dto.predicciones;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ConsumoMensualDTO {
    private LocalDate mes; // a partir del primer dia del mes
    private BigDecimal consumoKg;

    public ConsumoMensualDTO(LocalDate mes, BigDecimal consumoKg) {
        this.mes = mes;
        this.consumoKg = consumoKg;
    }

    public LocalDate getMes() {
        return mes;
    }

    public BigDecimal getConsumoKg() {
        return consumoKg;
    }
}
