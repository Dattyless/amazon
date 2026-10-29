package interfaz;

import clases.*;
import dao.*;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import personalizados.PedidoNoEncontradoException;
import personalizados.StockInsuficienteException;
import personalizados.TarjetaInvalidaException;

public class clienteUI extends JFrame {

    // Objetos principales
    private final usuario cliente;
    private final carrito carrito;
    private final productoDAO prodDAO;

    // Paneles
    private JPanel panelCatalogo;
    private JPanel top;
    private JPanel busqueda;
    private JPanel topRight;
    private JPanel filtros;
    private JPanel card;
    private JPanel info;
    private JPanel form;
    private JPanel cantidadPanel;
    private JPanel botones;
    private JPanel panel;
    private JPanel arriba;
    private JScrollPane scrollCatalogo;
    private Component componenteCentro;

    // Etiquetas
    private JLabel logo;
    private JLabel imagen;
    private JLabel nombreLabel;
    private JLabel precioLabel;
    private JLabel stockLabel;
    private JLabel agotado;
    private JLabel lblTotal;

    // Campos de texto
    private JTextField txtBuscar;
    private JTextField txtMin;
    private JTextField txtMax;
    private JTextField txtCantidad;
    private JTextField numTarjeta;
    private JTextField titular;
    private JTextField caducidad;
    private JTextField idPedidoTxt;

    // Areas de texto y contrasenas
    private JTextArea descripcion;
    private JTextArea mensaje;
    private JPasswordField cvv;

    // Combos
    private JComboBox<String> comboCat;
    private JComboBox<String> comboTarjetas;

    // Botones
    private JButton btnBuscar;
    private JButton btnCarrito;
    private JButton btnHistorial;
    private JButton btnQuejas;
    private JButton btnSalir;
    private JButton btnFiltrar;
    private JButton btnLimpiar;
    private JButton btnVer;
    private JButton btnMenos;
    private JButton btnMas;
    private JButton btnEliminar;
    private JButton btnCambiar;
    private JButton btnPagar;
    private JButton btnCerrar;
    private JButton btnDetalle;
    private JButton btnNueva;
    private JButton btnVolver;
    private JButton btnAnadir;

    // Tablas y modelos
    private JTable tabla;
    private DefaultTableModel modelo;
    private String[] columnas;

    // Ventanas
    private JDialog dialog;

    // Listas
    private List<producto> listaProductos;
    private List<tarjeta> tarjetas;
    private List<pedido> pedidos;
    private List<itemCarrito> detalle;
    private List<queja> quejas;

    // Objetos del proyecto
    private itemCarrito itemActual;
    private tarjeta tarjetaActual;
    private tarjeta nuevaTarjeta;
    private pedido pedidoCliente;
    private queja nuevaQuejaObj;

    // Imagenes
    private ImageIcon icono;
    private Image img;

    // Textos
    private String nombre;
    private String categoria;
    private String texto;
    private String numero;
    private String nombreTitular;
    private String fecha;
    private String codigo;
    private String actual;
    private String nuevo;

    // Numeros
    private int res;
    private int cantidad;
    private int yaEnCarrito;
    private int valorActual;
    private int fila;
    private int nuevaCantidad;
    private int index;
    private int guardar;
    private int idPedido;
    private int idPedidoQueja;
    private double min;
    private double max;
    private double subtotal;
    private double total;

    // Booleans
    private boolean valida;
    private boolean insertada;
    private boolean pedidoValido;

    public clienteUI(usuario cliente) {
        this.cliente = cliente;
        this.carrito = new carrito();
        this.prodDAO = new productoDAO();

        setTitle("Amazon - " + cliente.getUsername());
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Separo la interfaz en tres partes para que sea mas facil de organizar.
        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearPanelFiltros(), BorderLayout.WEST);
        add(crearCatalogo(), BorderLayout.CENTER);

        // Al abrir la ventana cargo los productos directamente.
        filtrar();
        setVisible(true);
    }

    private JPanel crearBarraSuperior() {
        top = new JPanel(new BorderLayout());
        top.setBackground(new Color(35, 47, 62));
        top.setBorder(new EmptyBorder(8, 15, 8, 15));

        // Logo de la parte superior, simplemente para que parezca una tienda.
        logo = new JLabel("AMAZON");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Arial", Font.BOLD, 20));
        top.add(logo, BorderLayout.WEST);

        // Buscador para filtrar productos por nombre.
        txtBuscar = new JTextField(20);
        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> filtrar());

        busqueda = new JPanel();
        busqueda.setBackground(new Color(35, 47, 62));
        busqueda.add(txtBuscar);
        busqueda.add(btnBuscar);
        top.add(busqueda, BorderLayout.CENTER);

        topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        topRight.setBackground(new Color(35, 47, 62));

        btnCarrito = new JButton("Carrito");
        btnHistorial = new JButton("Pedidos");
        btnQuejas = new JButton("Quejas");
        btnSalir = new JButton("Salir");

        // Cada boton abre una parte distinta de la aplicacion.
        btnCarrito.addActionListener(e -> verCarrito());
        btnHistorial.addActionListener(e -> verHistorial());
        btnQuejas.addActionListener(e -> verQuejas());
        btnSalir.addActionListener(e -> {
            dispose();
            new loginUI();
        });

        topRight.add(btnCarrito);
        topRight.add(btnHistorial);
        topRight.add(btnQuejas);
        topRight.add(btnSalir);
        top.add(topRight, BorderLayout.EAST);

        return top;
    }

    private JPanel crearPanelFiltros() {
        filtros = new JPanel(new GridLayout(0, 1, 5, 5));
        filtros.setBorder(new EmptyBorder(15, 10, 15, 10));
        filtros.setPreferredSize(new Dimension(170, 0));

        // Filtros que usa el cliente para buscar mejor los productos.
        filtros.add(new JLabel("Categoria"));
        comboCat = new JComboBox<>(new String[]{
            "Todas",
            "informatica",
            "muebles",
            "ocio",
            "libros",
            "cafeteras"
        });
        filtros.add(comboCat);

        filtros.add(new JLabel("Precio min"));
        txtMin = new JTextField("0");
        filtros.add(txtMin);

        filtros.add(new JLabel("Precio max"));
        txtMax = new JTextField("9999");
        filtros.add(txtMax);

        btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> filtrar());
        filtros.add(btnFiltrar);

        // Devuelve los filtros a los valores iniciales.
        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> limpiarFiltros());
        filtros.add(btnLimpiar);

        return filtros;
    }

    private JScrollPane crearCatalogo() {
        // Uso un panel con grid para que las tarjetas salgan ordenadas en columnas.
        panelCatalogo = new JPanel(new GridLayout(0, 3, 10, 10));
        panelCatalogo.setBorder(new EmptyBorder(10, 10, 10, 10));

        scrollCatalogo = new JScrollPane(panelCatalogo);
        return scrollCatalogo;
    }

    private void mostrarCatalogo() {
        // Vuelvo a poner el catalogo en el centro de la ventana.
        componenteCentro = ((BorderLayout) getContentPane().getLayout()).getLayoutComponent(BorderLayout.CENTER);

        if (componenteCentro != null) {
            getContentPane().remove(componenteCentro);
        }

        add(scrollCatalogo, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void mostrarEnCentro(Component componente) {
        // Cambio solo la parte central sin abrir otra ventana.
        componenteCentro = ((BorderLayout) getContentPane().getLayout()).getLayoutComponent(BorderLayout.CENTER);

        if (componenteCentro != null) {
            getContentPane().remove(componenteCentro);
        }

        add(componente, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void limpiarFiltros() {
        txtBuscar.setText("");
        comboCat.setSelectedIndex(0);
        txtMin.setText("0");
        txtMax.setText("9999");
        filtrar();
    }

    private void filtrar() {
        // Cojo los datos escritos en los filtros y se los paso al DAO.
        nombre = txtBuscar.getText().trim();
        categoria = comboCat.getSelectedItem().toString();

        if ("Todas".equals(categoria)) {
            categoria = "";
        }

        min = leerDoubleSeguro(txtMin.getText(), 0);
        max = leerDoubleSeguro(txtMax.getText(), 9999);
        listaProductos = prodDAO.filtrar(nombre, categoria, min, max);

        // Borro el catalogo anterior para pintar solo los productos filtrados.
        panelCatalogo.removeAll();

        for (producto p : listaProductos) {
            panelCatalogo.add(tarjetaProducto(p));
        }

        panelCatalogo.revalidate();
        panelCatalogo.repaint();
        mostrarCatalogo();
    }

    private double leerDoubleSeguro(String valor, double defecto) {
        try {
            // Permito coma o punto por si el usuario escribe el precio de las dos formas.
            return Double.parseDouble(valor.trim().replace(',', '.'));
        } catch (Exception e) {
            return defecto;
        }
    }

    private JPanel tarjetaProducto(producto p) {
        // Cada producto se muestra como una tarjeta con imagen, precio, stock y boton.
        card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.setBackground(Color.WHITE);

        imagen = new JLabel();
        imagen.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            // Intento cargar la imagen del producto y si falla muestro un texto.
            icono = new ImageIcon(p.getImagenRuta());
            img = icono.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            imagen.setIcon(new ImageIcon(img));
        } catch (Exception ex) {
            imagen.setText("Sin imagen");
        }

        nombreLabel = new JLabel(p.getNombre(), SwingConstants.CENTER);
        nombreLabel.setFont(new Font("Arial", Font.BOLD, 12));

        precioLabel = new JLabel(String.format("%.2f EUR", p.getPrecio()), SwingConstants.CENTER);
        precioLabel.setForeground(new Color(180, 0, 0));

        stockLabel = new JLabel("Stock: " + p.getStock(), SwingConstants.CENTER);

        btnVer = new JButton("Ver producto");
        btnVer.addActionListener(e -> verProducto(p));

        info = new JPanel(new GridLayout(3, 1));
        info.setBackground(Color.WHITE);
        info.add(nombreLabel);
        info.add(precioLabel);
        info.add(stockLabel);

        card.add(imagen, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);
        card.add(btnVer, BorderLayout.SOUTH);

        return card;
    }

    private void verProducto(producto p) {
        if (p != null) {
            mostrarProductoEnPanel(p);
            return;
        }
        // Aqui enseño la informacion completa del producto antes de meterlo al carrito.
        form = new JPanel(new GridLayout(0, 1, 5, 5));

        imagen = new JLabel();
        imagen.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            icono = new ImageIcon(p.getImagenRuta());
            img = icono.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
            imagen.setIcon(new ImageIcon(img));
        } catch (Exception ex) {
            imagen.setText("Sin imagen");
        }

        nombreLabel = new JLabel("<html><b>" + p.getNombre() + "</b></html>");
        precioLabel = new JLabel("Precio: " + String.format("%.2f EUR", p.getPrecio()));
        stockLabel = new JLabel("Stock disponible: " + p.getStock());

        descripcion = new JTextArea(p.getDescripcion());
        descripcion.setLineWrap(true);
        descripcion.setWrapStyleWord(true);
        descripcion.setEditable(false);
        descripcion.setOpaque(false);

        form.add(imagen);
        form.add(nombreLabel);
        form.add(precioLabel);
        form.add(stockLabel);
        form.add(new JScrollPane(descripcion));

        if (p.getStock() <= 0) {
            // Si no hay stock no dejo elegir cantidad.
            agotado = new JLabel("PRODUCTO AGOTADO");
            agotado.setForeground(Color.RED);
            agotado.setFont(new Font("Arial", Font.BOLD, 14));
            form.add(agotado);

            JOptionPane.showMessageDialog(this, form, "Producto", JOptionPane.PLAIN_MESSAGE);
            return;
        }

        cantidadPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnMenos = new JButton("-");
        btnMas = new JButton("+");
        txtCantidad = new JTextField("1", 4);

        // Limito la cantidad para que solo se puedan escribir numeros.
        limitarNumeros(txtCantidad, 3);

        btnMenos.addActionListener(e -> ajustarCantidad(txtCantidad, -1, p.getStock()));
        btnMas.addActionListener(e -> ajustarCantidad(txtCantidad, 1, p.getStock()));

        cantidadPanel.add(new JLabel("Cantidad"));
        cantidadPanel.add(btnMenos);
        cantidadPanel.add(txtCantidad);
        cantidadPanel.add(btnMas);
        form.add(cantidadPanel);

        res = JOptionPane.showConfirmDialog(this, form, "Producto", JOptionPane.OK_CANCEL_OPTION);

        if (res != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Introduce una cantidad valida");
            return;
        }

        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor que 0");
            return;
        }

        yaEnCarrito = carrito.cantidadProducto(p);

        try {
            // Compruebo tambien lo que ya habia en el carrito para no pasarme del stock.
            if (yaEnCarrito + cantidad > p.getStock()) {
                throw new StockInsuficienteException(
                        "Stock insuficiente.\n"
                        + "Ya tienes "
                        + yaEnCarrito
                        + " en el carrito."
                );
            }
        } catch (StockInsuficienteException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return;
        }

        carrito.anadirProducto(new itemCarrito(p, cantidad));
        JOptionPane.showMessageDialog(this, "Producto anadido al carrito");
    }

    private void mostrarProductoEnPanel(producto p) {
        // Muestro el producto en el centro de la pantalla, sin abrir otra ventana.
        form = new JPanel(new BorderLayout(10, 10));
        form.setBorder(new EmptyBorder(20, 25, 20, 25));

        imagen = new JLabel();
        imagen.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            icono = new ImageIcon(p.getImagenRuta());
            img = icono.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
            imagen.setIcon(new ImageIcon(img));
        } catch (Exception ex) {
            imagen.setText("Sin imagen");
        }

        nombreLabel = new JLabel("<html><b>" + p.getNombre() + "</b></html>");
        nombreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        precioLabel = new JLabel("Precio: " + String.format("%.2f EUR", p.getPrecio()));
        stockLabel = new JLabel("Stock disponible: " + p.getStock());

        descripcion = new JTextArea(p.getDescripcion());
        descripcion.setLineWrap(true);
        descripcion.setWrapStyleWord(true);
        descripcion.setEditable(false);
        descripcion.setOpaque(false);

        info = new JPanel(new GridLayout(0, 1, 5, 5));
        info.add(nombreLabel);
        info.add(precioLabel);
        info.add(stockLabel);
        info.add(new JScrollPane(descripcion));

        if (p.getStock() <= 0) {
            agotado = new JLabel("PRODUCTO AGOTADO");
            agotado.setForeground(Color.RED);
            agotado.setFont(new Font("Arial", Font.BOLD, 14));
            info.add(agotado);
        } else {
            cantidadPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            btnMenos = new JButton("-");
            btnMas = new JButton("+");
            txtCantidad = new JTextField("1", 4);

            limitarNumeros(txtCantidad, 3);
            btnMenos.addActionListener(e -> ajustarCantidad(txtCantidad, -1, p.getStock()));
            btnMas.addActionListener(e -> ajustarCantidad(txtCantidad, 1, p.getStock()));

            cantidadPanel.add(new JLabel("Cantidad"));
            cantidadPanel.add(btnMenos);
            cantidadPanel.add(txtCantidad);
            cantidadPanel.add(btnMas);
            info.add(cantidadPanel);
        }

        botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnVolver = new JButton("Volver");
        btnAnadir = new JButton("Anadir al carrito");

        btnVolver.addActionListener(e -> mostrarCatalogo());
        btnAnadir.addActionListener(e -> anadirProductoDesdePanel(p));
        btnAnadir.setEnabled(p.getStock() > 0);

        botones.add(btnVolver);
        botones.add(btnAnadir);

        form.add(imagen, BorderLayout.NORTH);
        form.add(info, BorderLayout.CENTER);
        form.add(botones, BorderLayout.SOUTH);

        mostrarEnCentro(new JScrollPane(form));
    }

    private void anadirProductoDesdePanel(producto p) {
        // Anado al carrito desde la vista del producto.
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Introduce una cantidad valida");
            return;
        }

        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor que 0");
            return;
        }

        yaEnCarrito = carrito.cantidadProducto(p);

        try {
            if (yaEnCarrito + cantidad > p.getStock()) {
                throw new StockInsuficienteException(
                        "Stock insuficiente.\n"
                        + "Ya tienes "
                        + yaEnCarrito
                        + " en el carrito."
                );
            }
        } catch (StockInsuficienteException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return;
        }

        carrito.anadirProducto(new itemCarrito(p, cantidad));
        JOptionPane.showMessageDialog(this, "Producto anadido al carrito");
    }

    private void ajustarCantidad(JTextField campo, int cambio, int maximo) {
        // Este metodo controla los botones de + y - de la cantidad.
        valorActual = 1;

        try {
            valorActual = Integer.parseInt(campo.getText().trim());
        } catch (Exception ignored) {
        }

        valorActual += cambio;

        if (valorActual < 1) {
            valorActual = 1;
        }

        if (valorActual > maximo) {
            valorActual = maximo;
        }

        campo.setText(String.valueOf(valorActual));
    }

    private void verCarrito() {
        // Si no hay productos no abro la tabla porque no habria nada que mostrar.
        if (carrito.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito esta vacio");
            return;
        }

        columnas = new String[]{"Producto", "Precio", "Cantidad", "Subtotal"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Relleno la tabla con los productos que el cliente ha añadido.
        for (itemCarrito item : carrito.getItems()) {
            subtotal = Math.round(item.getProducto().getPrecio() * item.getCantidad() * 100.0) / 100.0;
            modelo.addRow(new Object[]{
                item.getProducto().getNombre(),
                String.format("%.2f EUR", item.getProducto().getPrecio()),
                item.getCantidad(),
                String.format("%.2f EUR", subtotal)
            });
        }

        tabla = new JTable(modelo) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setDefaultEditor(Object.class, null);
        tabla.getTableHeader().setReorderingAllowed(false);

        // Redondeo el total para que no salgan muchos decimales.
        total = Math.round(carrito.calcularTotal() * 100.0) / 100.0;
        lblTotal = new JLabel("TOTAL: " + String.format("%.2f EUR", total), SwingConstants.RIGHT);

        btnEliminar = new JButton("Eliminar producto");
        btnCambiar = new JButton("Cambiar cantidad");
        btnPagar = new JButton("Pagar");
        btnCerrar = new JButton("Cerrar");

        botones = new JPanel();
        botones.add(btnCambiar);
        botones.add(btnEliminar);
        botones.add(btnPagar);
        botones.add(btnCerrar);

        panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(lblTotal, BorderLayout.NORTH);
        panel.add(botones, BorderLayout.SOUTH);

        dialog = new JDialog(this, "Carrito", true);
        dialog.setSize(720, 420);
        dialog.setLocationRelativeTo(this);
        dialog.add(panel);

        btnEliminar.addActionListener(e -> eliminarProductoCarrito());
        btnCambiar.addActionListener(e -> cambiarCantidadCarrito());
        btnPagar.addActionListener(e -> {
            dialog.dispose();
            procesarPago();
        });
        btnCerrar.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void eliminarProductoCarrito() {
        // Elimino el producto que este seleccionado en la tabla.
        fila = tabla.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(dialog, "Selecciona un producto");
            return;
        }

        carrito.getItems().remove(fila);
        dialog.dispose();
        verCarrito();
    }

    private void cambiarCantidadCarrito() {
        // Cambio la cantidad desde el carrito para no tener que borrar y volver a añadir.
        fila = tabla.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(dialog, "Selecciona un producto");
            return;
        }

        itemActual = carrito.getItems().get(fila);
        texto = JOptionPane.showInputDialog(
                dialog,
                "Nueva cantidad para " + itemActual.getProducto().getNombre(),
                itemActual.getCantidad()
        );

        if (texto == null) {
            return;
        }

        try {
            nuevaCantidad = Integer.parseInt(texto.trim());

            if (nuevaCantidad <= 0) {
                JOptionPane.showMessageDialog(dialog, "La cantidad debe ser mayor que 0");
                return;
            }

            if (nuevaCantidad > itemActual.getProducto().getStock()) {
                JOptionPane.showMessageDialog(dialog, "No hay stock suficiente");
                return;
            }

            carrito.getItems().set(fila, new itemCarrito(itemActual.getProducto(), nuevaCantidad));
            dialog.dispose();
            verCarrito();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Cantidad invalida");
        }
    }

    private void limitarNumeros(JTextField campo, int maximo) {
        // Filtro para que en algunos campos solo entren numeros y con un largo maximo.
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                actual = fb.getDocument().getText(0, fb.getDocument().getLength());
                nuevo = actual.substring(0, offset) + text + actual.substring(offset + length);

                if (nuevo.matches("\\d*") && nuevo.length() <= maximo) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    private void limitarFecha(JTextField campo) {
        // Para la fecha dejo escribir el formato MM/AA poco a poco.
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                actual = fb.getDocument().getText(0, fb.getDocument().getLength());
                nuevo = actual.substring(0, offset) + text + actual.substring(offset + length);

                if (nuevo.matches("\\d{0,2}(/\\d{0,2})?") && nuevo.length() <= 5) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    private void procesarPago() {
        // Formulario con los datos de la tarjeta para finalizar la compra.
        numTarjeta = new JTextField(16);
        titular = new JTextField(20);
        caducidad = new JTextField(5);
        cvv = new JPasswordField(3);

        limitarNumeros(numTarjeta, 16);
        limitarNumeros(cvv, 3);
        limitarFecha(caducidad);

        tarjetas = new tarjetaDAO().obtenerTarjetasDeCliente(cliente.getId());
        comboTarjetas = new JComboBox<>();
        comboTarjetas.addItem("Nueva tarjeta");

        // Muestro solo los ultimos 4 numeros para que se vea mas limpio.
        for (tarjeta t : tarjetas) {
            comboTarjetas.addItem("**** " + t.getNumero().substring(t.getNumero().length() - 4));
        }

        comboTarjetas.addActionListener(e -> cargarTarjetaSeleccionada());

        form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Tarjetas guardadas"));
        form.add(comboTarjetas);
        form.add(new JLabel("Numero tarjeta"));
        form.add(numTarjeta);
        form.add(new JLabel("Titular"));
        form.add(titular);
        form.add(new JLabel("Caducidad MM/AA"));
        form.add(caducidad);
        form.add(new JLabel("CVV"));
        form.add(cvv);

        res = JOptionPane.showConfirmDialog(this, form, "Pago", JOptionPane.OK_CANCEL_OPTION);

        if (res != JOptionPane.OK_OPTION) {
            return;
        }

        numero = numTarjeta.getText().trim();
        nombreTitular = titular.getText().trim();
        fecha = caducidad.getText().trim();
        codigo = new String(cvv.getPassword()).trim();

        if (!validarDatosTarjeta(numero, fecha, codigo)) {
            return;
        }

        valida = new tarjetaDAO().validarTarjeta(cliente.getId(), numero, nombreTitular, fecha, codigo);

        if (!valida) {
            // Si la tarjeta no existe, doy opcion a guardarla para proximas compras.
            guardar = JOptionPane.showConfirmDialog(
                    this,
                    "La tarjeta no esta guardada. Quieres guardarla?",
                    "Guardar",
                    JOptionPane.YES_NO_OPTION
            );

            if (guardar == JOptionPane.YES_OPTION) {
                nuevaTarjeta = new tarjeta();
                nuevaTarjeta.setIdCliente(cliente.getId());
                nuevaTarjeta.setNumero(numero);
                nuevaTarjeta.setTitular(nombreTitular);
                nuevaTarjeta.setCaducidad(fecha);
                nuevaTarjeta.setCvv(codigo);

                insertada = new tarjetaDAO().insertarTarjeta(nuevaTarjeta);

                if (insertada) {
                    JOptionPane.showMessageDialog(this, "Tarjeta guardada");
                    valida = true;
                }
            } else {
                return;
            }
        }

        if (!valida) {
            JOptionPane.showMessageDialog(this, "No se pudo validar la tarjeta");
            return;
        }

        // Si todo esta correcto, creo el pedido con los productos del carrito.
        pedidoCliente = new pedido();
        pedidoCliente.setIdCliente(cliente.getId());
        pedidoCliente.setTotal(carrito.calcularTotal());

        idPedido = new pedidoDAO().crearPedido(pedidoCliente, carrito.getItems());

        if (idPedido > 0) {
            carrito.vaciar();
            JOptionPane.showMessageDialog(this, "Compra realizada\nPedido No " + idPedido);
        } else {
            JOptionPane.showMessageDialog(this, "Error al crear pedido");
        }
    }

    private void cargarTarjetaSeleccionada() {
        // Cuando el usuario elige una tarjeta guardada, relleno los campos solos.
        index = comboTarjetas.getSelectedIndex();

        if (index > 0) {
            tarjetaActual = tarjetas.get(index - 1);
            numTarjeta.setText(tarjetaActual.getNumero());
            titular.setText(tarjetaActual.getTitular());
            caducidad.setText(tarjetaActual.getCaducidad());
            cvv.setText(tarjetaActual.getCvv());
        } else {
            numTarjeta.setText("");
            titular.setText("");
            caducidad.setText("");
            cvv.setText("");
        }
    }

    private boolean validarDatosTarjeta(String numero, String fecha, String codigo) {
        // Validaciones basicas antes de intentar guardar o pagar con la tarjeta.
        try {
            if (!numero.matches("\\d{16}")) {
                throw new TarjetaInvalidaException("La tarjeta debe tener 16 numeros");
            }
        } catch (TarjetaInvalidaException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return false;
        }

        try {
            if (!codigo.matches("\\d{3}")) {
                throw new TarjetaInvalidaException("El CVV debe tener 3 numeros");
            }
        } catch (TarjetaInvalidaException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return false;
        }

        if (!fecha.matches("(0[1-9]|1[0-2])\\/\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Formato fecha MM/AA");
            return false;
        }

        return true;
    }

    private void verHistorial() {
        // Cargo todos los pedidos anteriores del cliente.
        pedidos = new pedidoDAO().obtenerHistorial(cliente.getId());

        if (pedidos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No tienes pedidos");
            return;
        }

        columnas = new String[]{"ID", "Fecha", "Total"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Relleno la tabla del historial con los pedidos encontrados.
        for (pedido p : pedidos) {
            modelo.addRow(new Object[]{
                p.getId(),
                p.getFecha(),
                String.format("%.2f EUR", p.getTotal())
            });
        }

        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setDefaultEditor(Object.class, null);
        tabla.getTableHeader().setReorderingAllowed(false);

        btnDetalle = new JButton("Ver detalle");
        btnDetalle.addActionListener(e -> verDetalleSeleccionado());

        panel = new JPanel(new BorderLayout(0, 10));
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(btnDetalle, BorderLayout.SOUTH);

        dialog = new JDialog(this, "Historial de pedidos", true);
        dialog.setSize(650, 350);
        dialog.setLocationRelativeTo(this);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void verDetalleSeleccionado() {
        // Compruebo que haya un pedido seleccionado antes de ver su detalle.
        fila = tabla.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un pedido");
            return;
        }

        idPedido = (int) tabla.getValueAt(fila, 0);
        mostrarDetallePedido(idPedido);
    }

    private void mostrarDetallePedido(int idPedido) {
        // Aqui se muestran los productos que pertenecen a un pedido concreto.
        detalle = new pedidoDAO().obtenerDetallePedido(idPedido);

        try {
            if (detalle == null || detalle.isEmpty()) {
                throw new PedidoNoEncontradoException("Pedido no encontrado");
            }
        } catch (PedidoNoEncontradoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return;
        }

        columnas = new String[]{"Producto", "Cantidad", "Precio"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (itemCarrito item : detalle) {
            modelo.addRow(new Object[]{
                item.getProducto().getNombre(),
                item.getCantidad(),
                String.format("%.2f EUR", item.getProducto().getPrecio())
            });
        }

        tabla = new JTable(modelo);
        tabla.setDefaultEditor(Object.class, null);
        tabla.getTableHeader().setReorderingAllowed(false);

        JOptionPane.showMessageDialog(
                this,
                new JScrollPane(tabla),
                "Detalle del pedido #" + idPedido,
                JOptionPane.PLAIN_MESSAGE
        );
    }

    private void verQuejas() {
        // Ventana para ver las quejas del cliente y crear una nueva.
        panel = new JPanel(new BorderLayout(0, 10));

        arriba = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNueva = new JButton("Nueva queja");
        arriba.add(btnNueva);
        panel.add(arriba, BorderLayout.NORTH);

        columnas = new String[]{"ID", "Pedido", "Mensaje"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        quejas = new quejaDAO().obtenerPorCliente(cliente.getId());

        for (queja q : quejas) {
            modelo.addRow(new Object[]{
                q.getId(),
                q.getIdPedido(),
                q.getMensaje()
            });
        }

        tabla = new JTable(modelo);
        tabla.setDefaultEditor(Object.class, null);
        tabla.getTableHeader().setReorderingAllowed(false);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        btnNueva.addActionListener(e -> nuevaQueja());

        dialog = new JDialog(this, "Quejas", true);
        dialog.setSize(700, 400);
        dialog.setLocationRelativeTo(this);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void nuevaQueja() {
        // Formulario spara escribir la queja
        idPedidoTxt = new JTextField(5);
        mensaje = new JTextArea(4, 20);
        mensaje.setLineWrap(true);
        mensaje.setWrapStyleWord(true);

        form = new JPanel(new BorderLayout(5, 5));
        top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("ID Pedido"));
        top.add(idPedidoTxt);

        form.add(top, BorderLayout.NORTH);
        form.add(new JScrollPane(mensaje), BorderLayout.CENTER);

        res = JOptionPane.showConfirmDialog(this, form, "Enviar queja", JOptionPane.OK_CANCEL_OPTION);

        if (res != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            nuevaQuejaObj = new queja();
            idPedidoQueja = Integer.parseInt(idPedidoTxt.getText().trim());
            pedidoValido = new pedidoDAO().perteneceACliente(idPedidoQueja, cliente.getId());

            if (!pedidoValido) {
                JOptionPane.showMessageDialog(this, "Ese pedido no existe o no pertenece a tu cuenta");
                return;
            }

            nuevaQuejaObj.setIdCliente(cliente.getId());
            nuevaQuejaObj.setIdPedido(idPedidoQueja);
            nuevaQuejaObj.setMensaje(mensaje.getText().trim());

            new quejaDAO().insertar(nuevaQuejaObj);
            JOptionPane.showMessageDialog(this, "Queja enviada");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "ID invalido");
        }
    }
}
