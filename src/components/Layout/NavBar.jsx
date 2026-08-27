import '../../styles/Layout/NavBar.css';
import Search from "./Search.jsx";
import {useEffect, useState} from "react";
import {CategoriasApi as obtenerCategorias} from "../../api/CategoriasApi.jsx";
import logo from "/src/assets/logo.svg"


export default function NavBar(){
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
                <Search/>
                <section className="categorias">
                    <button>
                        Todas las categorías
                    </button>
                    {categorias.map((categoria) => (
                        <button
                            key={categoria.id}>
                            {categoria.nombre}
                        </button>
                    ))}
                </section>
            </nav>
        </header>
    )
}