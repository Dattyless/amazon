package clases;

public class cliente extends usuario {

    public cliente() {
        super();
        // Cuando creo un cliente, el rol ya queda asignado automaticamente.
        setRol("cliente");
    }

    public cliente(int id, String username, String password, String email) {
        super(id, username, password, email, "cliente");
    }
}
