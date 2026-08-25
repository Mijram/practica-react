export const formatPrecio = (valor) => {
    return new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP',
        minimumFractionDigits: 0 // Cambia a 2 si manejas centavos
    }).format(valor);
};