-- Crear la tabla
-- Table: Products
CREATE TABLE Products (
    product_id int NOT NULL,
    name varchar(100) NOT NULL,
    description varchar(100) NOT NULL,
    unit_price decimal(8, 2) NOT NULL,
    stocks char(300) NOT NULL,
    category_id int NOT NULL,
    Detail_Sale_id_detail int NOT NULL,
);