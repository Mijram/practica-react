import '../../styles/Layout/Search.css';

const Search = () => {
    return(
        <section className="searchContainer">
            <input type="text"
                   id="search-input"
                   name="search"
                   placeholder="Buscar"
                   className="searchBar"/>
        </section>
    )
}

export default Search;