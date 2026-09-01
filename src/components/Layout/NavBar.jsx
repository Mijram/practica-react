import '../../styles/Layout/NavBar.css';
import Search from "./Search.jsx";
import logo from "/src/assets/logo.svg"
import {Link} from "react-router-dom";


export default function NavBar(){

    return(
        <header>
            <nav>
                <a href="/" className="logo">
                    <img src={logo} alt="Marketplace" />
                </a>
                <Link className="carritoButton" to={"/carrito"}>
                     carrito
                </Link>
            </nav>
        </header>
    )
}