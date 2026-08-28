const BASE_URL = "http://localhost:8080/api"

export async function ProductoIdApi (id){
    try{
        const consulta = await fetch(`${BASE_URL}/productos/${id}`)
        if (!consulta.ok){
            throw new Error("Error en la respuesta del servidor");
        }

        return await consulta.json();
    } catch (e) {
        console.log(`No se pudieron obtener el producto con id '${id}': `, e);
        throw e;
    }
}