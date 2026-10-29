package personalizados;

public class PedidoNoEncontradoException extends Exception {

    public PedidoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}