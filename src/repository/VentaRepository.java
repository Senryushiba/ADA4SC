package repository;

import domain.Venta;
import java.util.Optional;

public interface VentaRepository {
    Optional<Venta> findById(Long id);
    Venta save(Venta venta);
}