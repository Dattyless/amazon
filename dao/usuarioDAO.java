package dao;

import clases.usuario;
import datos.datos;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class usuarioDAO {

    // Variables que uso para trabajar con la base de datos
    private Connection con;
    private PreparedStatement ps;
    private Statement st;
    private ResultSet rs;

    // Datos y listas
    private usuario usuarioEncontrado;
    private List<usuario> lista;
    private String sql;

    public usuario login(String username, String password) {
        // Busco un usuario que coincida con el nombre y la contrasena.
        usuarioEncontrado = null;
        sql = "SELECT * FROM usuarios WHERE username=? AND password=?";

        try {
            con = datos.getConexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();

            if (rs.next()) {
                usuarioEncontrado = new usuario(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("rol")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error login: " + e.getMessage());
        } finally {
            cerrarTodo();
        }

        return usuarioEncontrado;
    }

    public List<usuario> obtenerClientes() {
        // Cargo solo los usuarios que tienen rol de cliente.
        lista = new ArrayList<>();
        sql = "SELECT * FROM usuarios WHERE rol='cliente'";

        try {
            con = datos.getConexion();
            st = con.createStatement();
            rs = st.executeQuery(sql);

            while (rs.next()) {
                lista.add(new usuario(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("rol")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error obtenerClientes: " + e.getMessage());
        } finally {
            cerrarTodo();
        }

        return lista;
    }

    private void cerrarTodo() {
        // Cierro las conexiones para no dejarlas abiertas despues de consultar.
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (st != null) {
                st.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            System.err.println("Error cerrando conexion: " + e.getMessage());
        }
    }
}
