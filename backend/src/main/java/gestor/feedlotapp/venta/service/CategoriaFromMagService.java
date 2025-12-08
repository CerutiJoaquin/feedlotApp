package gestor.feedlotapp.venta.service;

import gestor.feedlotapp.venta.entities.MagPrecio;
import gestor.feedlotapp.venta.repository.MagPrecioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CategoriaFromMagService {

    private final MagPrecioRepository magRepo;

    private static final Pattern RANGO = Pattern.compile("(\\d{2,4})\\s*/\\s*(\\d{2,4})");

    public static record RangoCat(String categoria, BigDecimal min, BigDecimal max, BigDecimal kgsProm){}

    public String resolverCategoria(LocalDate fecha, BigDecimal pesoKg) {

        LocalDate f = magRepo.maxFechaHasta(
                (fecha == null) ? LocalDate.now() : fecha
        ).orElseThrow(() -> new IllegalStateException("No hay precios MAG cargados."));

        final BigDecimal pesoEval = (pesoKg != null) ? pesoKg : BigDecimal.ZERO;


        List<MagPrecio> filas = magRepo.findAllByFecha(f);
        if (filas.isEmpty()) throw new IllegalStateException("No hay categorías MAG para la fecha " + f);

        List<RangoCat> rangos = new ArrayList<>();
        for (MagPrecio m : filas) {
            Matcher mm = RANGO.matcher(m.getCategoria());
            if (mm.find()) {
                BigDecimal min = new BigDecimal(mm.group(1));
                BigDecimal max = new BigDecimal(mm.group(2));
                rangos.add(new RangoCat(m.getCategoria(), min, max, m.getKgsProm()));
            } else {
                rangos.add(new RangoCat(m.getCategoria(), null, null, m.getKgsProm()));
            }
        }


        RangoCat exact = rangos.stream()
                .filter(r -> r.min() != null && r.max() != null
                        && pesoEval.compareTo(r.min()) >= 0
                        && pesoEval.compareTo(r.max()) <= 0)
                .min(Comparator.comparing(r -> r.max().subtract(r.min())))
                .orElse(null);
        if (exact != null) return exact.categoria();


        RangoCat cercano = rangos.stream()
                .filter(r -> r.kgsProm() != null)
                .min(Comparator.comparing(r -> r.kgsProm().subtract(pesoEval).abs()))
                .orElseThrow(() -> new IllegalStateException("Sin categoría MAG cercana"));
        return cercano.categoria();
    }

}
