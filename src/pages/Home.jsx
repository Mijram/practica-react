import {useEffect, useMemo, useState} from "react";
import {ListProductosApi} from "../api/Productos/ListProductosApi.jsx";
import ListProductos from "../components/Productos/ListProductos.jsx";
import '/src/styles/pages/ProductoCategoria.css'

export default function Home() {

    const [categoria, setCategoria] = useState('todas');

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

    const handleChange = (event) => {
        setCategoria(event.target.value);
    };

    const categorias = useMemo(
        () => [...new Set(getProductos.map((producto) => producto.categoria))],
        [getProductos],
    );

    const productosFiltrados = useMemo(() => {
        return getProductos.filter((producto) => {
            const coincideCategoria = categoria === 'todas' || producto.categoria === categoria;
            return coincideCategoria;
        });
    }, [getProductos, categoria]);


    console.log("categorias")


    return(
        <main className="ProductoCategoria">
            <select value={categoria} onChange={handleChange} className="filtroCategorias">
                <option value="todas">Todas</option>
                {categorias.map((item) => (
                    <option key={item} value={item}>{item}</option>
                ))}
            </select>
            <ListProductos
                productos={productosFiltrados}/>
        </main>
    );
}