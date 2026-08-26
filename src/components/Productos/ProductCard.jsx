import ProductoPage from "../../pages/ProductoPage.jsx";
import {formatPrecio} from "../Formmaters/FormatPrice.jsx";

export default function ProductCard({producto}){ // se debe implementar calificacion

    return (
            <article className="grid-productos" onClick={ProductoPage
            }>
                <h3>{producto.nombre}</h3>
                <img src="/src/assets/404.jpeg" alt={producto.nombre}/>
                <p>{producto.descripcion}</p>
                <p className="precio">{formatPrecio(producto.precio)}</p>
            </article>
    );
}