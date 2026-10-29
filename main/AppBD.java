package main;

import java.sql.*;

public class AppBD {

    public static void inicializar() {

        try {

            System.out.println("Intentando conectar...");

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/amazon",
                    "root",
                    ""
            );

            System.out.println("Conexion exitosa");

            Statement st = con.createStatement();

            DatabaseMetaData meta = con.getMetaData();

            ResultSet rs = meta.getTables(null, null, "usuarios", null);

            if (!rs.next()) {

                System.out.println("La BD esta vacia, creando tablas...");

                st.executeUpdate("CREATE TABLE usuarios ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "username VARCHAR(50) NOT NULL UNIQUE, "
                        + "password VARCHAR(100) NOT NULL, "
                        + "email VARCHAR(100), "
                        + "rol VARCHAR(10) NOT NULL DEFAULT 'cliente')");

                st.executeUpdate("CREATE TABLE productos ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "nombre VARCHAR(100) NOT NULL, "
                        + "descripcion TEXT, "
                        + "precio DOUBLE NOT NULL, "
                        + "stock INT NOT NULL DEFAULT 0, "
                        + "categoria VARCHAR(50), "
                        + "imagen_ruta VARCHAR(200))");

                st.executeUpdate("CREATE TABLE tarjetas ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "id_cliente INT NOT NULL, "
                        + "numero VARCHAR(20) NOT NULL, "
                        + "titular VARCHAR(100) NOT NULL, "
                        + "caducidad VARCHAR(7) NOT NULL, "
                        + "cvv VARCHAR(4) NOT NULL, "
                        + "FOREIGN KEY (id_cliente) REFERENCES usuarios(id))");

                st.executeUpdate("CREATE TABLE pedidos ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "id_cliente INT NOT NULL, "
                        + "fecha DATETIME DEFAULT NOW(), "
                        + "total DOUBLE NOT NULL, "
                        + "FOREIGN KEY (id_cliente) REFERENCES usuarios(id))");

                st.executeUpdate("CREATE TABLE detalle_pedido ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "id_pedido INT NOT NULL, "
                        + "id_producto INT NOT NULL, "
                        + "cantidad INT NOT NULL, "
                        + "precio_unit DOUBLE NOT NULL, "
                        + "FOREIGN KEY (id_pedido) REFERENCES pedidos(id), "
                        + "FOREIGN KEY (id_producto) REFERENCES productos(id))");

                st.executeUpdate("CREATE TABLE quejas ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "id_cliente INT NOT NULL, "
                        + "id_pedido INT NOT NULL, "
                        + "mensaje TEXT NOT NULL, "
                        + "fecha DATETIME DEFAULT NOW(), "
                        + "FOREIGN KEY (id_cliente) REFERENCES usuarios(id), "
                        + "FOREIGN KEY (id_pedido) REFERENCES pedidos(id))");

                st.executeUpdate("INSERT INTO usuarios (username,password,email,rol) VALUES "
                        + "('admin','admin123','admin@amazon.com','admin'),"
                        + "('cliente1','pass123','cliente1@gmail.com','cliente'),"
                        + "('cliente2','pass456','cliente2@gmail.com','cliente')");

                st.executeUpdate("INSERT INTO productos (nombre,descripcion,precio,stock,categoria,imagen_ruta) VALUES "

                        + "('Portatil HP','Intel i5 15 pulgadas',699.99,10,'informatica','img/portatil.jpg'),"

                        + "('Silla Gaming','Ergonomica con reposapies',199.99,5,'muebles','img/silla.jpg'),"

                        + "('Harry Potter 1','Libro de J.K. Rowling',12.99,50,'libros','img/hp.jpg'),"

                        + "('Cafetera Nespresso','Cafetera de capsulas',89.99,15,'cafeteras','img/cafetera.jpg'),"

                        + "('Auriculares Sony','Cancelacion de ruido activa',149.99,20,'ocio','img/sony.jpg'),"

                        + "('Auriculares Bluetooth','Auriculares inalambricos con microfono',59.99,25,'informatica','img/auriculares.jpg'),"

                        + "('Cafetera Capsulas','Cafetera automatica de capsulas',79.99,12,'cafeteras','img/cafetera_capsulas.jpg'),"

                        + "('Escritorio Gaming','Escritorio grande con luces LED',149.99,8,'muebles','img/escritorio.jpg'),"

                        + "('Libro Java','Guia completa de programacion Java',29.99,30,'libros','img/libro_java.jpg'),"

                        + "('Mando PS5','Mando inalambrico DualSense',69.99,18,'ocio','img/mando.jpg'),"

                        + "('Novela Misterio','Libro de suspense y misterio',14.99,40,'libros','img/novela.jpg'),"

                        + "('Puzzle 1000 Piezas','Puzzle panoramico para adultos',19.99,22,'ocio','img/puzzle.jpg'),"

                        + "('Raton Logitech','Raton gaming RGB',49.99,35,'informatica','img/raton.jpg'),"

                        + "('Teclado Mecanico','Teclado mecanico RGB switches blue',89.99,16,'informatica','img/teclado.jpg')");

                st.executeUpdate("INSERT INTO tarjetas (id_cliente,numero,titular,caducidad,cvv) VALUES "
                        + "(2,'1234567890123456','CLIENTE UNO','12/27','123'),"
                        + "(3,'9876543210987654','CLIENTE DOS','06/26','456')");

                System.out.println("Tablas creadas correctamente.");

            } else {

                System.out.println("Las tablas ya existen, no se crea nada.");

            }

            rs.close();
            st.close();
            con.close();

        } catch (ClassNotFoundException e) {

            System.err.println("Driver no encontrado: " + e.getMessage());

        } catch (SQLException e) {

            System.err.println("Error BD: " + e.getMessage());

        }
    }
}
