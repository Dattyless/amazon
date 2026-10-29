package dao;

import clases.itemCarrito;
import clases.pedido;
import clases.producto;
import datos.datos;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class pedidoDAO {

    // Variables para la conexion y las consultas
    private Connection con;
    private PreparedStatement ps;
    private PreparedStatement psCheck;
    private PreparedStatement psPedido;
    private PreparedStatement psDetalle;
    private PreparedStatement psStock;
    private ResultSet rs;
    private ResultSet keys;

    // Listas y objetos
    private List<pedido> listaPedidos;
    private List<itemCarrito> listaDetalle;
    private producto productoActual;

    // Textos SQL
    private String sql;
    private String sqlDetalle;

    // Numeros
    private int stockActual;
    private int idPedido;
    private boolean pertenece;

    public int crearPedido(pedido p, List<itemCarrito> items) {
        // Creo el pedido, guardo el detalle y descuento el stock en una misma operacion.
        idPedido = -1;
        con = datos.getConexion();

        if (con == null) {
            return -1;
        }

        try {
            con.setAutoCommit(false);

            // Primero compruebo el stock real de cada producto antes de crear el pedido.
            psCheck = con.prepareStatement("SELECT stock FROM productos WHERE id=?");

            for (itemCarrito item : items) {
                psCheck.setInt(1, item.getProducto().getId());
                rs = psCheck.executeQuery();

                if (rs.next()) {
                    stockActual = rs.getInt("stock");

                    if (item.getCantidad() > stockActual) {
                        con.rollback();
                        return -1;
                    }
                }

                rs.close();
            }

            psPedido = con.prepareStatement(
                    "INSERT INTO pedidos (id_cliente, fecha, total) VALUES (?, NOW(), ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            psPedido.setInt(1, p.getIdCliente());
            psPedido.setDouble(2, p.getTotal());
            psPedido.executeUpdate();

            keys = psPedido.getGeneratedKeys();

            if (!keys.next()) {
                con.rollback();
                return -1;
            }

            idPedido = keys.getInt(1);

            psDetalle = con.prepareStatement(
                    "INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad, precio_unit) VALUES (?, ?, ?, ?)"
            );
            psStock = con.prepareStatement(
                    "UPDATE productos SET stock = stock - ? WHERE id = ?"
            );

            // Guardo cada producto del carrito en el detalle del pedido.
            for (itemCarrito item : items) {
                psDetalle.setInt(1, idPedido);
                psDetalle.setInt(2, item.getProducto().getId());
                psDetalle.setInt(3, item.getCantidad());
                psDetalle.setDouble(4, item.getProducto().getPrecio());
                psDetalle.executeUpdate();

                psStock.setInt(1, item.getCantidad());
                psStock.setInt(2, item.getProducto().getId());
                psStock.executeUpdate();
            }

            con.commit();
        } catch (SQLException e) {
            hacerRollback();
            System.err.println("Error crearPedido: " + e.getMessage());
            idPedido = -1;
        } finally {
            cerrarPedido();
        }

        return idPedido;
    }

    public List<pedido> obtenerHistorial(int idCliente) {
        // Devuelve los pedidos de un cliente ordenados desde el mas reciente.
        listaPedidos = new ArrayList<>();
        sql = "SELECT * FROM pedidos WHERE id_cliente=? ORDER BY fecha DESC";

        try {
            con = datos.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idCliente);
            rs = ps.executeQuery();

            while (rs.next()) {
                listaPedidos.add(new pedido(
                        rs.getInt("id"),
                        rs.getInt("id_cliente"),
                        rs.getString("fecha"),
                        rs.getDouble("total")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error historial: " + e.getMessage());
        } finally {
            cerrarTodo();
        }

        return listaPedidos;
    }

    public List<itemCarrito> obtenerDetallePedido(int idPedido) {
        // Cargo los productos que pertenecen a un pedido concreto.
        listaDetalle = new ArrayList<>();
        sqlDetalle = "SELECT dp.cantidad, dp.precio_unit, p.id, p.nombre, "
                + "p.categoria, p.imagen_ruta, p.stock "
                + "FROM detalle_pedido dp JOIN productos p ON dp.id_producto=p.id "
                + "WHERE dp.id_pedido=?";

        try {
            con = datos.getConexion();
            ps = con.prepareStatement(sqlDetalle);
            ps.setInt(1, idPedido);
            rs = ps.executeQuery();

            while (rs.next()) {
                productoActual = new producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        "",
                        rs.getDouble("precio_unit"),
                        rs.getInt("stock"),
                        rs.getString("categoria"),
                        rs.getString("imagen_ruta")
                );

                listaDetalle.add(new itemCarrito(productoActual, rs.getInt("cantidad")));
            }
        } catch (SQLException e) {
            System.err.println("Error detalle: " + e.getMessage());
        } finally {
            cerrarTodo();
        }

        return listaDetalle;
    }

    public boolean perteneceACliente(int idPedido, int idCliente) {
        // Compruebo que el pedido exista y sea del cliente que esta poniendo la queja.
        pertenece = false;
        sql = "SELECT id FROM pedidos WHERE id=? AND id_cliente=?";

        try {
            con = datos.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idPedido);
            ps.setInt(2, idCliente);
            rs = ps.executeQuery();

            pertenece = rs.next();
        } catch (SQLException e) {
            System.err.println("Error comprobando pedido: " + e.getMessage());
        } finally {
            cerrarTodo();
        }

        return pertenece;
    }

    private void hacerRollback() {
        // Si algo falla al crear el pedido, deshago todo para no dejar datos a medias.
        try {
            if (con != null) {
                con.rollback();
            }
        } catch (SQLException e) {
            System.err.println("Error rollback: " + e.getMessage());
        }
    }

    private void cerrarPedido() {
        // Cierro los recursos usados en la transaccion del pedido.
        try {
            if (rs != null) {
                rs.close();
            }
            if (keys != null) {
                keys.close();
            }
            if (psCheck != null) {
                psCheck.close();
            }
            if (psPedido != null) {
                psPedido.close();
            }
            if (psDetalle != null) {
                psDetalle.close();
            }
            if (psStock != null) {
                psStock.close();
            }
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        } catch (SQLException e) {
            System.err.println("Error cerrando pedido: " + e.getMessage());
        }
    }

    private void cerrarTodo() {
        // Cierro los recursos de las consultas normales.
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
