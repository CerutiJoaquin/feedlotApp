package gestor.feedlotapp.service;


import gestor.feedlotapp.entities.Animal;
import gestor.feedlotapp.entities.Pesaje;
import gestor.feedlotapp.entities.RegistroComedero;
import gestor.feedlotapp.enums.animal.EstadoAnimal;
import gestor.feedlotapp.repository.AnimalRepository;
import gestor.feedlotapp.repository.PesajeRepository;
import gestor.feedlotapp.repository.RegistroComederoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import gestor.feedlotapp.dto.predicciones.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PrediccionService {

    private final PesajeRepository pesajeRepo;
    private final RegistroComederoRepository registroComederoRepo;
    private final AnimalRepository animalRepo;

    // PREDICCIÓN DE PESO POR ANIMAL
    public List<PesoPrediccionDTO> predecirPesoAnimal(String query) {

        Optional<Animal> optAnimal;

        if (query.matches("\\d+")) {
            Long id = Long.parseLong(query);
            optAnimal = animalRepo.findByAnimalIdAndEstado(id, EstadoAnimal.ACTIVO);
        } else {
            optAnimal = animalRepo.findByCaravanaAndEstado(query, EstadoAnimal.ACTIVO);
        }

        Animal animal = optAnimal.orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Animal no encontrado o no está ACTIVO"
                )
        );

        List<Pesaje> pesajes =
                pesajeRepo.findByAnimal_AnimalIdOrderByFechaAsc(animal.getAnimalId());

        return calcularPrediccionPeso(pesajes);
    }


    private List<PesoPrediccionDTO> calcularPrediccionPeso(List<Pesaje> pesajes) {
        if (pesajes.isEmpty()) {
            return Collections.emptyList();
        }

        List<PesoPrediccionDTO> resultado = new ArrayList<>();
        for (Pesaje p : pesajes) {
            BigDecimal peso = BigDecimal.valueOf(p.getPeso());
            resultado.add(new PesoPrediccionDTO(p.getFecha(), peso, false));
        }

        BigDecimal gmd;
        Pesaje last = pesajes.get(pesajes.size() - 1);

        if (pesajes.size() == 1) {
            gmd = BigDecimal.valueOf(0.8);
        } else {
            Pesaje first = pesajes.get(0);
            long dias = ChronoUnit.DAYS.between(first.getFecha(), last.getFecha());
            if (dias <= 0) dias = 1;

            BigDecimal lastPeso = BigDecimal.valueOf(last.getPeso());
            BigDecimal firstPeso = BigDecimal.valueOf(first.getPeso());
            BigDecimal diff = lastPeso.subtract(firstPeso);
            gmd = diff.divide(BigDecimal.valueOf(dias), 3, RoundingMode.HALF_UP);
        }

        for (int i = 1; i <= 3; i++) {
            LocalDate fechaPred = last.getFecha().plusMonths(i);
            long diasDesdeUltimo = ChronoUnit.DAYS.between(last.getFecha(), fechaPred);

            BigDecimal pesoExtra = gmd.multiply(BigDecimal.valueOf(diasDesdeUltimo));
            BigDecimal pesoBase = BigDecimal.valueOf(last.getPeso());
            BigDecimal pesoPred = pesoBase.add(pesoExtra)
                    .setScale(2, RoundingMode.HALF_UP);

            resultado.add(new PesoPrediccionDTO(fechaPred, pesoPred, true));
        }

        return resultado;
    }


    //  PREDICCIÓN DE CONSUMO POR CORRAL
    public List<ConsumoPrediccionDTO> predecirConsumoCorral(Integer corralId) {
        LocalDate hoy = LocalDate.now();
        int diasHaciaAtras = 14;
        LocalDate desde = hoy.minusDays(diasHaciaAtras);

        List<RegistroComedero> registros =
                registroComederoRepo.findByCorral_CorralIdAndFechaBetweenOrderByFechaAsc(
                        corralId, desde, hoy);

        if (registros.isEmpty()) {
            return Collections.emptyList();
        }

        long cabezas = animalRepo.countByCorral_CorralIdAndEstado(corralId, EstadoAnimal.ACTIVO);
        if (cabezas <= 0) cabezas = 1;


        Map<LocalDate, BigDecimal> consumoPorDia = new TreeMap<>();

        for (RegistroComedero reg : registros) {
            if (reg.getInsumo() == null ||
                    reg.getInsumo().getTipo() == null ||
                    ! "ALIMENTO".equalsIgnoreCase(reg.getInsumo().getTipo())) {
                continue;
            }

            BigDecimal cant = reg.getCantidad();
            if (cant == null) cant = BigDecimal.ZERO;

            BigDecimal totalDia = consumoPorDia.getOrDefault(reg.getFecha(), BigDecimal.ZERO);
            consumoPorDia.put(reg.getFecha(), totalDia.add(cant));
        }

        if (consumoPorDia.isEmpty()) {
            return Collections.emptyList();
        }

        List<ConsumoPrediccionDTO> resultado = new ArrayList<>();

        for (Map.Entry<LocalDate, BigDecimal> e : consumoPorDia.entrySet()) {
            BigDecimal totalKg = e.getValue().setScale(2, RoundingMode.HALF_UP);
            BigDecimal porCabeza = totalKg
                    .divide(BigDecimal.valueOf(cabezas), 3, RoundingMode.HALF_UP);

            resultado.add(new ConsumoPrediccionDTO(
                    e.getKey(),
                    totalKg,
                    porCabeza,
                    false
            ));
        }

        BigDecimal sumaTotales = resultado.stream()
                .map(ConsumoPrediccionDTO::getConsumoTotalKg)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal promTotal = sumaTotales
                .divide(BigDecimal.valueOf(resultado.size()), 3, RoundingMode.HALF_UP);

        BigDecimal promPorCabeza = promTotal
                .divide(BigDecimal.valueOf(cabezas), 3, RoundingMode.HALF_UP);

        for (int i = 1; i <= 7; i++) {
            LocalDate fechaPred = hoy.plusDays(i);

            resultado.add(new ConsumoPrediccionDTO(
                    fechaPred,
                    promTotal.setScale(2, RoundingMode.HALF_UP),
                    promPorCabeza.setScale(3, RoundingMode.HALF_UP),
                    true
            ));
        }

        return resultado;
    }

    // CONSUMO MENSUAL DE ALIMENTO EN GENERAL
    public List<ConsumoMensualDTO> obtenerConsumoMensualAlimento(int mesesAtras) {
        LocalDate hoy = LocalDate.now();
        LocalDate desde = hoy.minusMonths(mesesAtras - 1L).withDayOfMonth(1);
        LocalDate hasta = hoy.withDayOfMonth(hoy.lengthOfMonth());

        List<RegistroComedero> registros =
                registroComederoRepo.findByFechaBetweenOrderByFechaAsc(desde, hasta);

        if (registros.isEmpty()) {
            return Collections.emptyList();
        }


        Map<YearMonth, BigDecimal> consumoPorMes = new TreeMap<>();

        for (RegistroComedero reg : registros) {
            if (reg.getInsumo() == null ||
                    reg.getInsumo().getTipo() == null ||
                    ! "ALIMENTO".equalsIgnoreCase(reg.getInsumo().getTipo())) {
                continue;
            }

            BigDecimal cant = reg.getCantidad();
            if (cant == null) cant = BigDecimal.ZERO;

            YearMonth ym = YearMonth.from(reg.getFecha());
            BigDecimal acum = consumoPorMes.getOrDefault(ym, BigDecimal.ZERO);
            consumoPorMes.put(ym, acum.add(cant));
        }

        if (consumoPorMes.isEmpty()) {
            return Collections.emptyList();
        }

        List<ConsumoMensualDTO> resultado = new ArrayList<>();

        for (Map.Entry<YearMonth, BigDecimal> e : consumoPorMes.entrySet()) {
            YearMonth ym = e.getKey();
            LocalDate mesDate = ym.atDay(1);
            BigDecimal totalMes = e.getValue().setScale(2, RoundingMode.HALF_UP);

            resultado.add(new ConsumoMensualDTO(mesDate, totalMes));
        }

        return resultado;
    }


}
