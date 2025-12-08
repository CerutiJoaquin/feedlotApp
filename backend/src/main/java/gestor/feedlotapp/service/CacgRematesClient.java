package gestor.feedlotapp.service;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitUntilState;
import gestor.feedlotapp.dto.remate.RemateDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;

@Service
public class CacgRematesScraper {

    private static final String URL = "https://cacg.org.ar/remates";
    private static final String TABLE_SEL = "table.table.table-hover.table-sm.shadow";
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final Pattern PAG_RX = Pattern.compile("Página:\\s*(\\d+)\\s*de\\s*(\\d+)", Pattern.CASE_INSENSITIVE);

    private volatile List<RemateDto> cache = List.of();

    public List<RemateDto> fetchAllWithFallback() {
        try {
            List<RemateDto> r = fetchAll();
            if (!r.isEmpty()) cache = r;
            return r.isEmpty() ? cache : r;
        } catch (Exception e) {
            return cache;
        }
    }

    public List<RemateDto> fetchAll() {
        List<RemateDto> out = new ArrayList<>();
        try (Playwright pw = Playwright.create()) {
            Browser browser = pw.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            page.navigate(URL, new Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE));

            int total = detectTotalPages(page);
            if (total <= 0) total = 1;

            for (int p = 1; p <= total; p++) {
                if (p > 1) {
                    Locator next = page.locator("text=>").last();
                    if (next.isDisabled()) break;
                    next.click();
                    page.waitForLoadState(LoadState.NETWORKIDLE);
                }

                page.waitForSelector(TABLE_SEL);
                Locator rows = page.locator(TABLE_SEL + " tbody tr");
                int n = rows.count();
                for (int i = 0; i < n; i++) {
                    Locator tds = rows.nth(i).locator("> td");
                    if (tds.count() < 6) continue;

                    String fechaCol = tds.nth(0).innerText().trim();
                    String socio    = tds.nth(1).innerText().replace('\u00a0',' ').trim();
                    String desc     = tds.nth(2).innerText().replace('\u00a0',' ').replaceAll("\\s*\\n\\s*"," ").replaceAll("\\s{2,}"," ").trim();
                    String ubi      = tds.nth(3).innerText().trim();
                    String[] partsU = ubi.split("\\r?\\n");
                    String localidad = partsU.length>0 ? partsU[0].trim() : null;
                    String provincia = partsU.length>1 ? partsU[1].trim() : null;
                    String cabezasTx = tds.nth(4).innerText().trim().replaceAll("[^0-9]","");
                    Integer cabezas  = cabezasTx.isEmpty()? null : Integer.parseInt(cabezasTx);
                    String modo      = tds.nth(5).innerText().trim();

                    LocalDateTime fh = parseFechaHora(fechaCol);
                    out.add(new RemateDto(socio, desc, localidad, provincia, modo, cabezas, fh, URL));
                }
            }

            out.sort(Comparator.comparing(RemateDto::fechaHora, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
            browser.close();
        }
        return out;
    }

    private int detectTotalPages(Page page) {
        String txt = page.locator("body").innerText();
        Matcher m = PAG_RX.matcher(txt);
        if (m.find()) {
            try { return Integer.parseInt(m.group(2)); } catch (Exception ignored) {}
        }
        return 1;
    }

    private LocalDateTime parseFechaHora(String raw) {
        if (raw == null || raw.isBlank()) return null;

        raw = raw.replace(",", " ").replaceAll("\\s+"," ").trim();
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.of(0,0);

        Matcher m1 = Pattern.compile("(\\d{1,2})/(\\d{1,2})/(\\d{4})\\s+(\\d{1,2}:\\d{2}(?::\\d{2})?)").matcher(raw);
        if (m1.find()) {
            int d = Integer.parseInt(m1.group(1));
            int mo = Integer.parseInt(m1.group(2));
            int y = Integer.parseInt(m1.group(3));
            time = LocalTime.parse(m1.group(4).length()==5? m1.group(4): m1.group(4).substring(0,5), HORA);
            date = LocalDate.of(y, mo, d);
            return LocalDateTime.of(date, time);
        }

        Matcher m2 = Pattern.compile("(\\d{1,2})-([A-Za-zÁÉÍÓÚáéíóú]{3})\\s+(\\d{1,2}:\\d{2})").matcher(raw);
        if (m2.find()) {
            int d = Integer.parseInt(m2.group(1));
            String mmm = m2.group(2).toLowerCase(Locale.ROOT);
            int mo = monthFromAbbr(mmm);
            time = LocalTime.parse(m2.group(3), HORA);
            date = LocalDate.of(LocalDate.now().getYear(), mo, d);
            return LocalDateTime.of(date, time);
        }

        return LocalDateTime.of(date, time);
    }

    private int monthFromAbbr(String m) {
        String[] es = {"ene","feb","mar","abr","may","jun","jul","ago","sep","oct","nov","dic"};
        for (int i=0;i<es.length;i++) if (m.startsWith(es[i])) return i+1;
        return LocalDate.now().getMonthValue();
    }
}
