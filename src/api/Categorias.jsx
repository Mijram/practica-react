const BASE_URL = "http://localhost:8080/api"

export async function Categorias (){
    try{
        console.log("realizando consulta");
        const consulta = await fetch(`${BASE_URL}/categorias`)
        console.log("consulta realizada");
        if (!consulta.ok){
            throw new Error("Error en la respuesta del servidor");
        }

        return await consulta.json();
    } catch (e) {
        console.log("No se pudieron obtener las categorias: ", e);
        throw e;
    }
}