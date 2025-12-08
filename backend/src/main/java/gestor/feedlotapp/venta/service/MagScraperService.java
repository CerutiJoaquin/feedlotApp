package gestor.feedlotapp.venta.service;

import gestor.feedlotapp.venta.dto.MagSyncResult;
import gestor.feedlotapp.venta.entities.MagPrecio;
import gestor.feedlotapp.venta.repository.MagPrecioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class MagScraperService {

    private final MagPrecioRepository repo;

    private static final String BASE =
            "https://www.mercadoagroganadero.com.ar/dll/hacienda1.dll/haciinfo000002";
    private static final String REF =
            "https://www.mercadoagroganadero.com.ar/dll/hacienda1.dll/haciinfo000002";
    private static final DateTimeFormatter ARG = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /* SCHEDULER  */
    @Scheduled(cron = "0 30 9 * * *", zone = "America/Argentina/Buenos_Aires")
    public void scheduled() {
        MagSyncResult r = fetchAndUpsertDefault();
        log.info("MAG cron -> {}", r);
    }

    public MagSyncResult fetchAndUpsertDefault() {
        LocalDate hoy = LocalDate.now();
        return fetchAndUpsertRange(hoy.minusDays(4), hoy);
    }

    public MagSyncResult fetchAndUpsertFor(LocalDate fecha) {
        return fetchAndUpsertRange(fecha.minusDays(4), fecha);
    }

    public MagSyncResult fetchAndUpsertRange(LocalDate desde, LocalDate hasta) {
        int intentados = 0, guardados = 0, actualizados = 0, saltados = 0;
        try {
            String sDesde = desde.format(ARG);
            String sHasta = hasta.format(ARG);

            Map<String, String> cookies = bootstrapCookies();
            Document doc = fetchPrintable(sDesde, sHasta, cookies);

            Element table = findResultTable(doc);
            if (table == null) {
                log.warn("Tabla MAG no encontrada ({} → {}). URL: {}", sDesde, sHasta,
                        BASE + "?txtFECHAINI=" + encode(sDesde) + "&txtFECHAFIN=" + encode(sHasta) + "&CP=&LISTADO=SI");
                return new MagSyncResult(hasta, 0, 0, 0, 0);
            }

            LocalDate fechaRef = hasta;

            for (Element tr : table.select("tr:has(td)")) {
                Elements tds = tr.select("td");
                if (tds.isEmpty()) { saltados++; continue; }

                String joinedLower = tds.text().toLowerCase();

                if (joinedLower.contains("totales")
                        || joinedLower.contains("mercado agroganadero")
                        || joinedLower.contains("imprimir")
                        || joinedLower.contains("cerrar")
                        || joinedLower.matches(".*precios\\s+por\\s+categoria.*")) {
                    saltados++;
                    continue;
                }

                String categoria = text(tds, 0).trim();
                if (categoria.isBlank()) { saltados++; continue; }


                BigDecimal precioProm = parseNumber(text(tds, 3));
                String kgsPromStr = tds.size() > 8 ? text(tds, 8) : text(tds, tds.size()-1);
                BigDecimal kgsProm  = parseNumber(kgsPromStr);


                if (precioProm == null || kgsProm == null) { saltados++; continue; }

                intentados++;
                UpsertOutcome out = upsertReturningOutcome(fechaRef, categoria, precioProm, kgsProm);
                if (out == UpsertOutcome.INSERT) guardados++;
                else if (out == UpsertOutcome.UPDATE) actualizados++;
            }

        } catch (Exception e) {
            log.error("Error al scrapear MAG ({} → {})", desde, hasta, e);
        }
        return new MagSyncResult(hasta, intentados, guardados, actualizados, saltados);
    }

    private enum UpsertOutcome { INSERT, UPDATE, NONE }

    @Transactional
    public UpsertOutcome upsertReturningOutcome(LocalDate fecha, String categoria,
                                                   BigDecimal precioProm, BigDecimal kgsProm) {
        return repo.findByFechaAndCategoria(fecha, categoria).map(existing -> {
            existing.setPrecioProm(precioProm);
            existing.setKgsProm(kgsProm);
            repo.save(existing);
            return UpsertOutcome.UPDATE;
        }).orElseGet(() -> {
            MagPrecio p = new MagPrecio();
            p.setFecha(fecha);
            p.setCategoria(categoria);
            p.setPrecioProm(precioProm);
            p.setKgsProm(kgsProm);
            p.setFuente("MAG");
            repo.save(p);
            return UpsertOutcome.INSERT;
        });
    }


    private Map<String, String> bootstrapCookies() throws Exception {
        Connection.Response resp = Jsoup.connect(REF)
                .method(Connection.Method.GET)
                .userAgent("Mozilla/5.0 (FeedlotGestor)")
                .timeout(30000)
                .execute();
        return resp.cookies();
    }

    private Document fetchPrintable(String desde, String hasta, Map<String, String> cookies) throws Exception {
        String url = BASE + "?txtFECHAINI=" + encode(desde)
                + "&txtFECHAFIN=" + encode(hasta)
                + "&CP=&LISTADO=SI";
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (FeedlotGestor)")
                .timeout(30000)
                .referrer(REF)
                .cookies(cookies)
                .get();
    }

    private Element findResultTable(Document doc) {
        Element t = doc.selectFirst("div.table-custom table.table");
        if (t != null) return t;

        for (Element table : doc.select("table")) {
            for (Element th : table.select("th")) {
                if ("categoria".equalsIgnoreCase(th.text().trim())) {
                    return table;
                }
            }
        }

        for (Element table : doc.select("table")) {
            boolean hasCategoria = table.select("th").stream().anyMatch(th -> "categoria".equalsIgnoreCase(th.text().trim()));
            boolean hasPrecios   = table.select("th").stream().anyMatch(th -> "precios".equalsIgnoreCase(th.text().trim()));
            if (hasCategoria && hasPrecios) return table;
        }

        return null;
    }



    private static String text(Elements tds, int i) {
        return i >= 0 && i < tds.size() ? tds.get(i).text().trim() : "";
    }


    private static BigDecimal parseNumber(String s) {
        if (s == null) return null;
        String x = s.replace("$", "").replace(" ", "")
                .replace(".", "").replace(",", ".");
        try {
            if (x.isBlank()) return null;
            return new BigDecimal(x);
        } catch (Exception e) {
            return null;
        }
    }

    private static String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
