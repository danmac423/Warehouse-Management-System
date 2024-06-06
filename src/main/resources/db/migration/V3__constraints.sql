ALTER TABLE products
ADD CONSTRAINT products_price_non_negative CHECK (price >= 0);

ALTER TABLE products
ADD CONSTRAINT products_stock_non_negative CHECK (stock >= 0);

ALTER TABLE supplies
ADD CONSTRAINT supplies_amount_non_negative CHECK (amount >= 0);

ALTER TABLE supplies_history
ADD CONSTRAINT supplies_history_amount_non_negative CHECK (amount >= 0);

ALTER TABLE products_orders
ADD CONSTRAINT products_orders_amount_non_negative CHECK (amount >= 0);

ALTER TABLE products_orders_history
ADD CONSTRAINT products_orders_history_amount_non_negative CHECK (amount >= 0);

ALTER TABLE products
ADD CONSTRAINT products_name_unique UNIQUE (name);

ALTER TABLE categories
ADD CONSTRAINT categories_name_unique UNIQUE (name);

ALTER TABLE customers
ADD CONSTRAINT customers_email_unique UNIQUE (email);

ALTER TABLE suppliers
ADD CONSTRAINT supplier_name_unique UNIQUE (name);
