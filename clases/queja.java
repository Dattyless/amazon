package clases;

public class queja {

    private int id;
    private int idCliente;
    private int idPedido;
    private String mensaje;
    private String fecha;

    public queja() {
    }

    public queja(int id, int idCliente, int idPedido, String mensaje, String fecha) {
        this.id = id;
        this.idCliente = idCliente;
        this.idPedido = idPedido;
        this.mensaje = mensaje;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
