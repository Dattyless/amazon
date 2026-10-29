package dao;

import clases.queja;
import datos.datos;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class quejaDAO {

    // Variables para trabajar con la base de datos
    private Connection con;
    private PreparedStatement ps;
    private Statement st;
    private ResultSet rs;

    // Datos que se usan en los metodos
    private List<queja> lista;
    private String sql;

    private queja mapear(ResultSet rs) throws SQLException {
        // Convierto una fila de la tabla quejas en un objeto queja.
        return new queja(
                rs.getInt("id"),
                rs.getInt("id_cliente"),
                rs.getInt("id_pedido"),
                rs.getString("mensaje"),
                rs.getString("fecha")
        );
    }

    public void insertar(queja q) {
        // Guarda una queja nueva con la fecha actual.
        sql = "INSERT INTO quejas (id_cliente,id_pedido,mensaje,fecha) VALUES (?,?,?,NOW())";

        try {
            con = datos.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, q.getIdCliente());
            ps.setInt(2, q.getIdPedido());
            ps.setString(3, q.getMensaje());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error insertar queja: " + e.getMessage());
        } finally {
            cerrarTodo();
        }
    }

    public List<queja> obtenerPorCliente(int idCliente) {
        // Devuelve solo las quejas del cliente que ha iniciado sesion.
        lista = new ArrayList<>();
        sql = "SELECT * FROM quejas WHERE id_cliente=? ORDER BY fecha DESC";

        try {
            con = datos.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idCliente);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error quejas cliente: " + e.getMessage());
        } finally {
            cerrarTodo();
        }

        return lista;
    }

    public List<queja> obtenerTodas() {
        // El administrador ve todas las quejas ordenadas por fecha.
        lista = new ArrayList<>();
        sql = "SELECT * FROM quejas ORDER BY fecha DESC";

        try {
            con = datos.getConexion();
            st = con.createStatement();
            rs = st.executeQuery(sql);

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error obtenerTodas: " + e.getMessage());
        } finally {
            cerrarTodo();
        }

        return lista;
    }

    private void cerrarTodo() {
        // Cierro los recursos usados en las consultas.
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
