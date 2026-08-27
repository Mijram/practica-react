import { createBrowserRouter, RouterProvider } from 'react-router-dom';

import AppLayout from "./components/Layout/AppLayout.jsx";
import ListProductos from "./components/Productos/ListProductos.jsx";

const router = createBrowserRouter([
    {
        path: '/',
        element: <AppLayout />,
        children: [
            {
                path: '/',
                element: <ListProductos />,
            },
        ],
    },
]);

function App() {
    return <RouterProvider router={router} />;
}

export default App;
