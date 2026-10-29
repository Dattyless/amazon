package dao;

import clases.producto;
import datos.datos;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class productoDAO {

    // Variables para la conexion y las consultas
    private Connection con;
    private PreparedStatement ps;
    private Statement st;
    private ResultSet rs;

    // Datos que se reutilizan en los metodos
    private List<producto> lista;
    private producto productoEncontrado;
    private String sql;
    private StringBuilder sqlFiltro;
    private int indice;

    private producto mapear(ResultSet rs) throws SQLException {
        // Paso una fila de la base de datos a un objeto producto.
        return new producto(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getDouble("precio"),
                rs.getInt("stock"),
                rs.getString("categoria"),
                rs.getString("imagen_ruta")
        );
    }

    public List<producto> listarTodos() {
        // Devuelve todos los productos del catalogo.
        lista = new ArrayList<>();
        sql = "SELECT * FROM productos";

        try {
            con = datos.getConexion();
            st = con.createStatement();
            rs = st.executeQuery(sql);

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error listarTodos: " + e.getMessage());
        } finally {
            cerrarTodo();
        }

        return lista;
    }

    public List<producto> filtrar(String nombre, String categoria, double precioMin, double precioMax) {
        // Creo la consulta segun los filtros que haya usado el cliente.
        lista = new ArrayList<>();
        sqlFiltro = new StringBuilder("SELECT * FROM productos WHERE precio >= ? AND precio <= ?");

        if (!nombre.isEmpty()) {
            sqlFiltro.append(" AND nombre LIKE ?");
        }

        if (!categoria.isEmpty()) {
            sqlFiltro.append(" AND categoria = ?");
        }

        try {
            con = datos.getConexion();
            ps = con.prepareStatement(sqlFiltro.toString());
            indice = 1;

            ps.setDouble(indice++, precioMin);
            ps.setDouble(indice++, precioMax);

            if (!nombre.isEmpty()) {
                ps.setString(indice++, "%" + nombre + "%");
            }

            if (!categoria.isEmpty()) {
                ps.setString(indice++, categoria);
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error filtrar: " + e.getMessage());
        } finally {
            cerrarTodo();
        }

        return lista;
    }

    public producto obtenerPorId(int id) {
        // Busco un producto concreto por su id.
        productoEncontrado = null;
        sql = "SELECT * FROM productos WHERE id=?";

        try {
            con = datos.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                productoEncontrado = mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error obtenerPorId: " + e.getMessage());
        } finally {
            cerrarTodo();
        }

        return productoEncontrado;
    }

    public void insertar(producto p) {
        // Inserta un producto nuevo en la base de datos.
        sql = "INSERT INTO productos (nombre,descripcion,precio,stock,categoria,imagen_ruta) VALUES (?,?,?,?,?,?)";

        try {
            con = datos.getConexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());
            ps.setDouble(3, p.getPrecio());
            ps.setInt(4, p.getStock());
            ps.setString(5, p.getCategoria());
            ps.setString(6, p.getImagenRuta());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error insertar: " + e.getMessage());
        } finally {
            cerrarTodo();
        }
    }

    public void actualizar(producto p) {
        // Actualiza los datos de un producto que ya existe.
        sql = "UPDATE productos SET nombre=?,descripcion=?,precio=?,stock=?,categoria=?,imagen_ruta=? WHERE id=?";

        try {
            con = datos.getConexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());
            ps.setDouble(3, p.getPrecio());
            ps.setInt(4, p.getStock());
            ps.setString(5, p.getCategoria());
            ps.setString(6, p.getImagenRuta());
            ps.setInt(7, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error actualizar: " + e.getMessage());
        } finally {
            cerrarTodo();
        }
    }

    public void eliminar(int id) {
        // Borra el producto seleccionado por su id.
        sql = "DELETE FROM productos WHERE id=?";

        try {
            con = datos.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error eliminar: " + e.getMessage());
        } finally {
            cerrarTodo();
        }
    }

    private void cerrarTodo() {
        // Cierro lo que se haya usado para evitar conexiones abiertas.
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
