import {BrowserRouter, Route, Routes} from 'react-router-dom';

import AppLayout from "./components/Layout/AppLayout.jsx";
import Home from "./pages/Home.jsx";
import ProductoDetail from "./pages/ProductoDetail.jsx";
import Carrito from "./pages/Carrito.jsx";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<AppLayout />}>
                    <Route path="/producto/:id" element={<ProductoDetail/>}/>
                    <Route path={"/carrito"} element={<Carrito/>}/>
                    <Route index element={<Home/>}/>
                </Route>
            </Routes>
        </BrowserRouter>
    );
}

export default App;
