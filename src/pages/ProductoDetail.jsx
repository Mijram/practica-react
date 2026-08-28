import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import {ProductoIdApi} from "../api/Productos/ProductoIdApi.jsx";
import "/src/styles/pages/ProductoDetail.css"
import {formatPrecio} from "../components/Formmaters/FormatPrice.jsx";
import TodosProductos from "../components/Productos/TodosProductos.jsx";

export default function ProductoDetail(){
    const [getProducto, setProducto] = useState(null);
    const {id} = useParams();

    useEffect(()=> {
        async function cargarProducto(){
            try{
                const producto = await ProductoIdApi(id);
                setProducto(producto);
            }
            catch(e){
                console.log("Error al obtener producto", e);
                throw e
            }
        }
        cargarProducto();
    }, [id]);

    if (!getProducto) return <div>Cargando...</div>

    console.log("getProducto:", getProducto, "tipo:", typeof getProducto, "es array:", Array.isArray(getProducto));

    return(
        <>
            <article className="ProductoDetail">
                <section className="imagenes">
                    <img src="/src/assets/404.png" alt={getProducto.nombre}/>
                </section>
                <section className="detalle">
                    <h1 className="tituloProducto">{getProducto.nombre}</h1>
                    <p><strong>{formatPrecio(getProducto.precio)}</strong></p>
                    <p>{getProducto.descripcion}</p>
                    <p>Unidades restantes: {getProducto.stock}</p>
                </section>
                <section className="comprar">
                    <p className="descripcionCompra">
                        Llega gratis mañana por ser tu primera compra<br/>

                        Más detalles y formas de entrega<br/>
                        Retira gratis a partir de mañana en una agencia de Mercado Libre<br/>
                        Comprando dentro de las próximas 8 h 55 min<br/>
                        Ver en el mapa<br/>
                        Stock disponible<br/>

                        Almacenado y enviado por {getProducto.proveedor}<br/>
                        Full<br/>
                    </p>
                    <button className="comprarAhora">Comprar ahora</button><br/>
                    <button className="agregarCarrito">Agregar al carrito</button>
                    <h4 className="proveedor">proveedor: {getProducto.proveedor}</h4>
                </section>
            </article>
            <aside className="masProductos">
                <h2>Mas productos</h2>
                <TodosProductos/>
            </aside>
        </>
    );
}