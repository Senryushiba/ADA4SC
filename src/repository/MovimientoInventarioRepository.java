package repository;

import domain.MovimientoInventario;

public interface MovimientoInventarioRepository {
    MovimientoInventario save(MovimientoInventario movimiento);
}