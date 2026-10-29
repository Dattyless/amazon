package clases;

public class itemCarrito {

    private producto producto;
    private int cantidad;

    public itemCarrito(producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public producto getProducto() {
        return producto;
    }

    public void setProducto(producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getSubtotal() {
        // Precio de este producto segun la cantidad seleccionada.
        return producto.getPrecio() * cantidad;
    }
}
