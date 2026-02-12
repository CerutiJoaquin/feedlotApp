package gestor.feedlotapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gestor.feedlotapp.dto.predicciones.*;
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
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PrediccionService {

    private final PesajeRepository pesajeRepo;
    private final RegistroComederoRepository registroComederoRepo;
    private final AnimalRepository animalRepo;

    private final ObjectMapper mapper = new ObjectMapper();

    public List<PesoPrediccionDTO> predecirPesoAnimal(Long animalId, int meses) {

        Animal animal = animalRepo
                .findByAnimalIdAndEstado(animalId, EstadoAnimal.ACTIVO)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Animal no encontrado o no está ACTIVO"
                        )
                );

        List<Pesaje> pesajes =
                pesajeRepo.findByAnimal_AnimalIdOrderByFechaAsc(animal.getAnimalId());

        if (pesajes.size() < 2) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Se requieren al menos 2 pesajes para predecir"
            );
        }

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "python3",
                    "backend/python/PredictionWeight.py",
                    animal.getAnimalId().toString(),
                    String.valueOf(meses)
            );

            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Error ejecutando script Python");
            }

            return mapper.readValue(
                    json.toString(),
                    new TypeReference<List<PesoPrediccionDTO>>() {}
            );

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error ejecutando predicción de peso",
                    e
            );
        }
    }

    public List<ConsumoPrediccionDTO> predecirConsumoCorral(
            Integer corralId, int dias) {

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "python3",
                    "backend/python/PredictionConsumption.py",
                    corralId.toString(),
                    String.valueOf(dias)
            );

            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Error ejecutando Python");
            }

            return mapper.readValue(
                    json.toString(),
                    new TypeReference<List<ConsumoPrediccionDTO>>() {}
            );

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error prediciendo consumo",
                    e
            );
        }
    }

    public List<ConsumoMensualDTO> obtenerConsumoMensualAlimento(int mesesAtras) {

        LocalDate hoy = LocalDate.now();
        YearMonth mesActual = YearMonth.from(hoy);
        YearMonth desdeMes = mesActual.minusMonths(mesesAtras - 1);

        LocalDate desde = desdeMes.atDay(1);
        LocalDate hasta = mesActual.atEndOfMonth();

        List<RegistroComedero> registros =
                registroComederoRepo.findByFechaBetweenOrderByFechaAsc(desde, hasta);

        Map<YearMonth, BigDecimal> consumoPorMes = new TreeMap<>();
        for (int i = 0; i < mesesAtras; i++) {
            consumoPorMes.put(desdeMes.plusMonths(i), BigDecimal.ZERO);
        }

        for (RegistroComedero reg : registros) {
            if (reg.getInsumo() == null ||
                    reg.getInsumo().getTipo() == null ||
                    !"ALIMENTO".equalsIgnoreCase(reg.getInsumo().getTipo())) {
                continue;
            }

            BigDecimal cant = Optional.ofNullable(reg.getCantidad())
                    .orElse(BigDecimal.ZERO);

            YearMonth ym = YearMonth.from(reg.getFecha());
            consumoPorMes.merge(ym, cant, BigDecimal::add);
        }

        List<ConsumoMensualDTO> resultado = new ArrayList<>();
        for (var e : consumoPorMes.entrySet()) {
            resultado.add(new ConsumoMensualDTO(
                    e.getKey().atDay(1),
                    e.getValue().setScale(2, RoundingMode.HALF_UP)
            ));
        }

        return resultado;
    }
}
