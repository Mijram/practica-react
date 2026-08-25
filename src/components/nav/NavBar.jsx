import '../../styles/navBar.css';
import Search from "../Search/Search.jsx";
import logo from '../../assets/logo.png'
import {useEffect, useState} from "react";
import {Categorias as obtenerCategorias} from "../../api/Categorias.jsx";


export default function NavBar({ categoriaSeleccionada, alSeleccionarCategoria, productoBuscado, alBuscarProducto }){
    const [categorias, setCategorias] = useState([]);
    useEffect(() => {
        async function cargarCategorias(){
            try{
                const cargarCategorias = await obtenerCategorias();
                setCategorias(cargarCategorias);
            } catch(error){
                console.log("Ocurrio un error al obtener las categorías: ", error);
            }
        }
        cargarCategorias();
    }, []);

    return(
        <header>
            <nav>
                <a href="/" className="logo">
                    <img src={logo} alt="Marketplace" />
                </a>
                <Search
                    productoBuscado = {productoBuscado}
                    alBuscarProducto = {alBuscarProducto}/>
                <section className="categorias">
                    <button
                        onClick={() => alSeleccionarCategoria(null)}
                    >
                        Todas las categorías
                    </button>
                    {categorias.map((categoria) => (
                        <button
                            key={categoria.id}
                            className={categoriaSeleccionada === categoria.nombre ? 'activa' : ''}
                            onClick={() => alSeleccionarCategoria(categoria.nombre)}
                        >
                            {categoria.nombre}
                        </button>
                    ))}
                </section>
            </nav>
        </header>
    )
}
