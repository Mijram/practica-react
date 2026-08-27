import '/src/App.css'
import NavBar from "./NavBar.jsx";
import Footer from "./Footer.jsx";
import { Outlet } from 'react-router-dom';

function AppLayout() {

    return (
        <div className="App">
            <NavBar/>
            <Outlet />
            <Footer/>
        </div>
    )
}

export default AppLayout;