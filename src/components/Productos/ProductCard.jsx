import "/src/styles/Productos/ProductCard.css"
import {formatPrecio} from "../Formmaters/FormatPrice.jsx";
import { useNavigate} from "react-router-dom";

export default function ProductCard({producto}){
    const navigate = useNavigate();
    const handleClickDetail = () => navigate(`/producto/${producto.id}`, {preventScrollReset: false})
    return(
        <section className="ProductCard" onClick={handleClickDetail}>
            <h3>{producto.nombre}</h3>
            <img src="/src/assets/404.png" alt={producto.nombre}/>
            <p className="descripcion">{producto.descripcion}</p>
            <strong><p className="precio">{formatPrecio(producto.precio)}</p></strong>
        </section>
    );
}

