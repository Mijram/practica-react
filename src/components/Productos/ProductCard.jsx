import "/src/styles/Productos/ProductCard.css"
import {formatPrecio} from "../Formmaters/FormatPrice.jsx";

export default function ProductCard({producto}){
    return(
        <section className="ProductCard">
            <h3>{producto.nombre}</h3>
            <img src="/src/assets/404.png" alt={producto.nombre}/>
            <p className="descripcion">{producto.descripcion}</p>
            <strong><p className="precio">{formatPrecio(producto.precio)}</p></strong>
        </section>
    );
}

