package repository;

import domain.Producto;
import java.util.Optional;

public interface ProductoRepository {
    Optional<Producto> findById(Long id);
    Producto save(Producto producto);
}