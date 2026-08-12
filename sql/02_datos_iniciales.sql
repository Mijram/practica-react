-- =====================================================================
--  CURSO JPA / HIBERNATE — DATOS INICIALES
-- =====================================================================
--  Ejecutalo DESPUES de 01_crear_base_datos.sql.
--      psql -d tienda_jpa -f sql/02_datos_iniciales.sql
--
--  Para volver a empezar de cero en cualquier momento, ejecuta los dos
--  archivos en orden: el primero borra las tablas y las recrea.
--
--  Nota: las tablas usan GENERATED ALWAYS AS IDENTITY, asi que la
--  columna id NO se menciona en los INSERT. Es PostgreSQL quien la
--  asigna, igual que hara Hibernate cuando la aplicacion inserte.
-- =====================================================================


-- categorias (8 filas)
INSERT INTO categorias (nombre, descripcion, activa) VALUES
    ('Portatiles', 'Computadores portatiles y ultrabooks', TRUE),
    ('Perifericos', 'Teclados, ratones y accesorios', TRUE),
    ('Monitores', 'Pantallas y soportes', TRUE),
    ('Audio', 'Audifonos, microfonos y parlantes', TRUE),
    ('Almacenamiento', 'Discos duros, SSD y memorias', TRUE),
    ('Redes', 'Routers, switches y cableado', TRUE),
    ('Sillas', 'Mobiliario de oficina', TRUE),
    ('Impresion', 'Impresoras y consumibles', FALSE);

-- proveedores (7 filas)
INSERT INTO proveedores (nombre, pais, email, dias_entrega, activo) VALUES
    ('Andina Tech S.A.S.', 'Colombia', 'ventas@andinatech.co', 5, TRUE),
    ('Shenzhen Global Parts', 'China', 'export@szglobal.cn', 28, TRUE),
    ('Iberia Componentes', 'Espana', 'pedidos@iberiacomp.es', 14, TRUE),
    ('Pacifico Import Ltda.', 'Chile', 'contacto@pacificoimp.cl', 10, TRUE),
    ('NorthBridge Supply', 'Mexico', 'sales@northbridge.mx', 8, TRUE),
    ('Deltec Distribuciones', 'Colombia', NULL, 4, TRUE),
    ('Vieja Guardia Suministros', 'Argentina', 'info@viejaguardia.ar', 21, FALSE);

-- productos (33 filas)
--  categoria_id y proveedor_id se resuelven por nombre con subconsultas,
--  para no depender de los IDs que asigne la secuencia.
INSERT INTO productos (nombre, descripcion, precio, stock, activo, fecha_alta, categoria_id, proveedor_id) VALUES
    ('Portatil Nexus 14 i5', 'Portatil de gama media para oficina', 3200000, 24, TRUE, '2024-01-15', (SELECT id FROM categorias WHERE nombre = 'Portatiles'), (SELECT id FROM proveedores WHERE nombre = 'Andina Tech S.A.S.')),
    ('Portatil Nexus 15 i7', 'Portatil de alto rendimiento', 4800000, 11, TRUE, '2024-01-15', (SELECT id FROM categorias WHERE nombre = 'Portatiles'), (SELECT id FROM proveedores WHERE nombre = 'Andina Tech S.A.S.')),
    ('Ultrabook Aire 13', 'Ultraligero de 1.1 kg', 5600000, 6, TRUE, '2024-02-01', (SELECT id FROM categorias WHERE nombre = 'Portatiles'), (SELECT id FROM proveedores WHERE nombre = 'Shenzhen Global Parts')),
    ('Portatil Gama Basica 14', 'Equipo de entrada para estudio', 1900000, 40, TRUE, '2024-02-10', (SELECT id FROM categorias WHERE nombre = 'Portatiles'), (SELECT id FROM proveedores WHERE nombre = 'Pacifico Import Ltda.')),
    ('Portatil Titan Gamer 16', 'Portatil gamer con grafica dedicada', 8900000, 3, TRUE, '2024-03-05', (SELECT id FROM categorias WHERE nombre = 'Portatiles'), (SELECT id FROM proveedores WHERE nombre = 'Shenzhen Global Parts')),
    ('Portatil Nexus 13 (2021)', 'Modelo anterior, sin reposicion', 2400000, 8, FALSE, '2023-06-01', (SELECT id FROM categorias WHERE nombre = 'Portatiles'), (SELECT id FROM proveedores WHERE nombre = 'Andina Tech S.A.S.')),
    ('Teclado Mecanico RGB 87', 'Teclado mecanico compacto', 280000, 65, TRUE, '2024-01-20', (SELECT id FROM categorias WHERE nombre = 'Perifericos'), (SELECT id FROM proveedores WHERE nombre = 'Shenzhen Global Parts')),
    ('Teclado Inalambrico Slim', 'Teclado bluetooth de perfil bajo', 145000, 90, TRUE, '2024-01-20', (SELECT id FROM categorias WHERE nombre = 'Perifericos'), NULL),
    ('Mouse Ergonomico Vertical', 'Mouse vertical para evitar tension', 135000, 52, TRUE, '2024-02-14', (SELECT id FROM categorias WHERE nombre = 'Perifericos'), NULL),
    ('Mouse Gamer 16000 DPI', 'Mouse de alta precision', 210000, 37, TRUE, '2024-02-14', (SELECT id FROM categorias WHERE nombre = 'Perifericos'), (SELECT id FROM proveedores WHERE nombre = 'Shenzhen Global Parts')),
    ('Pad Escritorio XL', 'Alfombrilla extendida', 55000, 120, TRUE, '2024-03-01', (SELECT id FROM categorias WHERE nombre = 'Perifericos'), (SELECT id FROM proveedores WHERE nombre = 'Deltec Distribuciones')),
    ('Teclado Membrana Basico', 'Modelo descontinuado', 45000, 0, FALSE, '2023-05-10', (SELECT id FROM categorias WHERE nombre = 'Perifericos'), (SELECT id FROM proveedores WHERE nombre = 'Pacifico Import Ltda.')),
    ('Monitor 24 IPS 75Hz', 'Monitor Full HD para oficina', 680000, 30, TRUE, '2024-01-25', (SELECT id FROM categorias WHERE nombre = 'Monitores'), (SELECT id FROM proveedores WHERE nombre = 'Iberia Componentes')),
    ('Monitor 27 QHD 165Hz', 'Monitor gaming QHD', 1450000, 14, TRUE, '2024-02-20', (SELECT id FROM categorias WHERE nombre = 'Monitores'), (SELECT id FROM proveedores WHERE nombre = 'Iberia Componentes')),
    ('Monitor 32 4K Profesional', 'Monitor 4K con calibracion', 2800000, 5, TRUE, '2024-04-01', (SELECT id FROM categorias WHERE nombre = 'Monitores'), (SELECT id FROM proveedores WHERE nombre = 'NorthBridge Supply')),
    ('Soporte Doble Monitor', 'Brazo articulado para dos pantallas', 190000, 28, TRUE, '2024-04-01', (SELECT id FROM categorias WHERE nombre = 'Monitores'), (SELECT id FROM proveedores WHERE nombre = 'Deltec Distribuciones')),
    ('Audifonos Over-Ear ANC', 'Cancelacion activa de ruido', 620000, 22, TRUE, '2024-02-05', (SELECT id FROM categorias WHERE nombre = 'Audio'), NULL),
    ('Audifonos In-Ear BT', 'Audifonos inalambricos compactos', 180000, 74, TRUE, '2024-02-05', (SELECT id FROM categorias WHERE nombre = 'Audio'), (SELECT id FROM proveedores WHERE nombre = 'Shenzhen Global Parts')),
    ('Microfono USB Cardioide', 'Microfono para streaming', 390000, 18, TRUE, '2024-03-15', (SELECT id FROM categorias WHERE nombre = 'Audio'), (SELECT id FROM proveedores WHERE nombre = 'NorthBridge Supply')),
    ('Parlante Bluetooth 20W', 'Parlante portatil resistente al agua', 240000, 33, TRUE, '2024-03-15', (SELECT id FROM categorias WHERE nombre = 'Audio'), (SELECT id FROM proveedores WHERE nombre = 'Pacifico Import Ltda.')),
    ('SSD NVMe 1TB', 'Unidad de estado solido PCIe 4.0', 480000, 45, TRUE, '2024-01-30', (SELECT id FROM categorias WHERE nombre = 'Almacenamiento'), (SELECT id FROM proveedores WHERE nombre = 'Shenzhen Global Parts')),
    ('SSD NVMe 2TB', 'Unidad de estado solido de alta capacidad', 890000, 19, TRUE, '2024-01-30', (SELECT id FROM categorias WHERE nombre = 'Almacenamiento'), (SELECT id FROM proveedores WHERE nombre = 'Shenzhen Global Parts')),
    ('Disco Duro Externo 4TB', 'Disco externo USB 3.2', 520000, 26, TRUE, '2024-02-28', (SELECT id FROM categorias WHERE nombre = 'Almacenamiento'), (SELECT id FROM proveedores WHERE nombre = 'NorthBridge Supply')),
    ('Memoria USB 128GB', 'Memoria flash USB 3.0', 65000, 150, TRUE, '2024-02-28', (SELECT id FROM categorias WHERE nombre = 'Almacenamiento'), (SELECT id FROM proveedores WHERE nombre = 'Deltec Distribuciones')),
    ('Tarjeta MicroSD 256GB', 'MicroSD clase 10 para camaras', 110000, 88, TRUE, '2024-03-20', (SELECT id FROM categorias WHERE nombre = 'Almacenamiento'), (SELECT id FROM proveedores WHERE nombre = 'Shenzhen Global Parts')),
    ('Router WiFi 6 Doble Banda', 'Router AX1800', 430000, 21, TRUE, '2024-04-10', (SELECT id FROM categorias WHERE nombre = 'Redes'), (SELECT id FROM proveedores WHERE nombre = 'NorthBridge Supply')),
    ('Switch 8 Puertos Gigabit', 'Switch no administrable', 195000, 34, TRUE, '2024-04-10', (SELECT id FROM categorias WHERE nombre = 'Redes'), (SELECT id FROM proveedores WHERE nombre = 'Iberia Componentes')),
    ('Cable Red Cat6 5m', 'Cable de red categoria 6', 28000, 200, TRUE, '2024-04-10', (SELECT id FROM categorias WHERE nombre = 'Redes'), (SELECT id FROM proveedores WHERE nombre = 'Deltec Distribuciones')),
    ('Silla Ergonomica Malla', 'Silla con soporte lumbar', 890000, 16, TRUE, '2024-05-02', (SELECT id FROM categorias WHERE nombre = 'Sillas'), (SELECT id FROM proveedores WHERE nombre = 'Andina Tech S.A.S.')),
    ('Silla Gerencial Cuero', 'Silla ejecutiva reclinable', 1650000, 7, TRUE, '2024-05-02', (SELECT id FROM categorias WHERE nombre = 'Sillas'), (SELECT id FROM proveedores WHERE nombre = 'Andina Tech S.A.S.')),
    ('Reposapies Ajustable', 'Reposapies con inclinacion', 95000, 41, TRUE, '2024-05-02', (SELECT id FROM categorias WHERE nombre = 'Sillas'), (SELECT id FROM proveedores WHERE nombre = 'Deltec Distribuciones')),
    ('Impresora Laser Mono', 'Impresora laser monocromatica', 740000, 12, TRUE, '2024-06-01', (SELECT id FROM categorias WHERE nombre = 'Impresion'), (SELECT id FROM proveedores WHERE nombre = 'Iberia Componentes')),
    ('Toner Compatible Negro', 'Toner generico de alto rendimiento', 95000, 60, TRUE, '2024-06-01', (SELECT id FROM categorias WHERE nombre = 'Impresion'), NULL);

-- usuarios (17 filas)
INSERT INTO usuarios (nombre, apellido, email, ciudad, fecha_registro, activo) VALUES
    ('Ana', 'Torres', 'ana.torres@correo.com', 'Bogota', '2024-01-15', TRUE),
    ('Luis', 'Perez', 'luis.perez@correo.com', 'Medellin', '2024-02-03', TRUE),
    ('Sofia', 'Gomez', 'sofia.gomez@empresa.com', 'Bogota', '2024-02-20', TRUE),
    ('Mateo', 'Rios', 'mateo.rios@correo.com', 'Cali', '2024-04-11', TRUE),
    ('Valentina', 'Cruz', 'valentina.cruz@correo.com', 'Barranquilla', '2024-05-30', TRUE),
    ('Javier', 'Moreno', 'javier.moreno@correo.com', 'Lima', '2024-06-18', TRUE),
    ('Camila', 'Herrera', 'camila.herrera@empresa.com', 'Santiago', '2024-07-22', TRUE),
    ('Daniel', 'Suarez', 'daniel.suarez@correo.com', 'Quito', '2024-09-05', TRUE),
    ('Isabella', 'Castro', 'isabella.castro@correo.com', 'Bogota', '2024-10-14', TRUE),
    ('Sebastian', 'Pena', 'sebastian.pena@correo.com', 'Medellin', '2024-11-27', TRUE),
    ('Laura', 'Jimenez', 'laura.jimenez@empresa.com', 'Ciudad de Mexico', '2025-01-09', TRUE),
    ('Nicolas', 'Ruiz', 'nicolas.ruiz@correo.com', 'Bogota', '2025-02-16', TRUE),
    ('Mariana', 'Delgado', 'mariana.delgado@correo.com', 'Cali', '2025-03-21', TRUE),
    ('Emilio', 'Navarro', 'emilio.navarro@correo.com', 'Buenos Aires', '2025-04-08', FALSE),
    ('Antonia', 'Vega', 'antonia.vega@empresa.com', 'Bogota', '2025-05-19', TRUE),
    ('Olivia', 'Cordoba', 'olivia.cordoba@correo.com', 'Pereira', '2025-06-01', TRUE),
    ('Hector', 'Zambrano', 'hector.zambrano@correo.com', 'Manizales', '2025-06-20', TRUE);

-- pedidos (79 filas)
INSERT INTO pedidos (fecha, estado, direccion, costo_envio, usuario_id) VALUES
    ('2024-01-06', 'ENVIADO', 'Calle 134 # 3-11, Medellin', 0, (SELECT id FROM usuarios WHERE email = 'sebastian.pena@correo.com')),
    ('2024-01-18', 'ENTREGADO', 'Calle 139 # 45-41, Barranquilla', 15000, (SELECT id FROM usuarios WHERE email = 'valentina.cruz@correo.com')),
    ('2024-02-12', 'CANCELADO', 'Calle 126 # 2-26, Barranquilla', 12000, (SELECT id FROM usuarios WHERE email = 'valentina.cruz@correo.com')),
    ('2024-02-15', 'ENTREGADO', 'Calle 18 # 44-90, Bogota', 12000, (SELECT id FROM usuarios WHERE email = 'isabella.castro@correo.com')),
    ('2024-02-15', 'CANCELADO', 'Calle 23 # 41-94, Bogota', 0, (SELECT id FROM usuarios WHERE email = 'antonia.vega@empresa.com')),
    ('2024-02-16', 'ENTREGADO', 'Calle 76 # 12-67, Cali', 15000, (SELECT id FROM usuarios WHERE email = 'mariana.delgado@correo.com')),
    ('2024-02-21', 'ENTREGADO', 'Calle 99 # 30-77, Quito', 18000, (SELECT id FROM usuarios WHERE email = 'daniel.suarez@correo.com')),
    ('2024-02-21', 'PENDIENTE', 'Calle 4 # 28-30, Buenos Aires', 25000, (SELECT id FROM usuarios WHERE email = 'emilio.navarro@correo.com')),
    ('2024-02-24', 'ENVIADO', 'Calle 57 # 29-36, Lima', 15000, (SELECT id FROM usuarios WHERE email = 'javier.moreno@correo.com')),
    ('2024-02-28', 'CANCELADO', 'Calle 67 # 27-28, Medellin', 12000, (SELECT id FROM usuarios WHERE email = 'sebastian.pena@correo.com')),
    ('2024-03-03', 'PAGADO', 'Calle 54 # 10-40, Bogota', 12000, (SELECT id FROM usuarios WHERE email = 'sofia.gomez@empresa.com')),
    ('2024-03-26', 'PENDIENTE', 'Calle 131 # 47-35, Bogota', 25000, (SELECT id FROM usuarios WHERE email = 'nicolas.ruiz@correo.com')),
    ('2024-03-27', 'ENVIADO', 'Calle 121 # 18-88, Quito', 0, (SELECT id FROM usuarios WHERE email = 'daniel.suarez@correo.com')),
    ('2024-03-27', 'ENTREGADO', 'Calle 123 # 80-72, Cali', 0, (SELECT id FROM usuarios WHERE email = 'mariana.delgado@correo.com')),
    ('2024-04-07', 'ENTREGADO', 'Calle 46 # 58-36, Bogota', 0, (SELECT id FROM usuarios WHERE email = 'nicolas.ruiz@correo.com')),
    ('2024-04-08', 'PENDIENTE', 'Calle 58 # 70-67, Cali', 15000, (SELECT id FROM usuarios WHERE email = 'mariana.delgado@correo.com')),
    ('2024-04-17', 'ENTREGADO', 'Calle 97 # 61-70, Bogota', 0, (SELECT id FROM usuarios WHERE email = 'sofia.gomez@empresa.com')),
    ('2024-04-26', 'ENTREGADO', 'Calle 135 # 61-62, Cali', 25000, (SELECT id FROM usuarios WHERE email = 'mariana.delgado@correo.com')),
    ('2024-05-08', 'ENTREGADO', 'Calle 26 # 86-62, Bogota', 0, (SELECT id FROM usuarios WHERE email = 'nicolas.ruiz@correo.com')),
    ('2024-05-09', 'PAGADO', 'Calle 41 # 35-60, Buenos Aires', 18000, (SELECT id FROM usuarios WHERE email = 'emilio.navarro@correo.com')),
    ('2024-05-11', 'PAGADO', 'Calle 122 # 7-60, Ciudad de Mexico', 18000, (SELECT id FROM usuarios WHERE email = 'laura.jimenez@empresa.com')),
    ('2024-05-24', 'PENDIENTE', 'Calle 67 # 42-25, Bogota', 25000, (SELECT id FROM usuarios WHERE email = 'sofia.gomez@empresa.com')),
    ('2024-05-27', 'PAGADO', 'Calle 40 # 1-27, Barranquilla', 12000, (SELECT id FROM usuarios WHERE email = 'valentina.cruz@correo.com')),
    ('2024-06-03', 'PAGADO', 'Calle 95 # 63-34, Barranquilla', 18000, (SELECT id FROM usuarios WHERE email = 'valentina.cruz@correo.com')),
    ('2024-06-08', 'CANCELADO', 'Calle 129 # 86-44, Bogota', 25000, (SELECT id FROM usuarios WHERE email = 'antonia.vega@empresa.com')),
    ('2024-06-15', 'ENTREGADO', 'Calle 107 # 15-59, Ciudad de Mexico', 25000, (SELECT id FROM usuarios WHERE email = 'laura.jimenez@empresa.com')),
    ('2024-06-20', 'PAGADO', 'Calle 53 # 24-12, Quito', 0, (SELECT id FROM usuarios WHERE email = 'daniel.suarez@correo.com')),
    ('2024-07-06', 'CANCELADO', 'Calle 51 # 43-90, Ciudad de Mexico', 18000, (SELECT id FROM usuarios WHERE email = 'laura.jimenez@empresa.com')),
    ('2024-07-08', 'ENTREGADO', 'Calle 30 # 37-7, Bogota', 0, (SELECT id FROM usuarios WHERE email = 'ana.torres@correo.com')),
    ('2024-07-13', 'PAGADO', 'Calle 39 # 77-8, Lima', 15000, (SELECT id FROM usuarios WHERE email = 'javier.moreno@correo.com')),
    ('2024-07-15', 'ENVIADO', 'Calle 108 # 12-67, Medellin', 18000, (SELECT id FROM usuarios WHERE email = 'sebastian.pena@correo.com')),
    ('2024-07-18', 'ENVIADO', 'Calle 64 # 22-78, Lima', 12000, (SELECT id FROM usuarios WHERE email = 'javier.moreno@correo.com')),
    ('2024-08-01', 'ENTREGADO', 'Calle 102 # 45-62, Ciudad de Mexico', 25000, (SELECT id FROM usuarios WHERE email = 'laura.jimenez@empresa.com')),
    ('2024-09-14', 'ENTREGADO', 'Calle 127 # 82-60, Lima', 12000, (SELECT id FROM usuarios WHERE email = 'javier.moreno@correo.com')),
    ('2024-09-26', 'PAGADO', 'Calle 138 # 84-42, Medellin', 15000, (SELECT id FROM usuarios WHERE email = 'sebastian.pena@correo.com')),
    ('2024-10-21', 'CANCELADO', 'Calle 55 # 62-8, Ciudad de Mexico', 12000, (SELECT id FROM usuarios WHERE email = 'laura.jimenez@empresa.com')),
    ('2024-11-07', 'CANCELADO', 'Calle 113 # 50-93, Barranquilla', 12000, (SELECT id FROM usuarios WHERE email = 'valentina.cruz@correo.com')),
    ('2024-11-13', 'PAGADO', 'Calle 67 # 39-89, Medellin', 15000, (SELECT id FROM usuarios WHERE email = 'sebastian.pena@correo.com')),
    ('2024-11-20', 'PAGADO', 'Calle 50 # 56-98, Buenos Aires', 0, (SELECT id FROM usuarios WHERE email = 'emilio.navarro@correo.com')),
    ('2024-11-21', 'ENTREGADO', 'Calle 94 # 41-57, Bogota', 0, (SELECT id FROM usuarios WHERE email = 'sofia.gomez@empresa.com')),
    ('2024-11-28', 'PENDIENTE', 'Calle 122 # 5-22, Cali', 0, (SELECT id FROM usuarios WHERE email = 'mateo.rios@correo.com')),
    ('2024-12-01', 'ENTREGADO', 'Calle 72 # 33-18, Barranquilla', 18000, (SELECT id FROM usuarios WHERE email = 'valentina.cruz@correo.com')),
    ('2024-12-02', 'ENVIADO', 'Calle 64 # 50-9, Medellin', 25000, (SELECT id FROM usuarios WHERE email = 'sebastian.pena@correo.com')),
    ('2024-12-02', 'ENTREGADO', 'Calle 122 # 25-27, Bogota', 18000, (SELECT id FROM usuarios WHERE email = 'nicolas.ruiz@correo.com')),
    ('2024-12-06', 'ENTREGADO', 'Calle 90 # 53-63, Cali', 15000, (SELECT id FROM usuarios WHERE email = 'mariana.delgado@correo.com')),
    ('2024-12-18', 'ENTREGADO', 'Calle 57 # 24-41, Cali', 18000, (SELECT id FROM usuarios WHERE email = 'mateo.rios@correo.com')),
    ('2024-12-20', 'PENDIENTE', 'Calle 98 # 6-9, Bogota', 15000, (SELECT id FROM usuarios WHERE email = 'antonia.vega@empresa.com')),
    ('2024-12-24', 'ENVIADO', 'Calle 133 # 68-45, Medellin', 25000, (SELECT id FROM usuarios WHERE email = 'sebastian.pena@correo.com')),
    ('2024-12-28', 'PAGADO', 'Calle 108 # 72-65, Santiago', 12000, (SELECT id FROM usuarios WHERE email = 'camila.herrera@empresa.com')),
    ('2025-01-03', 'ENVIADO', 'Calle 116 # 60-65, Medellin', 25000, (SELECT id FROM usuarios WHERE email = 'luis.perez@correo.com')),
    ('2025-01-03', 'CANCELADO', 'Calle 26 # 79-81, Bogota', 12000, (SELECT id FROM usuarios WHERE email = 'isabella.castro@correo.com')),
    ('2025-01-07', 'ENTREGADO', 'Calle 134 # 24-20, Buenos Aires', 0, (SELECT id FROM usuarios WHERE email = 'emilio.navarro@correo.com')),
    ('2025-01-14', 'ENTREGADO', 'Calle 19 # 7-31, Bogota', 25000, (SELECT id FROM usuarios WHERE email = 'isabella.castro@correo.com')),
    ('2025-01-20', 'CANCELADO', 'Calle 116 # 52-61, Ciudad de Mexico', 0, (SELECT id FROM usuarios WHERE email = 'laura.jimenez@empresa.com')),
    ('2025-01-22', 'ENTREGADO', 'Calle 70 # 48-20, Cali', 25000, (SELECT id FROM usuarios WHERE email = 'mateo.rios@correo.com')),
    ('2025-01-25', 'ENTREGADO', 'Calle 95 # 45-82, Bogota', 12000, (SELECT id FROM usuarios WHERE email = 'sofia.gomez@empresa.com')),
    ('2025-01-28', 'ENVIADO', 'Calle 60 # 28-6, Bogota', 25000, (SELECT id FROM usuarios WHERE email = 'ana.torres@correo.com')),
    ('2025-02-08', 'ENTREGADO', 'Calle 69 # 22-56, Ciudad de Mexico', 18000, (SELECT id FROM usuarios WHERE email = 'laura.jimenez@empresa.com')),
    ('2025-02-09', 'ENTREGADO', 'Calle 45 # 46-34, Medellin', 0, (SELECT id FROM usuarios WHERE email = 'luis.perez@correo.com')),
    ('2025-02-17', 'PAGADO', 'Calle 53 # 63-19, Medellin', 0, (SELECT id FROM usuarios WHERE email = 'luis.perez@correo.com')),
    ('2025-02-21', 'PAGADO', 'Calle 18 # 82-21, Bogota', 15000, (SELECT id FROM usuarios WHERE email = 'nicolas.ruiz@correo.com')),
    ('2025-02-26', 'ENVIADO', 'Calle 93 # 5-90, Bogota', 0, (SELECT id FROM usuarios WHERE email = 'nicolas.ruiz@correo.com')),
    ('2025-03-12', 'PAGADO', 'Calle 52 # 69-62, Cali', 15000, (SELECT id FROM usuarios WHERE email = 'mariana.delgado@correo.com')),
    ('2025-03-23', 'PAGADO', 'Calle 136 # 11-48, Cali', 0, (SELECT id FROM usuarios WHERE email = 'mateo.rios@correo.com')),
    ('2025-03-24', 'ENTREGADO', 'Calle 40 # 88-45, Ciudad de Mexico', 0, (SELECT id FROM usuarios WHERE email = 'laura.jimenez@empresa.com')),
    ('2025-03-27', 'ENTREGADO', 'Calle 75 # 84-87, Bogota', 0, (SELECT id FROM usuarios WHERE email = 'sofia.gomez@empresa.com')),
    ('2025-04-06', 'ENVIADO', 'Calle 68 # 57-13, Ciudad de Mexico', 15000, (SELECT id FROM usuarios WHERE email = 'laura.jimenez@empresa.com')),
    ('2025-04-12', 'PENDIENTE', 'Calle 10 # 87-60, Bogota', 25000, (SELECT id FROM usuarios WHERE email = 'ana.torres@correo.com')),
    ('2025-04-15', 'ENTREGADO', 'Calle 12 # 22-43, Bogota', 12000, (SELECT id FROM usuarios WHERE email = 'ana.torres@correo.com')),
    ('2025-04-22', 'ENTREGADO', 'Calle 77 # 56-82, Bogota', 18000, (SELECT id FROM usuarios WHERE email = 'antonia.vega@empresa.com')),
    ('2025-04-24', 'CANCELADO', 'Calle 44 # 89-27, Cali', 18000, (SELECT id FROM usuarios WHERE email = 'mariana.delgado@correo.com')),
    ('2025-05-23', 'PAGADO', 'Calle 85 # 30-54, Cali', 0, (SELECT id FROM usuarios WHERE email = 'mariana.delgado@correo.com')),
    ('2025-05-25', 'PAGADO', 'Calle 134 # 64-85, Santiago', 0, (SELECT id FROM usuarios WHERE email = 'camila.herrera@empresa.com')),
    ('2025-06-01', 'ENVIADO', 'Calle 115 # 13-50, Cali', 12000, (SELECT id FROM usuarios WHERE email = 'mariana.delgado@correo.com')),
    ('2025-06-04', 'ENTREGADO', 'Calle 57 # 64-69, Bogota', 18000, (SELECT id FROM usuarios WHERE email = 'isabella.castro@correo.com')),
    ('2025-06-05', 'ENTREGADO', 'Calle 134 # 48-93, Bogota', 0, (SELECT id FROM usuarios WHERE email = 'sofia.gomez@empresa.com')),
    ('2025-06-07', 'CANCELADO', 'Calle 3 # 87-8, Cali', 0, (SELECT id FROM usuarios WHERE email = 'mateo.rios@correo.com')),
    ('2025-06-12', 'PENDIENTE', 'Calle 130 # 26-21, Bogota', 12000, (SELECT id FROM usuarios WHERE email = 'antonia.vega@empresa.com')),
    ('2025-06-23', 'ENTREGADO', 'Calle 81 # 37-83, Lima', 0, (SELECT id FROM usuarios WHERE email = 'javier.moreno@correo.com'));

-- detalle_pedido
--  Se genera con un bloque anonimo: por cada pedido toma entre 1 y 4
--  productos activos al azar. Asi el detalle siempre es coherente con
--  los IDs reales que asigno la secuencia.
DO $$
DECLARE
    p          RECORD;
    prod       RECORD;
    n_lineas   INTEGER;
BEGIN
    FOR p IN SELECT id FROM pedidos ORDER BY id LOOP
        n_lineas := 1 + (p.id % 4);
        FOR prod IN
            SELECT id, precio FROM productos WHERE activo = TRUE
            ORDER BY (id * 7 + p.id * 13) % 31
            LIMIT n_lineas
        LOOP
            INSERT INTO detalle_pedido (pedido_id, producto_id, cantidad, precio_unitario)
            VALUES (p.id, prod.id, 1 + ((p.id + prod.id) % 5), prod.precio)
            ON CONFLICT DO NOTHING;
        END LOOP;
    END LOOP;
END $$;

-- etiquetas
INSERT INTO etiquetas (nombre, color) VALUES
    ('Oferta',      '#E53935'),
    ('Novedad',     '#1E88E5'),
    ('Mas vendido', '#43A047'),
    ('Ultimas unidades', '#FB8C00'),
    ('Gama alta',   '#8E24AA');

-- resenas: una muestra sobre productos activos
DO $$
DECLARE
    prod  RECORD;
    usr   RECORD;
    cal   INTEGER;
BEGIN
    FOR prod IN SELECT id FROM productos WHERE activo = TRUE ORDER BY id LIMIT 18 LOOP
        FOR usr IN SELECT id FROM usuarios ORDER BY (id * 5 + prod.id * 3) % 17 LIMIT 1 + (prod.id % 3) LOOP
            cal := 1 + ((prod.id + usr.id) % 5);
            INSERT INTO resenas (producto_id, usuario_id, calificacion, comentario, fecha)
            VALUES (prod.id, usr.id, cal,
                    CASE WHEN cal >= 4 THEN 'Muy buen producto, cumple lo prometido'
                         WHEN cal = 3  THEN 'Aceptable para el precio'
                         ELSE 'No funciono como esperaba' END,
                    DATE '2025-01-01' + (((prod.id * 7 + usr.id) % 180))::INTEGER)
            ON CONFLICT DO NOTHING;
        END LOOP;
    END LOOP;
END $$;

-- producto_etiqueta: relacion N:N de ejemplo
INSERT INTO producto_etiqueta (producto_id, etiqueta_id)
SELECT p.id, e.id
FROM   productos p
JOIN   etiquetas e ON e.nombre = CASE
           WHEN p.precio >= 3000000 THEN 'Gama alta'
           WHEN p.stock <= 8        THEN 'Ultimas unidades'
           WHEN p.stock >= 100      THEN 'Oferta'
           ELSE 'Novedad' END
WHERE  p.activo = TRUE
ON CONFLICT DO NOTHING;

-- ---------------------------------------------------------------------
--  COMPROBACION
-- ---------------------------------------------------------------------
SELECT 'categorias' AS tabla, COUNT(*) FROM categorias
UNION ALL SELECT 'proveedores', COUNT(*) FROM proveedores
UNION ALL SELECT 'productos',   COUNT(*) FROM productos
UNION ALL SELECT 'usuarios',    COUNT(*) FROM usuarios
UNION ALL SELECT 'pedidos',     COUNT(*) FROM pedidos
UNION ALL SELECT 'detalle_pedido', COUNT(*) FROM detalle_pedido
UNION ALL SELECT 'resenas',     COUNT(*) FROM resenas
UNION ALL SELECT 'etiquetas',   COUNT(*) FROM etiquetas
UNION ALL SELECT 'producto_etiqueta', COUNT(*) FROM producto_etiqueta
ORDER BY 1;
