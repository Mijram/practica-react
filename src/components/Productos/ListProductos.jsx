import ProductCard from "./ProductCard.jsx";
import "/src/styles/Productos/ListProducts.css"


//componente para listar productos de forma dinamica según la entrada o el filtro
export default function ListProductos({productos}){


    return(
        <main className="listProductos">
            {productos.map((productoActual) => (
                    <ProductCard
                    key={productoActual.id}
                    producto={productoActual}
                    />))}
        </main>
    );

}