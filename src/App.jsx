//componentes
import ListarProductosCategoria from "./components/Productos/ListarProductosCategoria.jsx";
import NavBar from "./components/nav/NavBar.jsx"
import Footer from "./components/Footer.jsx";

//utils
import './App.css'
import { useState } from 'react';

//pages


function App() {
  const [categoriaSeleccionada, setCategoriaSeleccionada] = useState(null);
  const [productoBuscado, setProductoBuscado] = useState(null);

  return (
      <>
        <NavBar
          categoriaSeleccionada={categoriaSeleccionada}
          alSeleccionarCategoria={setCategoriaSeleccionada}
          productoBuscado = {productoBuscado}
          alBuscarProducto = {setProductoBuscado}
        />
        <ListarProductosCategoria
            productoBuscado = {productoBuscado}
            categoriaSeleccionada={categoriaSeleccionada}/>
        <Footer/>
      </>
  )
}

export default App
