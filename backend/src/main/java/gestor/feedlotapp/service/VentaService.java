package gestor.feedlotapp.service;

import gestor.feedlotapp.dto.venta.VentaListDto;
import gestor.feedlotapp.enums.animal.EstadoAnimal;
import gestor.feedlotapp.dto.venta.VentaDtos.CotizarRequest;
import gestor.feedlotapp.dto.venta.VentaDtos.CotizarResponse;
import gestor.feedlotapp.dto.venta.VentaDtos.CrearVentaRequest;
import gestor.feedlotapp.dto.venta.VentaDtos.ItemCotizado;
import gestor.feedlotapp.dto.venta.VentaDtos.VentaDetalleDto;
import gestor.feedlotapp.dto.venta.VentaDtos.VentaResponse;
import gestor.feedlotapp.entities.Animal;
import gestor.feedlotapp.entities.Cliente;
import gestor.feedlotapp.entities.Venta;
import gestor.feedlotapp.entities.VentaDetalle;
import gestor.feedlotapp.repository.AnimalRepository;
import gestor.feedlotapp.repository.ClienteRepository;
import gestor.feedlotapp.repository.VentaDetalleRepository;
import gestor.feedlotapp.repository.VentaRepository;
import gestor.feedlotapp.venta.service.CategoriaFromMagService;
import gestor.feedlotapp.venta.service.PrecioVentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final AnimalRepository animalRepo;
    private final VentaRepository ventaRepo;
    private final VentaDetalleRepository detalleRepo;
    private final PrecioVentaService precioVentaService;
    private final CategoriaFromMagService categoriaFromMagService;
    private final ClienteRepository clienteRepo;

    private record PrecioCat(BigDecimal precio, String fuente, LocalDate fecha) {}

    private List<Animal> resolveAnimales(List<Long> ids, List<String> caravanas) {
        Set<Animal> out = new LinkedHashSet<>();
        if (ids != null && !ids.isEmpty()) out.addAll(animalRepo.findByAnimalIdIn(ids));
        if (caravanas != null && !caravanas.isEmpty()) out.addAll(animalRepo.findByCaravanaIn(caravanas));
        if (out.isEmpty()) throw new IllegalArgumentException("Debe indicar animalIds o caravanas");
        return new ArrayList<>(out);
    }

    public CotizarResponse cotizar(CotizarRequest req) {
        LocalDate fecha = (req.fecha() == null) ? LocalDate.now() : req.fecha();
        List<Animal> animales = resolveAnimales(req.animalIds(), req.caravanas());

        List<ItemCotizado> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        Map<String, PrecioCat> cachePrecio = new HashMap<>();

        for (Animal a : animales) {
            BigDecimal peso = a.getPesoActual() != null ? a.getPesoActual() : a.getPesoInicial();
            if (peso == null) peso = BigDecimal.ZERO;
            peso = peso.setScale(3, RoundingMode.HALF_UP);


            String categoria = categoriaFromMagService.resolverCategoria(fecha, peso);


            PrecioCat pc = cachePrecio.computeIfAbsent(categoria, c -> {
                var p = precioVentaService.getPrecioKg(c, fecha);
                return new PrecioCat(p.precioKg(), p.fuente(), p.fecha());
            });

            BigDecimal importe = pc.precio()
                    .multiply(peso)
                    .setScale(2, RoundingMode.HALF_UP);

            items.add(new ItemCotizado(
                    a.getAnimalId(),
                    a.getCaravana(),
                    categoria,
                    peso,
                    pc.precio(),
                    importe,
                    pc.fuente(),
                    pc.fecha()
            ));
            total = total.add(importe);
        }
        return new CotizarResponse(items, total);
    }

    @Transactional
    public VentaResponse crearVenta(CrearVentaRequest req) {
        LocalDate fecha = (req.fecha() == null) ? LocalDate.now() : req.fecha();

        CotizarResponse cot = this.cotizar(new CotizarRequest(req.animalIds(), req.caravanas(), fecha));
        if (cot.items().isEmpty()) {
            throw new IllegalArgumentException("No hay animales para vender.");
        }

        List<Long> allIds = cot.items().stream().map(ItemCotizado::animalId).toList();
        Map<Long, Animal> animalsById = animalRepo.findByAnimalIdIn(allIds)
                .stream().collect(Collectors.toMap(Animal::getAnimalId, a -> a));

        boolean algunoNoActivo = animalsById.values().stream()
                .anyMatch(a -> a.getEstado() != EstadoAnimal.ACTIVO);
        if (algunoNoActivo) {
            throw new IllegalStateException("Uno o más animales no están activos (ya vendidos o de baja).");
        }

        Venta v = new Venta();
        v.setFecha(fecha);
        v.setTotal(cot.total());
        try {
            var m = Venta.class.getMethod("setDescripcion", String.class);
            m.invoke(v, req.descripcion());
        } catch (Exception ignored) {}

        if (req.clienteId() != null && clienteRepo != null) {
            Cliente c = clienteRepo.findById(Math.toIntExact(req.clienteId()))
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + req.clienteId()));
            v.setCliente(c);
        }
        v = ventaRepo.save(v);


        List<Long> idsAVender = cot.items().stream().map(ItemCotizado::animalId).toList();
        List<Long> yaVendidos = detalleRepo.findAnimalIdsVendidos(idsAVender);
        List<Long> dadosDeBaja = animalRepo.findIdsConBaja(idsAVender);

        if (!yaVendidos.isEmpty() || !dadosDeBaja.isEmpty()) {
            throw new IllegalStateException("Uno o mas animales ya han sido vendidos");
        }
        for (ItemCotizado it : cot.items()) {
            Animal an = animalsById.get(it.animalId());

            VentaDetalle d = new VentaDetalle();
            d.setVenta(v);
            d.setAnimal(an);
            d.setCategoria(it.categoria());
            d.setPesoKg(it.pesoKg());
            d.setPrecioKg(it.precioKg());
            d.setImporte(it.importe());
            d.setFuente(it.fuente());
            d.setFechaPrecio(it.fechaPrecio());
            d = detalleRepo.save(d);

            v.getDetalles().add(d);

            an.setEstado(EstadoAnimal.VENDIDO);
            an.setFechaBaja(fecha);
            an.setMotivoBaja("Vendido");
            an.setVentaDetalleBaja(d);
            animalRepo.save(an);
        }


        return mapVentaToResponse(v);
    }



    public VentaResponse getVenta(Long ventaId) {
        Venta v = ventaRepo.findWithDetallesByVentaId(ventaId)
                .orElseThrow(() -> new NoSuchElementException("Venta no encontrada: " + ventaId));
        return mapVentaToResponse(v);
    }

    public List<VentaListDto> listarResumen() {
        return ventaRepo.findAllByOrderByFechaDescVentaIdDesc()
                .stream()
                .map(v -> {
                    String cli = (v.getCliente() == null)
                            ? "Sin cliente"
                            : (v.getCliente().getNombre() + " " + v.getCliente().getApellido()).trim();

                    String caravanas = v.getDetalles() == null ? "" :
                            v.getDetalles().stream()
                                    .map(d -> d.getAnimal() != null ? d.getAnimal().getCaravana() : null)
                                    .filter(s -> s != null && !s.isBlank())
                                    .sorted()
                                    .collect(Collectors.joining(", "));

                    return new VentaListDto(
                            v.getVentaId(),
                            v.getFecha(),
                            v.getTotal(),
                            cli.isBlank() ? "Sin cliente" : cli,
                            caravanas.isBlank() ? "–" : caravanas
                    );
                })
                .toList();
    }


    public List<VentaListDto> historial(Long clienteId) {
        return ventaRepo.historialRaw(clienteId).stream().map(row -> {
            Long ventaId = row[0] != null ? ((Number) row[0]).longValue() : null;

            LocalDate fecha = null;
            Object f = row[1];
            if (f instanceof java.sql.Date sd) {
                fecha = sd.toLocalDate();
            } else if (f instanceof LocalDate ld) {
                fecha = ld;
            } else if (f instanceof java.util.Date ud) {
                fecha = ud.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }

            BigDecimal total = (BigDecimal) row[2];
            String cliente = (String) row[3];
            String caravanas = (String) row[4];
            return new VentaListDto(ventaId, fecha, total, cliente, caravanas);
        }).toList();
    }



    private VentaResponse mapVentaToResponse(Venta v) {
        List<VentaDetalleDto> dets = v.getDetalles().stream().map(d ->
                new VentaDetalleDto(
                        d.getVentaDetalleId(),
                        d.getAnimal().getAnimalId(),
                        d.getAnimal().getCaravana(),
                        d.getCategoria(),
                        d.getPesoKg(),
                        d.getPrecioKg(),
                        d.getImporte(),
                        d.getFuente(),
                        d.getFechaPrecio()
                )
        ).toList();

        Integer clienteId = (v.getCliente() != null) ? v.getCliente().getClienteId() : null;
        String clienteNombre = (v.getCliente() != null)
                ? (Optional.ofNullable(v.getCliente().getNombre()).orElse("") + " " +
                Optional.ofNullable(v.getCliente().getApellido()).orElse("")).trim()
                : null;

        String descripcion = null;
        try {
            var m = Venta.class.getMethod("getDescripcion");
            Object value = m.invoke(v);
            descripcion = (value != null) ? value.toString() : null;
        } catch (Exception ignored) {}

        return new VentaResponse(
                v.getVentaId(),
                v.getFecha(),
                v.getTotal(),
                clienteId,
                descripcion,
                dets,
                clienteNombre
        );
    }

}
