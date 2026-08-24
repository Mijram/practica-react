import '../../styles/navBar.css';
import Search from "../Search/Search.jsx";
import logo from '../../assets/logo.png'


export default function NavBar(){
    return(
        <header>
            <nav>
                <a href="/" className="logo">
                    <img src={logo} alt="Marketplace" />
                </a>
                <Search/>
                <section className="categorias">
                    <a href="#Tegnologia">Tegnología</a>
                    <a href="#Ropa">Ropa</a>
                    <a href="#Electrodomesticos">Electrodomesticos</a>
                </section>
            </nav>
        </header>
    )
}