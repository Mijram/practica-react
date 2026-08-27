import {ProductosApi} from "../../api/ProductosApi.jsx";
import {useEffect, useState} from "react";
import ProductCard from "./ProductCard.jsx";
import "/src/styles/Productos/ListProducts.jsx.css"



export default function ListProductos(){

    const [getProducto, setProductos] = useState([]);

    useEffect(()=> {
        async function cargarProductos(){
            try{
                const productos = await ProductosApi();
                setProductos(productos);
                console.log("Productos conseguidos");
            }
            catch(e){
                console.log("Error al obtener productos", e);
                throw e
            }
        }
        cargarProductos();
    }, []);
    return(
        <main className="listProductos">
            {getProducto.map((productoActual) => (
                    <ProductCard
                    key={productoActual.id}
                    producto={productoActual}
                    />))}
        </main>
    );

}