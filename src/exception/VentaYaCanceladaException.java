package exception;

public class VentaYaCanceladaException extends RuntimeException {
    public VentaYaCanceladaException(Long id) {
        super("La venta " + id + " ya está cancelada");
    }
}