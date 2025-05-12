USE master;
GO

DROP DATABASE IF EXISTS dbAgro;
GO

-- Crear la base de datos nuevamente
CREATE DATABASE dbAgro;
GO

USE dbAgro;
GO

-- Crear tabla de clientes
CREATE TABLE client (
    id INT IDENTITY NOT NULL,
    name VARCHAR(50) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    birthday_date DATE NOT NULL,
    document_type CHAR(3) NOT NULL,
    number_document VARCHAR(15) NOT NULL,
    cell_number CHAR(9) NOT NULL,
    email VARCHAR(100) NOT NULL,
    client_type CHAR(2) NOT NULL,
    address VARCHAR(100) NOT NULL,
    registration_date DATE NOT NULL,
    state CHAR(1) NOT NULL,
    CONSTRAINT client_pk PRIMARY KEY (id)
);
GO

--	Revisamos la estructura de la tabla orders
exec sp_help 'dbo.client'
GO

-- Listamos los PKs de la base de datos en uso
SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
WHERE CONSTRAINT_TYPE = 'PRIMARY KEY'
GO

/* CONSTRAINT DE INTEGRIDAD DE DATOS -> UNIQUE */

--  Listar restricciones de una tabla
SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
WHERE TABLE_NAME = 'client'
GO

-->  Crear constraint UNIQUE en una tabla existente

ALTER TABLE client
	ADD CONSTRAINT uq_cell_number_client 
	UNIQUE (cell_number)
GO

ALTER TABLE client
	ADD CONSTRAINT uq_number_document_client 
	UNIQUE (number_document)
GO

ALTER TABLE client
	ADD CONSTRAINT uq_email_client 
	UNIQUE (email)
GO

--  4. Listar restricciones de tabla client
SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
WHERE TABLE_NAME = 'client'
GO


/* CONSTRAINT DE INTEGRIDAD DE DATOS -> DEFAULT */

-- Inserción registro con fecha ingresada manualmente de registro
SET DATEFORMAT dmy;

--  Agregamos campo default sea la fecha del sistema al momento de registro
ALTER TABLE client
	ADD CONSTRAINT df_registration_date
	DEFAULT getdate() FOR registration_date
GO

ALTER TABLE client
	ADD CONSTRAINT df_state
	DEFAULT getdate() FOR state
GO

-- Exploramos la estructura de la tabla client
exec sp_help 'dbo.client'
GO

/* CONSTRAINT DE INTEGRIDAD DE DATOS -> CHECK */

-- Validar que el nombre solo contenga letras y espacios
ALTER TABLE client
ADD CONSTRAINT chk_name
CHECK (name LIKE '%[A-Za-záéíóúÁÉÍÓÚüÜ ]%' AND LEN(name) >= 2);
GO


-- Validar que el apellido solo contenga letras y espacios
ALTER TABLE client
ADD CONSTRAINT chk_last_name
CHECK (last_name LIKE '%[A-Za-záéíóúÁÉÍÓÚüÜ ]%' AND LEN(last_name) >= 2);
GO

-- Validar que la fecha de nacimiento sea menor a la fecha actual
ALTER TABLE client
ADD CONSTRAINT chk_birthday_date
CHECK (birthday_date < GETDATE());
GO

-- Validar tipo de documento: 3 caracteres y solo letras (DNI, CNE)
ALTER TABLE client
ADD CONSTRAINT chk_document_type
CHECK (document_type IN ('DNI', 'CNE'));
GO

-- Validar número de documento con mínimo 8 caracteres (ajustar según tu lógica)
ALTER TABLE client
ADD CONSTRAINT chk_number_document
CHECK (
    (document_type = 'DNI' AND LEN(number_document) = 8) OR
    (document_type = 'CNE' AND LEN(number_document) = 15)
);
GO

-- Validar que el celular tenga 9 dígitos numéricos
ALTER TABLE client
ADD CONSTRAINT chk_cell_number
CHECK (cell_number LIKE '[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]');
GO

-- Validar que el correo tenga estructura simple válida
ALTER TABLE client
ADD CONSTRAINT chk_email
CHECK (email LIKE '%@%.%');
GO

-- Validar tipo de cliente (ejemplo: 'MI' = minorista, 'MA' = mayorista)
ALTER TABLE client
ADD CONSTRAINT chk_client_type
CHECK (client_type IN ('MI', 'MA'));
GO

-- Validar que el estado sea activo o inactivo (ejemplo: A = activo, I = inactivo)
ALTER TABLE client
ADD CONSTRAINT chk_state
CHECK (state IN ('A', 'I'));
GO


-- Listar restricciones de tipo CHECK
SELECT * FROM sys.check_constraints
GO

/* ELIMINACIÓN DE RESTRICCIONES */

-- Eliminamos la restricción CHK_CANT_DIG_RUC
ALTER TABLE client
	DROP CONSTRAINT chk_state
GO

-- Crear tabla de inventario
CREATE TABLE inventory (
    id INT IDENTITY NOT NULL,
    products_id INT NOT NULL,
    quantity_available INT NOT NULL,
	batch_number INT NOT NULL,
	description VARCHAR(100) NOT NULL,
	last_updated DATE NOT NULL,
    CONSTRAINT inventory_pk PRIMARY KEY (id)
);
GO

-- Crear detalle del pedido
CREATE TABLE order_detail (
    id INT IDENTITY NOT NULL,
    products_id INT NOT NULL,
    orders_id INT NOT NULL,
    unit INT NOT NULL,
    order_total DECIMAL(6,2) NOT NULL,
    CONSTRAINT order_detail_pk PRIMARY KEY (id)
);
GO

-- Crear tabla de pedidos
CREATE TABLE orders (
    id INT IDENTITY NOT NULL,
    client_id INT NOT NULL,
    user_id INT NOT NULL,
    order_date DATE NOT NULL,
    CONSTRAINT orders_pk PRIMARY KEY (id)
);
GO

/* CONSTRAINT DE INTEGRIDAD REFERENCIAL - FOREIGN KEY sobre tablas existentes */

--  1. Listar tablas existentes en la base de datos
SELECT * FROM sys.tables
GO

--  2. Establecer la relación entre la tabla pedido y la tabla cliente
ALTER TABLE orders
	ADD CONSTRAINT fk_client_orders
	FOREIGN KEY (client_id)
	REFERENCES client (id)-- ON UPDATE CASCADE / ON DELETE CASCADE
	ON UPDATE CASCADE
	ON DELETE CASCADE;
GO
-----------
--  3. Verificar relación entre client & country
SELECT 
	fk.name [Constraint],
    OBJECT_NAME(fk.parent_object_id) [Tabla],
    COL_NAME(fc.parent_object_id,fc.parent_column_id) [Columna],
    OBJECT_NAME (fk.referenced_object_id) AS [Tabla base],
    COL_NAME(fc.referenced_object_id, fc.referenced_column_id) AS [Columna de tabla base (PK)]
FROM 
	sys.foreign_keys fk
	INNER JOIN sys.foreign_key_columns fc ON (fk.OBJECT_ID = fc.constraint_object_id)
WHERE
	(OBJECT_NAME(fk.parent_object_id) = 'orders')
GO

-- Crear tabla de productos
CREATE TABLE products (
    id int IDENTITY NOT NULL,
    name VARCHAR(50) NOT NULL,
	brand VARCHAR(100)NOT NULL,
    categories CHAR(2) NOT NULL,
    unit_measure CHAR(1) NOT NULL,
	unit_price DECIMAL(6,2)NOT NULL,
	expiration_date DATE NOT NULL,
	state CHAR(1)NOT NULL,
    CONSTRAINT products_pk PRIMARY KEY (id)
);
GO

-- Asegurar que el precio no sea negativo
ALTER TABLE products
ADD CONSTRAINT chk_products_unit_price CHECK (unit_price > 0);
GO

-- Validar que la fecha de vencimiento sea posterior a la fecha actual
ALTER TABLE products
ADD CONSTRAINT chk_products_expiration CHECK (expiration_date > GETDATE());
GO

-- Restringir los valores posibles para la categoría (por ejemplo: Fungicidas (FU), insecticidas (IN), foliares (FO), semillas (SE))
ALTER TABLE products
ADD CONSTRAINT chk_products_category CHECK (categories IN ('FU', 'IN', 'FO', 'SE'));
GO

-- Restringir los valores posibles para la unidad de medida (K = kilo, L = litro)
ALTER TABLE products
ADD CONSTRAINT chk_products_unit_measure CHECK (unit_measure IN ('K', 'L'));
GO

-- Valor por defecto: 'A' (Activo)
ALTER TABLE products
ADD CONSTRAINT df_products_state DEFAULT 'A' FOR state;
GO

-- Crear productos del proveedor
CREATE TABLE supplier_products (
    id INT IDENTITY NOT NULL,
    products_id INT NOT NULL,
    suppliers_id INT NOT NULL,
    price DECIMAL(6,2) NOT NULL,
    lead_time_days INT NOT NULL,
    state CHAR(1) NOT NULL,
    CONSTRAINT supplier_products_pk PRIMARY KEY (id)
);
GO

-- Crear proveedores
CREATE TABLE suppliers (
    id INT IDENTITY NOT NULL,
    company_name VARCHAR(100) NOT NULL,
    ruc CHAR(11) NOT NULL,
    address VARCHAR(100) NOT NULL,
    gmail VARCHAR(100) NOT NULL,
    cell_number CHAR(9) NOT NULL,
    state CHAR(1) NOT NULL,
    CONSTRAINT suppliers_pk PRIMARY KEY (id)
);
GO

-- Crear tabla de usuarios
CREATE TABLE "user" (
    id INT IDENTITY NOT NULL,
    name VARCHAR(50) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    document_type CHAR(3) NOT NULL,
    number_document VARCHAR(15) NOT NULL,
    cell_number CHAR(9) NOT NULL,
    gmail VARCHAR(100) NOT NULL,
    date DATE NOT NULL,
    state CHAR(1) NOT NULL,
    CONSTRAINT user_pk PRIMARY KEY (id)
);
GO

-- Agregar llaves foráneas para relacionar tablas
ALTER TABLE order_detail ADD CONSTRAINT detalle_pedido_orders
    FOREIGN KEY (orders_id)
    REFERENCES orders (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE;
GO

ALTER TABLE inventory ADD CONSTRAINT inventory_products
    FOREIGN KEY (products_id)
    REFERENCES products (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE;
GO

ALTER TABLE order_detail ADD CONSTRAINT order_detail_products
    FOREIGN KEY (products_id)
    REFERENCES products (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE;
GO

ALTER TABLE orders ADD CONSTRAINT orders_user
    FOREIGN KEY (user_id)
    REFERENCES "user" (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE;
GO


ALTER TABLE supplier_products ADD CONSTRAINT supplier_products_products
    FOREIGN KEY (products_id)
    REFERENCES products (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE;
GO

ALTER TABLE supplier_products ADD CONSTRAINT supplier_products_suppliers
    FOREIGN KEY (suppliers_id)
    REFERENCES suppliers (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE;
GO
