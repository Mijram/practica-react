import "/src/styles/pages/Carrito.css"
//la clase va a usar valores simulados, solo para probar la apariencia a tomar en la pagina
export default function Carrito() {
    return(
        <article className="carrito">
            <section className="ListaProductos">
                <div className="compartir"><input type="checkbox"/> compartir carrito</div>
                <div className="seccionProveedor">
                    <h2>Productos de proveedor</h2>
                    <div className="detalleProducto" >
                        <input type="checkbox"/>
                        <img src="/src/assets/404.png" alt="nombre producto"/>
                        <h3>nombre producto</h3>
                        <input type="number" defaultValue={1} min="1"/>
                        <p className="eliminar">eliminar</p>
                    </div>
                </div>
            </section>
            <section className="resumenCompra">
                <h3>Total</h3>
                <p className="precioTotal">precio</p>
                <button className="comprar">
                    Comprar ahora
                </button>
            </section>
        </article>
    );
}
