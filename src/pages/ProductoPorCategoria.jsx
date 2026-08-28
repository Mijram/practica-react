import {useParams} from 'react-router-dom';
import {useEffect, useState} from "react";
import {ListProductosApi} from "../api/Productos/ListProductosApi.jsx";
import ListProductos from "../components/Productos/ListProductos.jsx";
import '/src/styles/pages/ProductoCategoria.css'


export default function ProductoPorCategoria() {

    const {categoriaNombre} = useParams();

    const [getProductos, setProductos] = useState([]);

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

    const productos = getProductos.filter((producto) => producto.categoria === categoriaNombre)

    return(
        <main className="ProductoCategoria">
            <h1>{categoriaNombre}</h1>
            <ListProductos productos={productos}/>
        </main>
    );
}