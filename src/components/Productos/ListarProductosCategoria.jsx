import { useEffect, useState } from 'react';
import { Productos as obtenerProductos } from '../../api/Productos.jsx';
import { Categorias as obtenerCategorias } from '../../api/Categorias.jsx';
import '../../styles/ListarProductosCategoría.css';
import { formatText } from '../Formmaters/FormatText.jsx';
import ProductCard from "./ProductCard.jsx";

function Categoria({ nombreCategoria, productos }) {
    const productoCategoria = productos.filter(
        (producto) => producto.categoria === nombreCategoria,
    );

    if (productoCategoria.length === 0) return null;

    return (
        <section className="categoriaCard">
            <h2><strong>{nombreCategoria}</strong></h2>
            {productoCategoria.map((producto) => (
                <ProductCard
                    key={producto.id}
                    producto={producto}
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
