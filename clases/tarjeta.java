package clases;

public class tarjeta {

    private int id;
    private int idCliente;
    private String numero;
    private String titular;
    private String caducidad;
    private String cvv;

    public tarjeta() {
    }

    public tarjeta(int id, int idCliente, String numero, String titular,
            String caducidad, String cvv) {
        this.id = id;
        this.idCliente = idCliente;
        this.numero = numero;
        this.titular = titular;
        this.caducidad = caducidad;
        this.cvv = cvv;
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

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getCaducidad() {
        return caducidad;
    }

    public void setCaducidad(String caducidad) {
        this.caducidad = caducidad;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}
