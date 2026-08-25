import '../../styles/Search.css';

const Search = ({productoBuscado, alBuscarProducto}) => {
    return(
        <section className="searchContainer">
            <input type="text"
                   id="search-input"
                   name="search"
                   placeholder="Buscar"
                   className="searchBar"
                   value={productoBuscado ?? ""}
                   onChange={(evento) => alBuscarProducto(evento.target.value)}/>
        </section>
    )
}

export default Search;