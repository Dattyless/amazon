package dao;

import clases.tarjeta;
import datos.datos;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class tarjetaDAO {

    // Variables para conexion y consultas
    private Connection con;
    private PreparedStatement ps;
    private ResultSet rs;

    // Datos reutilizados
    private List<tarjeta> lista;
    private tarjeta tarjetaActual;
    private String sql;
    private boolean correcto;

    public List<tarjeta> obtenerTarjetasDeCliente(int idCliente) {
        // Cargo las tarjetas guardadas de un cliente.
        lista = new ArrayList<>();
        sql = "SELECT * FROM tarjetas WHERE id_cliente=?";

        try {
            con = datos.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idCliente);
            rs = ps.executeQuery();

            while (rs.next()) {
                tarjetaActual = new tarjeta();
                tarjetaActual.setId(rs.getInt("id"));
                tarjetaActual.setIdCliente(rs.getInt("id_cliente"));
                tarjetaActual.setNumero(rs.getString("numero"));
                tarjetaActual.setTitular(rs.getString("titular"));
                tarjetaActual.setCaducidad(rs.getString("caducidad"));
                tarjetaActual.setCvv(rs.getString("cvv"));

                lista.add(tarjetaActual);
            }
        } catch (SQLException e) {
            System.err.println("Error tarjetas cliente: " + e.getMessage());
        } finally {
            cerrarTodo();
        }

        return lista;
    }

    public boolean validarTarjeta(int idCliente, String numero, String titular, String caducidad, String cvv) {
        // Compruebo si la tarjeta ya existe exactamente con esos datos.
        correcto = false;
        sql = "SELECT * FROM tarjetas "
                + "WHERE id_cliente=? "
                + "AND numero=? "
                + "AND titular=? "
                + "AND caducidad=? "
                + "AND cvv=?";

        try {
            con = datos.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idCliente);
            ps.setString(2, numero);
            ps.setString(3, titular);
            ps.setString(4, caducidad);
            ps.setString(5, cvv);
            rs = ps.executeQuery();

            correcto = rs.next();
        } catch (SQLException e) {
            System.err.println("Error validar tarjeta: " + e.getMessage());
        } finally {
            cerrarTodo();
        }

        return correcto;
    }

    public boolean insertarTarjeta(tarjeta t) {
        // Guarda una tarjeta nueva para poder usarla despues.
        correcto = false;
        sql = "INSERT INTO tarjetas (id_cliente, numero, titular, caducidad, cvv) VALUES (?, ?, ?, ?, ?)";

        try {
            con = datos.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, t.getIdCliente());
            ps.setString(2, t.getNumero());
            ps.setString(3, t.getTitular());
            ps.setString(4, t.getCaducidad());
            ps.setString(5, t.getCvv());

            correcto = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error insertar tarjeta: " + e.getMessage());
        } finally {
            cerrarTodo();
        }

        return correcto;
    }

    private void cerrarTodo() {
        // Cierro lo que se haya abierto en el metodo.
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            System.err.println("Error cerrando conexion: " + e.getMessage());
        }
    }
}
