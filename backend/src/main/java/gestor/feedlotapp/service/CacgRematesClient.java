package gestor.feedlotapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gestor.feedlotapp.dto.remate.RemateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class CacgRematesClient {

    private final ObjectMapper om = new ObjectMapper();

    @Value("${cacg.remates.url}")           private String url;
    @Value("${cacg.remates.limit:50}")      private int limit;
    @Value("${cacg.remates.max-pages:11}")  private int maxPagesFallback;
    @Value("${cacg.remates.concurrency:4}") private int concurrency;
    @Value("${cacg.remates.cache-minutes:10}") private int cacheMinutes;

    @Value("${cacg.remates.headers.origin:}")          private String hOrigin;
    @Value("${cacg.remates.headers.referer:}")         private String hReferer;
    @Value("${cacg.remates.headers.user-agent:}")      private String hUA;
    @Value("${cacg.remates.headers.cookie:}")          private String hCookie;
    @Value("${cacg.remates.headers.accept-language:}") private String hLang;

    private final ExecutorService pool = Executors.newFixedThreadPool(Math.max(1, Runtime.getRuntime().availableProcessors() / 2));
    private RestTemplate rt;


    private volatile List<RemateDto> cache = List.of();
    private volatile Instant cacheAt = Instant.EPOCH;

    private RestTemplate rt() {
        if (rt != null) return rt;
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(20);
        cm.setDefaultMaxPerRoute(10);

        RequestConfig rc = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(10))
                .setResponseTimeout(Timeout.ofSeconds(15))
                .build();

        CloseableHttpClient http = HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(rc)
                .disableCookieManagement()
                .build();

        HttpComponentsClientHttpRequestFactory f = new HttpComponentsClientHttpRequestFactory(http);
        this.rt = new RestTemplate(f);
        return rt;
    }

    public List<RemateDto> fetchAll() {
        if (Duration.between(cacheAt, Instant.now()).toMinutes() < cacheMinutes && !cache.isEmpty()) {
            return cache;
        }


        PageResult p1 = fetchPage(1);
        if (!p1.ok) return List.of();

        int total = p1.totalItems > 0 ? p1.totalItems : p1.rows.size();
        int pages = (int) Math.ceil(total / (double) limit);
        if (pages <= 0) pages = maxPagesFallback;
        pages = Math.min(pages, maxPagesFallback);


        int to = Math.max(1, pages);
        int from = 2;
        List<CompletableFuture<PageResult>> futures = new ArrayList<>();
        Semaphore gate = new Semaphore(Math.max(1, concurrency));

        for (int page = from; page <= to; page++) {
            final int p = page;
            futures.add(CompletableFuture.supplyAsync(() -> {
                try { gate.acquire(); return fetchPage(p); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); return PageResult.fail(); }
                finally { gate.release(); }
            }, pool));
        }

        List<RemateDto> all = new ArrayList<>(p1.rows);
        for (CompletableFuture<PageResult> cf : futures) {
            try {
                PageResult pr = cf.get(20, TimeUnit.SECONDS);
                if (pr.ok && !pr.rows.isEmpty()) all.addAll(pr.rows);
            } catch (Exception e) {
                log.warn("Página paralela falló: {}", e.toString());
            }
        }

        all.sort(Comparator.comparing(RemateDto::fechaHora, Comparator.nullsLast(Comparator.naturalOrder())));
        cache = all;
        cacheAt = Instant.now();
        return all;
    }

    private static record PageResult(boolean ok, int totalItems, List<RemateDto> rows) {
        static PageResult ok(int total, List<RemateDto> r){ return new PageResult(true,total,r); }
        static PageResult fail(){ return new PageResult(false,0,List.of()); }
    }

    private PageResult fetchPage(int page) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.setAccept(List.of(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.ALL));
        if (!hOrigin.isBlank())  h.set("origin", hOrigin);
        if (!hReferer.isBlank()) h.set(HttpHeaders.REFERER, hReferer);
        if (!hUA.isBlank())      h.set(HttpHeaders.USER_AGENT, hUA);
        if (!hCookie.isBlank())  h.set(HttpHeaders.COOKIE, hCookie);
        if (!hLang.isBlank())    h.set(HttpHeaders.ACCEPT_LANGUAGE, hLang);
        h.set(HttpHeaders.ACCEPT_ENCODING, "gzip");

        String json = String.format(Locale.ROOT,
                "{\"limit\":%d,\"page\":%d," +
                        "\"searchWord\":\"\",\"filterMode\":\"\",\"filterDate\":\"\"," +
                        "\"filterCompany\":\"\",\"filterState\":\"\",\"filterType\":\"\",\"filterHeads\":0}",
                limit, page);

        try {
            ResponseEntity<String> resp = rt().exchange(url, HttpMethod.POST, new HttpEntity<>(json, h), String.class);
            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody()==null) return PageResult.fail();
            String body = resp.getBody();

            JsonNode root = om.readTree(body);
            int total = root.path("dataset").path("total").asInt(0);
            if (total == 0) total = root.path("dataset").path("count").asInt(0);

            JsonNode rows = root.path("dataset").path("rows");
            if (!rows.isArray()) return PageResult.ok(total, List.of());

            List<RemateDto> list = new ArrayList<>();
            for (JsonNode n : rows) {
                String socio = pick(n, "company_name", "socio", "company");
                String partner = pick(n, "auction_partner_name", "partner");
                if (partner != null && !partner.isBlank())
                    socio = (socio == null || socio.isBlank()) ? partner : socio + " (con " + partner + ")";

                String desc = firstNonBlank(
                        pick(n, "auction_notes","notes","descripcion","description","auction_title"),
                        buildDescripcion(n)
                );

                String loc  = pick(n, "city_name", "localidad");
                String prov = pick(n, "state_name", "provincia");
                String modo = pick(n, "auction_mode", "modo", "mode");
                Integer cabe= asInt(n, "auction_heads", "cabezas", "heads");

                String f1 = pick(n, "auction_date", "fecha", "date", "schedule");
                String h1 = pick(n, "auction_time", "hora", "time");
                LocalDateTime fh = parseIsoDateAndTime(f1, h1);

                list.add(new RemateDto(clean(socio), clean(desc), clean(loc), clean(prov), clean(modo), cabe, fh, url));
            }
            return PageResult.ok(total, list);

        } catch (Exception e) {
            log.warn("CACG página {} falló: {}", page, e.toString());
            return PageResult.fail();
        }
    }

    private static String buildDescripcion(JsonNode n) {
        String tipo = pick(n, "auction_type", "tipo");
        String breed = pick(n, "auction_breed", "raza");
        String dest = pick(n, "auction_destination", "destino");
        StringBuilder sb = new StringBuilder();
        if (tipo != null)  sb.append(tipo).append(" ");
        if (breed != null) sb.append(breed).append(" ");
        if (dest != null)  sb.append(dest);
        return clean(sb.toString());
    }
    private static String firstNonBlank(String... s){ if (s==null) return null; for (String x:s) if (x!=null && !x.isBlank()) return x; return null; }
    private static String clean(String s){ return s==null?null:s.replace('\u00a0',' ').replaceAll("\\s{2,}"," ").trim(); }
    private static String pick(JsonNode n,String...keys){ for(String k:keys){ JsonNode v=n.path(k); if(!v.isMissingNode() && !v.isNull() && !v.asText().isBlank()) return v.asText(); } return null; }
    private static Integer asInt(JsonNode n,String...keys){ for(String k:keys){ JsonNode v=n.path(k); if(v.isInt()) return v.asInt(); if(v.isTextual()){ String d=v.asText().replaceAll("[^0-9]",""); if(!d.isEmpty()) return Integer.parseInt(d);} } return null; }
    private static LocalDateTime parseIsoDateAndTime(String f,String h){
        try{
            LocalDate d = (f!=null && f.matches("\\d{4}-\\d{2}-\\d{2}")) ? LocalDate.parse(f) : null;
            if (d==null) return null;
            LocalTime t = (h!=null && h.matches("\\d{1,2}:\\d{2}")) ? LocalTime.parse(h.length()>5?h.substring(0,5):h) : LocalTime.of(0,0);
            return LocalDateTime.of(d,t);
        }catch(Exception e){ return null; }
    }
}
