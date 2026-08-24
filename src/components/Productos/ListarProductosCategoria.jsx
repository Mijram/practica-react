import productos from "../../data.json"
import '../../styles/ListarProductosCategoría.css'
function Product({nombre, descripcion, precio, calificacion}){
    return (
        <article className="grid-productos">
            <h3>{nombre}</h3>
            <p>{descripcion}</p>
            <p className="precio">{precio}</p>
            <p className="calificacion">Calificación: {calificacion}</p>
        </article>
    );
}

function Categoria({ nombreCategoria }){
    const productoCategoria = productos.filter((producto) => producto.categoria === nombreCategoria);
    if(productoCategoria.length === 0) return null;

    return(
        <section className="categoriaCard">
            <h2><strong>{nombreCategoria}</strong></h2>
            {productoCategoria.map((p) => (
                <Product
                    key={p.id}
                    nombre={p.nombre}
                    descripcion={p.descripcion}
                    precio={p.precio}
                    calificacion={p.calificacion}
                />
            ))}
        </section>

    );
}

export default function ListarProductosCategoria(){
    const categorias = [...new Set(productos.map((producto) => producto.categoria))];

    return(
        <main>
            {categorias.map((categoria) => (
                <Categoria key={categoria} nombreCategoria={categoria}/>
            ))}
        </main>
    )
}
