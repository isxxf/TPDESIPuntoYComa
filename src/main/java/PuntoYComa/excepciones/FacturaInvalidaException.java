package PuntoYComa.excepciones;

public class FacturaInvalidaException extends RuntimeException {
    public FacturaInvalidaException(String message) {
        super(message);
    }
}