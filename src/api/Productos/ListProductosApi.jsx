const BASE_URL = "http://localhost:8080/api"

export async function ListProductosApi (){
    try{
        const consulta = await fetch(`${BASE_URL}/productos`)
        if (!consulta.ok){
            throw new Error("Error en la respuesta del servidor");
        }

        return await consulta.json();
    } catch (e) {
        console.log("No se pudieron obtener los productos: ", e);
        throw e;
    }
}