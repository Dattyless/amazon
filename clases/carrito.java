package clases;

import java.util.ArrayList;
import java.util.List;

public class carrito {

    private List<itemCarrito> items;

    // Variables auxiliares para calcular y buscar dentro del carrito
    private int totalCantidad;
    private double total;

    public carrito() {
        items = new ArrayList<>();
    }

    public void anadirProducto(itemCarrito nuevoItem) {
        // Si el producto ya existe en el carrito, solo sumo la cantidad.
        for (itemCarrito item : items) {
            if (item.getProducto().getId() == nuevoItem.getProducto().getId()) {
                item.setCantidad(item.getCantidad() + nuevoItem.getCantidad());
                return;
            }
        }

        items.add(nuevoItem);
    }

    public void eliminarProducto(int idProducto) {
        // Elimino el producto por su id.
        items.removeIf(i -> i.getProducto().getId() == idProducto);
    }

    public int cantidadProducto(producto p) {
        // Devuelve cuantas unidades de ese producto ya hay en el carrito.
        totalCantidad = 0;

        for (itemCarrito item : items) {
            if (item.getProducto().getId() == p.getId()) {
                totalCantidad += item.getCantidad();
            }
        }

        return totalCantidad;
    }

    public void modificarCantidad(int idProducto, int nuevaCantidad) {
        // Cambia la cantidad de un producto que ya esta dentro.
        for (itemCarrito item : items) {
            if (item.getProducto().getId() == idProducto) {
                item.setCantidad(nuevaCantidad);
                return;
            }
        }
    }

    public double calcularTotal() {
        // Sumo todos los subtotales para sacar el precio final.
        total = 0;

        for (itemCarrito item : items) {
            total += item.getSubtotal();
        }

        return total;
    }

    public void vaciar() {
        // Despues de pagar dejo el carrito vacio.
        items.clear();
    }

    public List<itemCarrito> getItems() {
        return items;
    }
}
