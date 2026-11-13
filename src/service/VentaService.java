package service;

import domain.*;
import exception.*;
import repository.*;

import java.math.BigDecimal;
import java.util.List;

public class VentaService {

    private final ProductoRepository productoRepository;
    private final VentaRepository ventaRepository;
    private final MovimientoInventarioRepository movimientoRepository;

    public VentaService(ProductoRepository productoRepository,
                        VentaRepository ventaRepository,
                        MovimientoInventarioRepository movimientoRepository) {
        this.productoRepository = productoRepository;
        this.ventaRepository = ventaRepository;
        this.movimientoRepository = movimientoRepository;
    }

    /** Ítem de entrada: productoId + cantidad. */
    public record ItemVenta(Long productoId, Integer cantidad) { }

    public Venta registrarVenta(List<ItemVenta> items, String usuario) {
        if (items == null || items.isEmpty()) {
            throw new VentaSinProductosException();
        }

        Venta venta = new Venta();
        venta.setEstado(EstadoVenta.REGISTRADA);
        venta.setUsuarioRegistro(usuario);

        BigDecimal total = BigDecimal.ZERO;

        for (ItemVenta item : items) {
            Producto producto = productoRepository.findById(item.productoId())
                    .orElseThrow(() -> new ProductoNoEncontradoException(item.productoId()));

            if (!producto.isActivo()) {
                throw new ProductoInactivoException(producto.getId());
            }
            if (producto.getStock() < item.cantidad()) {
                throw new StockInsuficienteException(producto.getId(), producto.getStock());
            }

            producto.setStock(producto.getStock() - item.cantidad());

            DetalleVenta detalle = new DetalleVenta();
            detalle.setProducto(producto);
            detalle.setCantidad(item.cantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            venta.addDetalle(detalle);

            total = total.add(producto.getPrecio()
                    .multiply(BigDecimal.valueOf(item.cantidad())));

            MovimientoInventario mov = new MovimientoInventario();
            mov.setProducto(producto);
            mov.setTipo(TipoMovimiento.SALIDA);
            mov.setCantidad(item.cantidad());
            mov.setMotivo("Venta");
            mov.setUsuario(usuario);
            movimientoRepository.save(mov);
        }

        venta.setTotal(total);
        return ventaRepository.save(venta);
    }

    public Venta cancelarVenta(Long ventaId, String usuario) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new VentaNoEncontradaException(ventaId));

        if (venta.getEstado() == EstadoVenta.CANCELADA) {
            throw new VentaYaCanceladaException(ventaId);
        }

        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());

            MovimientoInventario mov = new MovimientoInventario();
            mov.setProducto(producto);
            mov.setTipo(TipoMovimiento.ENTRADA);
            mov.setCantidad(detalle.getCantidad());
            mov.setMotivo("Cancelación de venta " + ventaId);
            mov.setUsuario(usuario);
            movimientoRepository.save(mov);
        }

        venta.setEstado(EstadoVenta.CANCELADA);
        return ventaRepository.save(venta);
    }
}