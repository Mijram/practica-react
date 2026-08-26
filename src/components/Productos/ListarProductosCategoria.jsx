import { useEffect, useState } from 'react';
import { Productos as obtenerProductos } from '../../api/Productos.jsx';
import { Categorias as obtenerCategorias } from '../../api/Categorias.jsx';
import '../../styles/ListarProductosCategoría.css';
import {formatPrecio} from "../Formmaters/FormatPrice.jsx";
import { formatText } from '../Formmaters/FormatText.jsx';

function Product({ nombre, descripcion, precio }) { // se debe implementar calificacion
    return (
        <article className="grid-productos">
            <h3>{nombre}</h3>
            <p>{descripcion}</p>
            <p className="precio">{precio}</p>
        </article>
    );
}

function Categoria({ nombreCategoria, productos }) {
    const productoCategoria = productos.filter(
        (producto) => producto.categoria === nombreCategoria,
    );

    if (productoCategoria.length === 0) return null;

    return (
        <section className="categoriaCard">
            <h2><strong>{nombreCategoria}</strong></h2>
            {productoCategoria.map((producto) => (
                <Product
                    key={producto.id}
                    nombre={producto.nombre}
                    descripcion={producto.descripcion}
                    precio={formatPrecio(producto.precio)}
                    calificacion={producto.calificacion}
                />
            ))}
        </section>
    );
}

export default function ListarProductosCategoria({ categoriaSeleccionada, productoBuscado }) {
    const [productos, setProductos] = useState([]);
    const [categorias, setCategorias] = useState([]);

    useEffect(() => {
        async function cargarProductos() {
            try {
                const productosObtenidos = await obtenerProductos();
                setProductos(productosObtenidos);
            } catch (error) {
                console.log("ocurrio un error al obtener los productos: ", error);
            }
        }
        cargarProductos();
    }, []);

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


    const categoriasVisibles = categoriaSeleccionada
        ? categorias.filter((categoria) => categoria.nombre === categoriaSeleccionada)
        : categorias;

    const productosVisibles = productoBuscado
        ? productos.filter((producto) =>
            formatText(producto.nombre).includes(formatText(productoBuscado)),
        )
        : productos;

    return (
        <main>
            {categoriasVisibles.map((categoria) => (
                <Categoria
                    key={categoria.id}
                    nombreCategoria={categoria.nombre}
                    productos={productosVisibles}
                />
            ))}
        </main>
    );
}
