package exception;

public class VentaSinProductosException extends RuntimeException {
    public VentaSinProductosException() {
        super("La venta debe tener al menos un producto");
    }
}