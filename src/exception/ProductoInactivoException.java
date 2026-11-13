package exception;

public class ProductoInactivoException extends RuntimeException {
    public ProductoInactivoException(Long id) {
        super("Producto inactivo con id " + id);
    }
}