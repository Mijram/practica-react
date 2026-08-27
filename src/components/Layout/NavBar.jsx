import {useEffect, useState} from "react";
import { useNavigate, Link } from 'react-router-dom';

import '../../styles/Layout/NavBar.css';
import Search from "./Search.jsx";
import {CategoriasApi as obtenerCategorias} from "../../api/CategoriasApi.jsx";
import logo from "/src/assets/logo.svg"


export default function NavBar(){
    //const navigate = useNavigate();
    //let handleClick = navigate('/')
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
                    {categorias.map((categoria) => (
                        <Link className="categoriasLink" to={`/${categoria.nombre}`}
                            key={categoria.id}>
                            {categoria.nombre}
                        </Link>
                    ))}
                </section>
            </nav>
        </header>
    )
}