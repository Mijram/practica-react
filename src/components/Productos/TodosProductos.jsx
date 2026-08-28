import {useEffect, useState} from "react";
import {ListProductosApi} from "../../api/Productos/ListProductosApi.jsx";
import ListProductos from "./ListProductos.jsx";

export default function TodosProductos() {

    const [getProducto, setProductos] = useState([]);

    useEffect(()=> {
        async function cargarProductos(){
            try{
                const productos = await ListProductosApi();
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

    return <ListProductos productos={getProducto}/>;
}
