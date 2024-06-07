DO $$
	-- 6 produktow 3 kategorie 6 dostawcow 3 klientow 9 adresow
DECLARE
	addr_id1 bigint;
    addr_id2 bigint;
    addr_id3 bigint;
    addr_id4 bigint;
    addr_id5 bigint;
    addr_id6 bigint;
    addr_id7 bigint;
    addr_id8 bigint;
    addr_id9 bigint;

	work_id1 bigint;
    work_id2 bigint;
    work_id3 bigint;
    work_id4 bigint;

	supplier_id1 bigint;
    supplier_id2 bigint;
    supplier_id3 bigint;
    supplier_id4 bigint;
    supplier_id5 bigint;
    supplier_id6 bigint;

	cat1_id bigint;
    cat2_id bigint;
	cat3_id bigint;
   	
    prod1_id bigint;
    prod2_id bigint;
	prod3_id bigint;
    prod4_id bigint;
	prod5_id bigint;
    prod6_id bigint;

	cust1_id bigint;
	cust2_id bigint;
	cust3_id bigint;

	order1_id bigint;
	order2_id bigint;

	supply1_id bigint;
	supply2_id bigint;
	supply3_id bigint;

	yesterday_timestamp TIMESTAMP WITHOUT TIME ZONE;

BEGIN
	ALTER TABLE public.addresses
	ALTER COLUMN postal_code TYPE VARCHAR(10);

	ALTER TABLE public.addresses
	ALTER COLUMN house_nr TYPE VARCHAR(5);

	ALTER TABLE supplies ALTER COLUMN worker_id DROP NOT NULL;

	yesterday_timestamp := (CURRENT_TIMESTAMP - INTERVAL '1 day')::timestamp;	
	    -- 9 adresow 
	INSERT INTO addresses (postal_code, city, street, house_nr, country)
	VALUES ('10001', 'New York', '5th Avenue', '1', 'USA')
    RETURNING id INTO addr_id1;
	
	INSERT INTO addresses (postal_code, city, street, house_nr, country)
	VALUES ('WC2N 5DU', 'London', 'Trafalgar Square', '2', 'UK')
	RETURNING id INTO addr_id2;
	
	INSERT INTO addresses (postal_code, city, street, house_nr, country)
	VALUES ('75008', 'Paris', 'Avenue des Champs-Élysées', '3', 'France')
	RETURNING id INTO addr_id3;
	
	INSERT INTO addresses (postal_code, city, street, house_nr, country)
	VALUES ('10117', 'Berlin', 'Unter den Linden', '4', 'Germany')
	RETURNING id INTO addr_id4;
	
	INSERT INTO addresses (postal_code, city, street, house_nr, country)
	VALUES ('00187', 'Rome', 'Via Veneto', '5', 'Italy')
	RETURNING id INTO addr_id5;
	
	INSERT INTO addresses (postal_code, city, street, house_nr, country)
	VALUES ('104-0061', 'Tokyo', 'Ginza', '6', 'Japan')
	RETURNING id INTO addr_id6;
	
	INSERT INTO addresses (postal_code, city, street, house_nr, country)
	VALUES ('2000', 'Sydney', 'George Street', '7', 'Australia')
	RETURNING id INTO addr_id7;
	
	INSERT INTO addresses (postal_code, city, street, house_nr, country)
	VALUES ('110001', 'New Delhi', 'Connaught Place', '8', 'India')
	RETURNING id INTO addr_id8;
	
	INSERT INTO addresses (postal_code, city, street, house_nr, country)
	VALUES ('100000', 'Beijing', 'Wangfujing Street', '9', 'China')
	RETURNING id INTO addr_id9;

	INSERT INTO workers (login, name, last_name, position)
    VALUES ('24135', 'John', 'Doe', 'worker')
    RETURNING id INTO work_id1;

    INSERT INTO workers (login, name, last_name, position)
    VALUES ('89012', 'Jane', 'Smith', 'worker')
    RETURNING id INTO work_id2;

    INSERT INTO workers (login, name, last_name, position)
    VALUES ('01283', 'Michael', 'Johnson', 'worker')
    RETURNING id INTO work_id3;

    INSERT INTO workers (login, name, last_name, position)
    VALUES ('53745', 'Emily', 'Williams', 'worker')
    RETURNING id INTO work_id4;

	-- Dostawcy od Apple od telefonu
    INSERT INTO suppliers (name, address_id)
    VALUES ('Apple Supplier 1', addr_id1)
    RETURNING id INTO supplier_id1;
	-- Dostawca od laptopow
    INSERT INTO suppliers (name, address_id)
    VALUES ('Apple Supplier 2', addr_id2)
    RETURNING id INTO supplier_id2;

    -- Dostawcy od Samsunga od telefonow
    INSERT INTO suppliers (name, address_id)
    VALUES ('Samsung Supplier 1', addr_id3)
    RETURNING id INTO supplier_id3;
	-- Dostawca od lodowek
    INSERT INTO suppliers (name, address_id)
    VALUES ('Samsung Supplier 2', addr_id4)
    RETURNING id INTO supplier_id4;
    -- Inni dostawcy lodowek
    INSERT INTO suppliers (name, address_id)
    VALUES ('LG supplier', addr_id5)
    RETURNING id INTO supplier_id5;
	-- Dostawca od laptopw
    INSERT INTO suppliers (name, address_id)
    VALUES ('Lenovo', addr_id6)
    RETURNING id INTO supplier_id6;

	INSERT INTO categories (name)
    VALUES ('Phones')
    RETURNING id INTO cat1_id;

	INSERT INTO categories (name)
    VALUES ('Refrigerators')
    RETURNING id INTO cat2_id;

	INSERT INTO categories (name)
    VALUES ('Laptops')
    RETURNING id INTO cat3_id;

    INSERT INTO products (name, price, category_id, stock)
    VALUES ('IPhone 15 PRO', 4686.97, cat1_id, 100)
    RETURNING id INTO prod1_id;

	INSERT INTO products (name, price, category_id, stock)
    VALUES ('APPLE MacBook Pro 14 M3', 10399, cat3_id, 100)
    RETURNING id INTO prod2_id;

	INSERT INTO products (name, price, category_id, stock)
    VALUES ('SAMSUNG Galaxy S24 Ultra', 4722, cat1_id, 100)
    RETURNING id INTO prod3_id;

	INSERT INTO products (name, price, category_id, stock)
    VALUES ('SAMSUNG Grand+', 2699, cat2_id, 100)
    RETURNING id INTO prod4_id;

	INSERT INTO products (name, price, category_id, stock)
    VALUES ('LG GBV7280DEV 203cm ', 2959, cat2_id, 100)
    RETURNING id INTO prod5_id;

	INSERT INTO products (name, price, category_id, stock)
    VALUES ('LENOVO IdeaPad Slim 3-15 ', 2499, cat3_id, 100)
    RETURNING id INTO prod6_id;

	INSERT INTO customers (name, last_name, address_id, email)
	VALUES ('John', 'Doe', addr_id7, 'john.doe@example.com')
    RETURNING id INTO cust1_id;

	INSERT INTO customers (name, last_name, address_id, email)
	VALUES ('Alice', 'Smith', addr_id8, 'alice.smith@pw.edu.com')
	RETURNING id INTO cust2_id;

	INSERT INTO customers (name, last_name, address_id, email)
	VALUES ('Michael', 'Johnson', addr_id9, 'michael.johnson@gmail.com')
	RETURNING id INTO cust3_id;

	INSERT INTO orders (customer_id, date_processed, worker_id, state, date_received)
	VALUES (cust1_id, null, work_id2, 'received', CURRENT_TIMESTAMP)
	RETURNING id INTO order1_id;

	INSERT INTO orders (customer_id, date_processed, worker_id, state, date_received)
	VALUES (cust3_id, null, work_id1, 'received', yesterday_timestamp)
	RETURNING id INTO order2_id;

	INSERT INTO products_orders(product_id, order_id, amount)
	VALUES (prod2_id, order1_id, 5);

	INSERT INTO products_orders(product_id, order_id, amount)
	VALUES (prod6_id, order1_id, 10);

	INSERT INTO products_orders(product_id, order_id, amount)
	VALUES (prod1_id, order1_id, 6);

	INSERT INTO products_orders(product_id, order_id, amount)
	VALUES (prod4_id, order2_id, 2);

	INSERT INTO products_orders(product_id, order_id, amount)
	VALUES (prod5_id, order2_id, 3);

	UPDATE orders
	SET state = 'processed'
	WHERE id = order2_id;


	INSERT INTO supplies (supplier_id, worker_id, state, arrival_date, processed_date, expected_date, product_id, amount)
	VALUES (supplier_id1, work_id3, 'arrived', CURRENT_TIMESTAMP, null, CURRENT_TIMESTAMP, prod1_id, 20)
	RETURNING id INTO supply1_id;

	INSERT INTO supplies (supplier_id, worker_id, state, arrival_date, processed_date, expected_date, product_id, amount)
	VALUES (supplier_id2, null, 'underway', null, null, CURRENT_TIMESTAMP, prod2_id, 5)
	RETURNING id INTO supply2_id;

	INSERT INTO supplies (supplier_id, worker_id, state, arrival_date, processed_date, expected_date, product_id, amount)
	VALUES (supplier_id3, work_id4, 'arrived', CURRENT_TIMESTAMP, null, CURRENT_TIMESTAMP, prod3_id, 2)
	RETURNING id INTO supply3_id;

	UPDATE supplies
	SET state = 'processed'
	WHERE id = supply3_id;

END $$;
