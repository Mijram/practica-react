import '/src/App.css'
import NavBar from "./NavBar.jsx";
import Footer from "./Footer.jsx";
import { Outlet } from 'react-router-dom';
import '/src/App.css';

function AppLayout() {

    return (
        <div className="App">
            <NavBar/>
            <div className="content">
                <Outlet />
            </div>
            <Footer/>
        </div>
    )
}

export default AppLayout;