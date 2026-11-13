package test;

import domain.*;
import exception.*;
import repository.*;
import service.VentaService;
import testlib.MiniRunner;
import testlib.MiniTest;

import java.math.BigDecimal;
import java.util.*;

public class VentaServiceManualTest extends MiniTest {

    // ----- Repositorios falsos (reemplazan a Mockito) -----

    static class ProductoRepoFalso implements ProductoRepository {
        Map<Long, Producto> datos = new HashMap<>();
        public Optional<Producto> findById(Long id) { return Optional.ofNullable(datos.get(id)); }
        public Producto save(Producto p) { datos.put(p.getId(), p); return p; }
    }

    static class VentaRepoFalso implements VentaRepository {
        Map<Long, Venta> datos = new HashMap<>();
        int saves = 0;
        public Optional<Venta> findById(Long id) { return Optional.ofNullable(datos.get(id)); }
        public Venta save(Venta v) {
            if (v.getId() == null) v.setId((long) (datos.size() + 1));
            datos.put(v.getId(), v);
            saves++;
            return v;
        }
    }

    static class MovimientoRepoFalso implements MovimientoInventarioRepository {
        List<MovimientoInventario> guardados = new ArrayList<>();
        public MovimientoInventario save(MovimientoInventario m) { guardados.add(m); return m; }
    }

    // ----- Utilidades -----

    private Producto crearLibro(int stock) {
        Producto p = new Producto();
        p.setId(1L);
        p.setNombre("Java Efectivo");
        p.setPrecio(new BigDecimal("50.00"));
        p.setStock(stock);
        p.setActivo(true);
        return p;
    }

    // ----- Casos de prueba -----

    // PU-VEN-01
    public void testRegistrarVenta_exitoso() {
        Producto libro = crearLibro(10);
        ProductoRepoFalso pr = new ProductoRepoFalso();
        pr.datos.put(1L, libro);
        VentaRepoFalso vr = new VentaRepoFalso();
        MovimientoRepoFalso mr = new MovimientoRepoFalso();

        VentaService svc = new VentaService(pr, vr, mr);
        Venta v = svc.registrarVenta(
                List.of(new VentaService.ItemVenta(1L, 2)), "cajero1");

        assertBigDecimal("100.00", v.getTotal(), "total");
        assertEquals(EstadoVenta.REGISTRADA, v.getEstado(), "estado");
        assertEqualsInt(8, libro.getStock(), "stock descontado");
        assertEqualsInt(1, mr.guardados.size(), "un movimiento");
        assertEquals(TipoMovimiento.SALIDA, mr.guardados.get(0).getTipo(), "tipo SALIDA");
        assertEqualsInt(2, mr.guardados.get(0).getCantidad(), "cantidad 2");
    }

    // PU-VEN-02
    public void testRegistrarVenta_multiplesProductos() {
        Producto libro = crearLibro(10);
        Producto cuaderno = new Producto();
        cuaderno.setId(2L);
        cuaderno.setPrecio(new BigDecimal("15.50"));
        cuaderno.setStock(5);
        cuaderno.setActivo(true);

        ProductoRepoFalso pr = new ProductoRepoFalso();
        pr.datos.put(1L, libro);
        pr.datos.put(2L, cuaderno);
        VentaRepoFalso vr = new VentaRepoFalso();
        MovimientoRepoFalso mr = new MovimientoRepoFalso();

        VentaService svc = new VentaService(pr, vr, mr);
        Venta v = svc.registrarVenta(List.of(
                new VentaService.ItemVenta(1L, 2),
                new VentaService.ItemVenta(2L, 1)), "cajero1");

        assertBigDecimal("115.50", v.getTotal(), "total");
        assertEqualsInt(2, v.getDetalles().size(), "dos detalles");
        assertEqualsInt(2, mr.guardados.size(), "dos movimientos");
        assertEqualsInt(8, libro.getStock(), "stock libro");
        assertEqualsInt(4, cuaderno.getStock(), "stock cuaderno");
    }

    // PU-VEN-03
    public void testRegistrarVenta_stockInsuficiente() {
        Producto libro = crearLibro(1);
        ProductoRepoFalso pr = new ProductoRepoFalso();
        pr.datos.put(1L, libro);
        VentaRepoFalso vr = new VentaRepoFalso();
        MovimientoRepoFalso mr = new MovimientoRepoFalso();

        VentaService svc = new VentaService(pr, vr, mr);

        assertThrows(StockInsuficienteException.class,
                () -> svc.registrarVenta(
                        List.of(new VentaService.ItemVenta(1L, 2)), "cajero1"),
                "stock insuficiente");

        assertEqualsInt(1, libro.getStock(), "stock intacto");
        assertEqualsInt(0, vr.saves, "no se guardó venta");
        assertEqualsInt(0, mr.guardados.size(), "no se guardó movimiento");
    }

    // PU-VEN-04
    public void testRegistrarVenta_productoInactivo() {
        Producto libro = crearLibro(10);
        libro.setActivo(false);
        ProductoRepoFalso pr = new ProductoRepoFalso();
        pr.datos.put(1L, libro);
        VentaRepoFalso vr = new VentaRepoFalso();
        MovimientoRepoFalso mr = new MovimientoRepoFalso();

        VentaService svc = new VentaService(pr, vr, mr);

        assertThrows(ProductoInactivoException.class,
                () -> svc.registrarVenta(
                        List.of(new VentaService.ItemVenta(1L, 1)), "cajero1"),
                "producto inactivo");
        assertEqualsInt(0, vr.saves, "no se guardó venta");
    }

    // PU-VEN-05
    public void testRegistrarVenta_productoNoExiste() {
        ProductoRepoFalso pr = new ProductoRepoFalso();
        VentaRepoFalso vr = new VentaRepoFalso();
        MovimientoRepoFalso mr = new MovimientoRepoFalso();

        VentaService svc = new VentaService(pr, vr, mr);

        assertThrows(ProductoNoEncontradoException.class,
                () -> svc.registrarVenta(
                        List.of(new VentaService.ItemVenta(99L, 1)), "cajero1"),
                "producto inexistente");
        assertEqualsInt(0, vr.saves, "no se guardó venta");
    }

    // PU-VEN-06
    public void testRegistrarVenta_sinItems() {
        ProductoRepoFalso pr = new ProductoRepoFalso();
        VentaRepoFalso vr = new VentaRepoFalso();
        MovimientoRepoFalso mr = new MovimientoRepoFalso();

        VentaService svc = new VentaService(pr, vr, mr);

        assertThrows(VentaSinProductosException.class,
                () -> svc.registrarVenta(List.of(), "cajero1"),
                "venta vacía");
    }

    // PU-VEN-07
    public void testCancelarVenta_exitosa() {
        Producto libro = crearLibro(8);
        DetalleVenta d = new DetalleVenta();
        d.setProducto(libro);
        d.setCantidad(2);
        d.setPrecioUnitario(new BigDecimal("50.00"));

        Venta venta = new Venta();
        venta.setId(10L);
        venta.setEstado(EstadoVenta.REGISTRADA);
        venta.setDetalles(List.of(d));

        ProductoRepoFalso pr = new ProductoRepoFalso();
        VentaRepoFalso vr = new VentaRepoFalso();
        vr.datos.put(10L, venta);
        MovimientoRepoFalso mr = new MovimientoRepoFalso();

        VentaService svc = new VentaService(pr, vr, mr);
        Venta canc = svc.cancelarVenta(10L, "admin");

        assertEquals(EstadoVenta.CANCELADA, canc.getEstado(), "estado cancelada");
        assertEqualsInt(10, libro.getStock(), "stock revertido");
        assertEqualsInt(1, mr.guardados.size(), "un movimiento");
        assertEquals(TipoMovimiento.ENTRADA, mr.guardados.get(0).getTipo(), "tipo ENTRADA");
        assertEqualsInt(2, mr.guardados.get(0).getCantidad(), "cantidad 2");
    }

    // PU-VEN-08
    public void testCancelarVenta_yaCancelada() {
        Venta venta = new Venta();
        venta.setId(10L);
        venta.setEstado(EstadoVenta.CANCELADA);

        ProductoRepoFalso pr = new ProductoRepoFalso();
        VentaRepoFalso vr = new VentaRepoFalso();
        vr.datos.put(10L, venta);
        MovimientoRepoFalso mr = new MovimientoRepoFalso();

        VentaService svc = new VentaService(pr, vr, mr);

        assertThrows(VentaYaCanceladaException.class,
                () -> svc.cancelarVenta(10L, "admin"),
                "ya cancelada");
        assertEqualsInt(0, mr.guardados.size(), "no hay movimiento");
    }

    // PU-VEN-09
    public void testCancelarVenta_noExiste() {
        ProductoRepoFalso pr = new ProductoRepoFalso();
        VentaRepoFalso vr = new VentaRepoFalso();
        MovimientoRepoFalso mr = new MovimientoRepoFalso();

        VentaService svc = new VentaService(pr, vr, mr);

        assertThrows(VentaNoEncontradaException.class,
                () -> svc.cancelarVenta(99L, "admin"),
                "venta inexistente");
    }

    // ----- Punto de entrada -----

    public static void main(String[] args) {
        MiniRunner.correr(VentaServiceManualTest.class);
    }
}