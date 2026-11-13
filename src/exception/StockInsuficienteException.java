package exception;

public class StockInsuficienteException extends RuntimeException {
    public StockInsuficienteException(Long id, Integer stock) {
        super("Stock insuficiente para producto " + id + ". Disponible: " + stock);
    }
}