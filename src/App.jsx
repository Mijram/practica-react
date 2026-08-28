import {createBrowserRouter, RouterProvider} from 'react-router-dom';

import AppLayout from "./components/Layout/AppLayout.jsx";
import TodosProductos from "./components/Productos/TodosProductos.jsx";
import ProductoPorCategoria from "./pages/ProductoPorCategoria.jsx";
import ProductoDetail from "./pages/ProductoDetail.jsx";
import Carrito from "./pages/Carrito.jsx";

const router = createBrowserRouter([
    {
        path: '/',
        element: <AppLayout />,
        children: [
            {
                path: '/',
                element: <TodosProductos />,
            },
            {
                path: '/carrito',
                element: <Carrito/>
            },
            {
                path:'/categorias/:categoriaNombre',
                element: <ProductoPorCategoria/>
            },
            {
                path:`/producto/:id`,
                element: <ProductoDetail/>
            }
        ],
    },
]);

function App() {
    return <RouterProvider router={router} />;
}

export default App;
