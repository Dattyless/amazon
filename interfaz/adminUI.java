package interfaz;

import clases.pedido;
import clases.producto;
import clases.queja;
import clases.usuario;
import dao.pedidoDAO;
import dao.productoDAO;
import dao.quejaDAO;
import dao.usuarioDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class adminUI extends JFrame {

    // Usuario que ha iniciado sesion como administrador
    private final usuario admin;

    // Paneles principales
    private JPanel top;
    private JPanel panel;
    private JPanel botones;
    private JPanel sur;
    private JPanel form;

    // Pestanas
    private JTabbedPane tabs;

    // Etiquetas
    private JLabel titulo;

    // Tablas y modelos
    private JTable tabla;
    private JTable tablaClientes;
    private JTable tablaHistorial;
    private JTable tablaQuejas;
    private DefaultTableModel modelo;
    private DefaultTableModel modeloClientes;
    private DefaultTableModel modeloHistorial;
    private DefaultTableModel modeloQuejas;
    private String[] columnas;

    // Botones
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDel;
    private JButton btnSalir;
    private JButton btnHistorial;
    private JButton btnDetalleQueja;

    // Campos para mostrar detalle de una queja
    private JTextArea txtMensajeQueja;

    // Campos del formulario de producto
    private JTextField txtNombre;
    private JTextField txtDescripcion;
    private JTextField txtPrecio;
    private JTextField txtStock;
    private JTextField txtImagen;
    private JComboBox<String> comboCategoria;

    // Listas
    private List<producto> productos;
    private List<usuario> clientes;
    private List<pedido> pedidos;
    private List<queja> quejas;

    // Objetos del proyecto
    private producto productoActual;

    // Arrays
    private String[] categorias;

    // Numeros y estados
    private int fila;
    private int id;
    private int idCliente;
    private int idPedido;
    private int confirm;
    private int res;
    private boolean esNuevo;

    // Constructor de la ventana del administrador
    public adminUI(usuario admin) {
        this.admin = admin;

        setTitle("Amazon - Panel Administrador");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // La pantalla se divide en la barra superior y las pestanas de gestion.
        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearPestanas(), BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel crearBarraSuperior() {
        top = new JPanel(new BorderLayout());
        top.setBackground(new Color(35, 47, 62));
        top.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Muestro el nombre del administrador para saber con que cuenta se ha entrado.
        titulo = new JLabel("AMAZON - Administrador: " + admin.getUsername());
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        top.add(titulo, BorderLayout.WEST);

        btnSalir = new JButton("Cerrar sesion");
        btnSalir.addActionListener(e -> {
            dispose();
            new loginUI();
        });

        top.add(btnSalir, BorderLayout.EAST);
        return top;
    }

    private JTabbedPane crearPestanas() {
        // Uso pestanas para separar productos, clientes y quejas.
        tabs = new JTabbedPane();
        tabs.addTab("Productos", panelProductos());
        tabs.addTab("Clientes", panelClientes());
        tabs.addTab("Quejas", panelQuejas());

        return tabs;
    }

    private JPanel panelProductos() {
        panel = new JPanel(new BorderLayout());

        columnas = new String[]{"ID", "Nombre", "Precio", "Stock", "Categoria"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setDefaultEditor(Object.class, null);
        tabla.getTableHeader().setReorderingAllowed(false);

        cargarProductos();
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Botones principales para modificar el catalogo.
        botones = new JPanel();
        btnAdd = new JButton("Anadir");
        btnEdit = new JButton("Editar");
        btnDel = new JButton("Eliminar");

        btnAdd.addActionListener(e -> dialogoProducto(null));
        btnEdit.addActionListener(e -> editarProducto());
        btnDel.addActionListener(e -> eliminarProducto());

        botones.add(btnAdd);
        botones.add(btnEdit);
        botones.add(btnDel);
        panel.add(botones, BorderLayout.SOUTH);

        return panel;
    }

    private void cargarProductos() {
        // Vacio la tabla para que no se dupliquen productos al recargar.
        modelo.setRowCount(0);
        productos = new productoDAO().listarTodos();

        for (producto p : productos) {
            modelo.addRow(new Object[]{
                p.getId(),
                p.getNombre(),
                p.getPrecio(),
                p.getStock(),
                p.getCategoria()
            });
        }
    }

    private void editarProducto() {
        // Para editar necesito que el administrador seleccione antes una fila.
        fila = tabla.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto");
            return;
        }

        id = (int) modelo.getValueAt(fila, 0);
        productoActual = new productoDAO().obtenerPorId(id);
        dialogoProducto(productoActual);
    }

    private void eliminarProducto() {
        // Elimino el producto seleccionado despues de pedir confirmacion.
        fila = tabla.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto");
            return;
        }

        id = (int) modelo.getValueAt(fila, 0);
        confirm = JOptionPane.showConfirmDialog(this, "Seguro que quieres eliminar este producto?");

        if (confirm == JOptionPane.YES_OPTION) {
            new productoDAO().eliminar(id);
            cargarProductos();
        }
    }

    private void dialogoProducto(producto p) {
        // Este formulario sirve tanto para anadir como para editar productos.
        esNuevo = (p == null);

        if (esNuevo) {
            p = new producto();
        }

        txtNombre = new JTextField(p.getNombre(), 15);
        txtDescripcion = new JTextField(p.getDescripcion(), 15);
        txtPrecio = new JTextField(String.valueOf(p.getPrecio()), 15);
        txtStock = new JTextField(String.valueOf(p.getStock()), 15);
        txtImagen = new JTextField(p.getImagenRuta(), 15);

        categorias = new String[]{"informatica", "muebles", "ocio", "libros", "cafeteras"};
        comboCategoria = new JComboBox<>(categorias);

        if (p.getCategoria() != null) {
            comboCategoria.setSelectedItem(p.getCategoria());
        }

        form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Nombre"));
        form.add(txtNombre);
        form.add(new JLabel("Descripcion"));
        form.add(txtDescripcion);
        form.add(new JLabel("Precio"));
        form.add(txtPrecio);
        form.add(new JLabel("Stock"));
        form.add(txtStock);
        form.add(new JLabel("Categoria"));
        form.add(comboCategoria);
        form.add(new JLabel("Imagen"));
        form.add(txtImagen);

        res = JOptionPane.showConfirmDialog(
                this,
                form,
                esNuevo ? "Anadir producto" : "Editar producto",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (res != JOptionPane.OK_OPTION) {
            return;
        }

        // Compruebo los campos obligatorios antes de guardar.
        if (txtNombre.getText().trim().isEmpty()
                || txtPrecio.getText().trim().isEmpty()
                || txtStock.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos obligatorios");
            return;
        }

        try {
            p.setNombre(txtNombre.getText().trim());
            p.setDescripcion(txtDescripcion.getText().trim());

            // Cambio la coma por punto por si alguien escribe precios como 19,99.
            p.setPrecio(Double.parseDouble(txtPrecio.getText().replace(',', '.')));
            p.setStock(Integer.parseInt(txtStock.getText().trim()));
            p.setCategoria((String) comboCategoria.getSelectedItem());
            p.setImagenRuta(txtImagen.getText().trim());

            if (esNuevo) {
                new productoDAO().insertar(p);
            } else {
                new productoDAO().actualizar(p);
            }

            cargarProductos();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Precio o stock invalidos");
        }
    }

    private JPanel panelClientes() {
        panel = new JPanel(new BorderLayout());

        columnas = new String[]{"ID", "Usuario", "Email"};
        modeloClientes = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tablaClientes = new JTable(modeloClientes);
        tablaClientes.setDefaultEditor(Object.class, null);
        tablaClientes.getTableHeader().setReorderingAllowed(false);

        // Cargo solo usuarios cliente, no administradores.
        clientes = new usuarioDAO().obtenerClientes();

        for (usuario u : clientes) {
            modeloClientes.addRow(new Object[]{
                u.getId(),
                u.getUsername(),
                u.getEmail()
            });
        }

        panel.add(new JScrollPane(tablaClientes), BorderLayout.CENTER);

        btnHistorial = new JButton("Ver historial");
        btnHistorial.addActionListener(e -> verHistorialCliente());

        sur = new JPanel();
        sur.add(btnHistorial);
        panel.add(sur, BorderLayout.SOUTH);

        return panel;
    }

    private void verHistorialCliente() {
        // Miro que cliente esta seleccionado para abrir sus pedidos.
        fila = tablaClientes.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente");
            return;
        }

        idCliente = (int) modeloClientes.getValueAt(fila, 0);
        mostrarHistorial(idCliente);
    }

    private void mostrarHistorial(int idCliente) {
        // Cargo todos los pedidos del cliente seleccionado.
        pedidos = new pedidoDAO().obtenerHistorial(idCliente);

        columnas = new String[]{"ID", "Fecha", "Total"};
        modeloHistorial = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        for (pedido p : pedidos) {
            modeloHistorial.addRow(new Object[]{
                p.getId(),
                p.getFecha(),
                String.format("%.2f EUR", p.getTotal())
            });
        }

        tablaHistorial = new JTable(modeloHistorial);
        tablaHistorial.setDefaultEditor(Object.class, null);
        tablaHistorial.getTableHeader().setReorderingAllowed(false);

        JOptionPane.showMessageDialog(
                this,
                new JScrollPane(tablaHistorial),
                "Historial",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    private JPanel panelQuejas() {
        panel = new JPanel(new BorderLayout());

        columnas = new String[]{"ID", "Cliente", "Pedido", "Mensaje", "Fecha"};
        modeloQuejas = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tablaQuejas = new JTable(modeloQuejas);
        tablaQuejas.setDefaultEditor(Object.class, null);
        tablaQuejas.getTableHeader().setReorderingAllowed(false);

        // Aqui se cargan todas las quejas que han enviado los clientes.
        quejas = new quejaDAO().obtenerTodas();

        for (queja q : quejas) {
            modeloQuejas.addRow(new Object[]{
                q.getId(),
                q.getIdCliente(),
                q.getIdPedido(),
                q.getMensaje(),
                q.getFecha()
            });
        }

        panel.add(new JScrollPane(tablaQuejas), BorderLayout.CENTER);

        btnDetalleQueja = new JButton("Ver detalle");
        btnDetalleQueja.addActionListener(e -> verDetalleQueja());

        sur = new JPanel();
        sur.add(btnDetalleQueja);
        panel.add(sur, BorderLayout.SOUTH);

        return panel;
    }

    private void verDetalleQueja() {
        // Abro la queja seleccionada para leer bien toda la informacion.
        fila = tablaQuejas.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una queja");
            return;
        }

        id = (int) modeloQuejas.getValueAt(fila, 0);
        idCliente = (int) modeloQuejas.getValueAt(fila, 1);
        idPedido = (int) modeloQuejas.getValueAt(fila, 2);

        txtMensajeQueja = new JTextArea(String.valueOf(modeloQuejas.getValueAt(fila, 3)), 6, 28);
        txtMensajeQueja.setLineWrap(true);
        txtMensajeQueja.setWrapStyleWord(true);
        txtMensajeQueja.setEditable(false);

        form = new JPanel(new BorderLayout(5, 5));
        panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("ID Queja"));
        panel.add(new JLabel(String.valueOf(id)));
        panel.add(new JLabel("ID Cliente"));
        panel.add(new JLabel(String.valueOf(idCliente)));
        panel.add(new JLabel("ID Pedido"));
        panel.add(new JLabel(String.valueOf(idPedido)));
        panel.add(new JLabel("Fecha"));
        panel.add(new JLabel(String.valueOf(modeloQuejas.getValueAt(fila, 4))));

        form.add(panel, BorderLayout.NORTH);
        form.add(new JScrollPane(txtMensajeQueja), BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, form, "Detalle de queja", JOptionPane.PLAIN_MESSAGE);
    }
}
