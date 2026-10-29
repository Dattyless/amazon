package clases;

public class administrador extends usuario {

    public administrador() {
        super();
        // Dejo el rol puesto aqui para que siempre se cree como administrador.
        setRol("admin");
    }

    public administrador(int id, String username, String password, String email) {
        super(id, username, password, email, "admin");
    }
}
